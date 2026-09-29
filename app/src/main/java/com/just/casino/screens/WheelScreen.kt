package com.just.casino.screens

import android.graphics.Canvas
import android.graphics.RectF
import com.just.casino.core.Consts
import com.just.casino.core.DrawCache
import com.just.casino.core.SaveManager
import com.just.casino.core.Theme
import com.just.casino.core.UiButton
import com.just.casino.core.UiKit

class WheelScreen(private val save: SaveManager) : Screen {

    private enum class State { IDLE, SPINNING }

    private var w = 0f
    private var h = 0f

    private val backBtn: UiButton = UiKit.makeBackButton(1f, 1f)
    private val btnBetMinus = UiButton(Consts.BTN_BET_MINUS, RectF(), Consts.LABEL_BET_MINUS, true, Theme.BTN_GRAY)
    private val btnBetPlus = UiButton(Consts.BTN_BET_PLUS, RectF(), Consts.LABEL_BET_PLUS, true, Theme.BTN_GRAY)
    private val btnSpin = UiButton(Consts.BTN_SPIN, RectF(), Consts.LABEL_SPIN, true, Theme.BTN_RED)

    // Кэш строк, чтобы не аллоцировать в кадре
    private var balanceLine = ""
    private var betLine = ""
    private var spinLabel = ""
    private var resultText = ""
    private var resultColor = Theme.TEXT_MAIN
    private val sectorLabels = Array(Consts.WHEEL_SECTORS) { i -> "x" + Consts.WHEEL_MULTIPLIERS[i] }

    private var state = State.IDLE
    private var angleDeg = 0f
    private var omega = 0f

    private var cx = 0f
    private var cy = 0f
    private var radius = 0f
    private val wheelBounds = RectF()

    // Сектор i занимает углы [i*45, (i+1)*45) в системе колеса (0 = вверх, по часовой)
    private val sectorSweep = Consts.FULL_ANGLE_DEG / Consts.WHEEL_SECTORS

    override fun layout(w: Float, h: Float) {
        this.w = w
        this.h = h
        backBtn.rect.set(0f, 0f, w * Consts.BACK_RIGHT_FRAC, h * Consts.BACK_BOTTOM_FRAC)

        cx = w * Consts.WHEEL_CX_FRAC
        cy = h * Consts.WHEEL_CY_FRAC
        val minWH = if (w < h) w else h
        radius = minWH * Consts.WHEEL_RADIUS_FRAC
        wheelBounds.set(cx - radius, cy - radius, cx + radius, cy + radius)

        val rowTop = h * Consts.WHEEL_BTNS_TOP
        val rowBottom = h * Consts.WHEEL_BTNS_BOTTOM
        val sideW = w * Consts.WHEEL_SIDE_BTN_W
        val spinW = w * Consts.WHEEL_SPIN_BTN_W
        btnBetMinus.rect.set(0f, rowTop, sideW, rowBottom)
        btnSpin.rect.set((w - spinW) / 2f, rowTop, (w + spinW) / 2f, rowBottom)
        btnBetPlus.rect.set(w - sideW, rowTop, w, rowBottom)
    }

    override fun update(dtMs: Long) {
        balanceLine = String.format(Consts.LABEL_BALANCE, save.balance)
        betLine = String.format(Consts.LABEL_BET, save.bet)
        spinLabel = String.format(Consts.LABEL_WHEEL_SPIN, save.bet)
        btnSpin.label = spinLabel

        if (state == State.SPINNING) {
            val dtSec = dtMs / Consts.MS_PER_SEC
            angleDeg += omega * dtSec
            omega -= Consts.WHEEL_DECEL * dtSec
            if (omega <= 0f) {
                omega = 0f
                settleResult()
            }
            if (angleDeg >= Consts.FULL_ANGLE_DEG) angleDeg -= Consts.FULL_ANGLE_DEG
        }

        val spinning = state == State.SPINNING
        btnBetMinus.enabled = !spinning
        btnBetPlus.enabled = !spinning
        btnSpin.enabled = !spinning && save.balance >= save.bet
    }

    private fun settleResult() {
        state = State.IDLE
        val pointerSector = pointerSector()
        val mult = Consts.WHEEL_MULTIPLIERS[pointerSector]
        val win = save.bet * mult
        if (win > 0) {
            save.balance += win
            save.wins += 1
            save.totalWon += win
            if (win > save.bestWin) save.bestWin = win
            resultText = String.format(Consts.MSG_WIN, win)
            resultColor = Theme.TEXT_WIN
        } else {
            resultText = Consts.MSG_LOSE
            resultColor = Theme.TEXT_LOSE
        }
        save.save()
    }

    // Сектор ПОД указателем (верх колеса). Колесо повёрнуто на angleDeg по часовой,
    // значит под верхним указателем оказался сектор со стартовым центром (360 - angle) mod 360.
    private fun pointerSector(): Int {
        var a = angleDeg % Consts.FULL_ANGLE_DEG
        if (a < 0f) a += Consts.FULL_ANGLE_DEG
        val posUnderPointer = (Consts.FULL_ANGLE_DEG - a) % Consts.FULL_ANGLE_DEG
        val idx = (posUnderPointer / sectorSweep).toInt() % Consts.WHEEL_SECTORS
        return idx
    }

    override fun draw(c: Canvas) {
        c.drawColor(Theme.BG)
        UiKit.drawTopLeftBack(c, backBtn)
        val centerX = w / 2f
        UiKit.drawCenteredText(c, balanceLine, centerX, h * Consts.WHEEL_BALANCE_Y, UiKit.sizeInfo, Theme.TEXT_MAIN)
        UiKit.drawCenteredText(c, betLine, centerX, h * Consts.WHEEL_BET_Y, UiKit.sizeInfo, Theme.TEXT_MAIN)

        drawWheel(c)
        drawPointer(c)

        if (resultText.isNotEmpty()) {
            UiKit.drawCenteredText(c, resultText, centerX, h * Consts.WHEEL_RESULT_Y, UiKit.sizeResult, resultColor)
        }

        btnBetMinus.draw(c)
        btnBetPlus.draw(c)
        btnSpin.draw(c)
    }

    private fun drawWheel(c: Canvas) {
        val p = DrawCache.fill
        for (i in 0 until Consts.WHEEL_SECTORS) {
            val start = -sectorSweep + i * sectorSweep + angleDeg
            p.color = if (Consts.WHEEL_MULTIPLIERS[i] == Consts.WHEEL_GOLD_MULT) Theme.GOLD
            else if (i % 2 == 0) Theme.SECTOR_EVEN else Theme.SECTOR_ODD
            DrawCache.path.rewind()
            DrawCache.path.moveTo(cx, cy)
            DrawCache.path.arcTo(wheelBounds, start, sectorSweep)
            DrawCache.path.close()
            c.drawPath(DrawCache.path, p)
        }
        // Ободок
        val s = DrawCache.stroke
        s.color = Theme.GOLD
        s.strokeWidth = radius * Consts.RIM_WIDTH_FRAC
        c.drawCircle(cx, cy, radius, s)
        // Текст множителей в середине секторов
        for (i in 0 until Consts.WHEEL_SECTORS) {
            val midDeg = i * sectorSweep + sectorSweep / 2f + angleDeg
            val rad = Math.toRadians((midDeg - Consts.QUARTER_TURN_DEG).toDouble())
            val tx = cx + (radius * Consts.SECTOR_TEXT_R_FRAC) * Math.cos(rad).toFloat()
            val ty = cy + (radius * Consts.SECTOR_TEXT_R_FRAC) * Math.sin(rad).toFloat()
            val mult = Consts.WHEEL_MULTIPLIERS[i]
            val color = when {
                mult == Consts.WHEEL_GOLD_MULT -> Theme.BLACK
                mult == Consts.WHEEL_ZERO_MULT -> Theme.TEXT_LOSE
                else -> Theme.TEXT_MAIN
            }
            UiKit.drawCenteredText(c, sectorLabels[i], tx, ty, UiKit.sizeInfo, color)
        }
    }

    private fun drawPointer(c: Canvas) {
        // Треугольник вершиной вниз над верхней точкой колеса
        val pw = radius * Consts.POINTER_W_FRAC
        val ph = radius * Consts.POINTER_H_FRAC
        val topY = cy - radius
        val path = DrawCache.path
        path.rewind()
        path.moveTo(cx, topY + ph * Consts.POINTER_TIP_IN_FRAC)
        path.lineTo(cx - pw, topY - ph)
        path.lineTo(cx + pw, topY - ph)
        path.close()
        DrawCache.fill.color = Theme.GOLD
        c.drawPath(path, DrawCache.fill)
    }

    override fun onTouch(x: Float, y: Float): ScreenId? {
        if (backBtn.hit(x, y)) {
            return ScreenId.LOBBY
        }
        if (btnSpin.hit(x, y)) {
            startSpin()
        } else if (btnBetMinus.hit(x, y)) {
            save.changeBet(-Consts.BET_STEP)
        } else if (btnBetPlus.hit(x, y)) {
            save.changeBet(Consts.BET_STEP)
        }
        return null
    }

    private fun startSpin() {
        if (state == State.SPINNING) return
        if (save.balance < save.bet) return
        save.balance -= save.bet
        save.totalSpent += save.bet
        save.spins += 1
        save.save()
        resultText = ""
        omega = Consts.WHEEL_OMEGA0
        state = State.SPINNING
    }

    // Экстренное дозавершение: мгновенно остановить и начислить по текущему углу
    fun forceFinishSpin() {
        if (state != State.SPINNING) return
        omega = 0f
        settleResult()
    }

    override fun onBack(): ScreenId {
        forceFinishSpin()
        return ScreenId.LOBBY
    }
}
