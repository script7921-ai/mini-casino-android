package com.just.casino

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.just.casino.core.Consts
import com.just.casino.core.UiKit
import com.just.casino.screens.ScreenManager
import java.util.concurrent.ConcurrentLinkedQueue

class GameSurfaceView(context: Context) : SurfaceView(context), SurfaceHolder.Callback, Runnable {

    private val manager: ScreenManager = ScreenManager(context)
    private val touches = ConcurrentLinkedQueue<Pair<Float, Float>>()

    private var thread: Thread? = null

    @Volatile
    private var running = false
    private val lock = Object()

    init {
        holder.addCallback(this)
        isFocusable = true
        UiKit.initDensity(resources.displayMetrics.scaledDensity)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        startThread()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Обязательное сохранение при уничтожении поверхности
        manager.save.save()
        stopThread()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        manager.layout(w.toFloat(), h.toFloat())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            // Мультитач игнорируется: action==ACTION_DOWN бывает только для первого указателя
            touches.add(Pair(event.getX(0), event.getY(0)))
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun startThread() {
        synchronized(lock) {
            if (thread != null) return
            running = true
            thread = Thread(this, "GameRenderThread").also { it.start() }
        }
    }

    private fun stopThread() {
        synchronized(lock) {
            running = false
            lock.notifyAll()
            thread?.let { t ->
                try {
                    t.join(1000)
                } catch (e: InterruptedException) {
                    // ignore
                }
            }
            thread = null
        }
    }

    fun pauseGame() {
        // Если идёт спин/вращение — немедленно дозавершить с просчитанным результатом
        manager.finishAnimations()
        manager.save.save()
        stopThread()
    }

    fun resumeGame() {
        if (holder.surface.isValid) {
            startThread()
        }
    }

    // Аппаратная кнопка «назад»: из игр/статистики в лобби; false = дефолт (выход)
    fun handleBack(): Boolean {
        return manager.back()
    }

    override fun run() {
        var last = System.currentTimeMillis()
        while (running) {
            val frameStart = System.currentTimeMillis()
            var dt = frameStart - last
            if (dt > Consts.MAX_DT_MS) dt = Consts.MAX_DT_MS
            last = frameStart
            update(dt)
            drawFrame()
            val sleep = Consts.FRAME_MS - (System.currentTimeMillis() - frameStart)
            if (sleep > 0) {
                synchronized(lock) {
                    try {
                        lock.wait(sleep)
                    } catch (e: InterruptedException) {
                        // ignore
                    }
                }
            }
        }
    }

    private fun update(dtMs: Long) {
        var t = touches.poll()
        while (t != null) {
            manager.touch(t.first, t.second)
            t = touches.poll()
        }
        manager.update(dtMs)
    }

    private fun drawFrame() {
        val h = holder ?: return
        var c: Canvas? = null
        try {
            c = h.lockCanvas() ?: return
            val s = manager.screen(manager.current)
            if (s != null) s.draw(c) else c.drawColor(Color.BLACK)
        } finally {
            if (c != null) {
                try {
                    h.unlockCanvasAndPost(c)
                } catch (e: Exception) {
                    // ignore
                }
            }
        }
    }
}
