package com.just.casino.core

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.TypedValue

class UiButton(
    val id: String,
    val rect: RectF,
    var label: String,
    var enabled: Boolean,
    var color: Int
) {
    var visible: Boolean = true

    fun draw(canvas: Canvas) {
        if (!visible) return
        val saveCount = if (enabled) -1 else canvas.saveLayerAlpha(rect, UiKit.DISABLED_ALPHA)
        val p = DrawCache.btnFill
        p.color = color
        canvas.drawRoundRect(rect, UiKit.CORNER, UiKit.CORNER, p)
        val tp = DrawCache.btnText
        tp.textSize = UiKit.sizeBtn
        tp.color = Theme.TEXT_MAIN
        val fm = tp.fontMetrics
        val ty = rect.centerY() - (fm.ascent + fm.descent) / 2f
        canvas.drawText(label, rect.centerX(), ty, tp)
        if (saveCount >= 0) canvas.restore()
    }

    fun hit(x: Float, y: Float): Boolean {
        return visible && enabled && rect.contains(x, y)
    }
}

object DrawCache {
    val btnFill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = android.graphics.Paint.Style.FILL }
    val btnText = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }
    val text = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }
    val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = android.graphics.Paint.Style.FILL }
    val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = android.graphics.Paint.Style.STROKE
    }
    val path = android.graphics.Path()
}

object UiKit {
    const val DISABLED_ALPHA = Consts.DISABLED_BUTTON_ALPHA
    const val CORNER = Consts.BUTTON_CORNER_PX

    // Размеры текста в px (sp -> px), пересчитываются при смене экрана
    var sizeTitle = 0f
    var sizeInfo = 0f
    var sizeReel = 0f
    var sizeBtn = 0f
    var sizeResult = 0f

    fun initDensity(scaledDensity: Float) {
        sizeTitle = spToPx(Consts.TEXT_SP_TITLE, scaledDensity)
        sizeInfo = spToPx(Consts.TEXT_SP_INFO, scaledDensity)
        sizeReel = spToPx(Consts.TEXT_SP_REEL, scaledDensity)
        sizeBtn = spToPx(Consts.TEXT_SP_BTN, scaledDensity)
        sizeResult = spToPx(Consts.TEXT_SP_RESULT, scaledDensity)
    }

    private fun spToPx(sp: Float, scaledDensity: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, makeMetrics(scaledDensity))
    }

    private fun makeMetrics(scaledDensity: Float): android.util.DisplayMetrics {
        val m = android.util.DisplayMetrics()
        m.density = 1f
        m.scaledDensity = scaledDensity
        return m
    }

    fun drawCenteredText(canvas: Canvas, text: String, x: Float, y: Float, sizePx: Float, color: Int) {
        val p = DrawCache.text
        p.textSize = sizePx
        p.color = color
        val fm = p.fontMetrics
        canvas.drawText(text, x, y - (fm.ascent + fm.descent) / 2f, p)
    }

    fun makeBackButton(w: Float, h: Float): UiButton {
        val r = RectF(0f, 0f, w * Consts.BACK_RIGHT_FRAC, h * Consts.BACK_BOTTOM_FRAC)
        return UiButton(Consts.BTN_BACK, r, Consts.LABEL_BACK, true, Theme.PANEL)
    }

    fun drawTopLeftBack(canvas: Canvas, btn: UiButton) {
        btn.draw(canvas)
    }
}
