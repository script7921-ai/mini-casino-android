package com.just.casino.screens

import android.graphics.Canvas
import android.graphics.RectF
import com.just.casino.core.Consts
import com.just.casino.core.SaveManager
import com.just.casino.core.Theme
import com.just.casino.core.UiButton
import com.just.casino.core.UiKit

class StatsScreen(private val save: SaveManager) : Screen {

    private var w = 0f
    private var h = 0f

    private val backBtn: UiButton = UiKit.makeBackButton(1f, 1f)
    private val btnReset = UiButton(
        Consts.BTN_RESET, RectF(), Consts.LABEL_RESET, true, Theme.BTN_RED
    )

    private var confirmDeadlineMs = 0L
    private var awaitingConfirm = false
    private var message = ""

    // Кэш строк статистики, чтобы не аллоцировать в кадре
    private var lineSpins = ""
    private var lineWins = ""
    private var linePercent = ""
    private var lineBest = ""
    private var lineWon = ""
    private var lineSpent = ""

    override fun layout(w: Float, h: Float) {
        this.w = w
        this.h = h
        backBtn.rect.set(0f, 0f, w * Consts.BACK_RIGHT_FRAC, h * Consts.BACK_BOTTOM_FRAC)
        val left = w * (1f - Consts.STATS_RESET_W) / 2f
        val right = w - left
        btnReset.rect.set(left, h * Consts.STATS_RESET_TOP, right, h * Consts.STATS_RESET_BOTTOM)
    }

    override fun update(dtMs: Long) {
        lineSpins = String.format(Consts.STATS_SPINS, save.spins)
        lineWins = String.format(Consts.STATS_WINS, save.wins)
        val percent = if (save.spins > 0) save.wins * Consts.PERCENT_BASE / save.spins else 0
        linePercent = String.format(Consts.STATS_PERCENT, percent)
        lineBest = String.format(Consts.STATS_BEST, save.bestWin)
        lineWon = String.format(Consts.STATS_TOTAL_WON, save.totalWon)
        lineSpent = String.format(Consts.STATS_TOTAL_SPENT, save.totalSpent)
        if (awaitingConfirm) {
            if (System.currentTimeMillis() >= confirmDeadlineMs) {
                awaitingConfirm = false
                btnReset.label = Consts.LABEL_RESET
            }
        }
    }

    override fun draw(c: Canvas) {
        c.drawColor(Theme.BG)
        UiKit.drawTopLeftBack(c, backBtn)
        val cx = w / 2f
        var y = h * Consts.STATS_FIRST_Y
        val step = h * Consts.STATS_STEP_Y
        UiKit.drawCenteredText(c, lineSpins, cx, y, UiKit.sizeInfo, Theme.TEXT_MAIN)
        y += step
        UiKit.drawCenteredText(c, lineWins, cx, y, UiKit.sizeInfo, Theme.TEXT_MAIN)
        y += step
        UiKit.drawCenteredText(c, linePercent, cx, y, UiKit.sizeInfo, Theme.TEXT_MAIN)
        y += step
        UiKit.drawCenteredText(c, lineBest, cx, y, UiKit.sizeInfo, Theme.TEXT_MAIN)
        y += step
        UiKit.drawCenteredText(c, lineWon, cx, y, UiKit.sizeInfo, Theme.TEXT_MAIN)
        y += step
        UiKit.drawCenteredText(c, lineSpent, cx, y, UiKit.sizeInfo, Theme.TEXT_MAIN)
        btnReset.draw(c)
        if (message.isNotEmpty()) {
            UiKit.drawCenteredText(c, message, cx, h * Consts.STATS_MESSAGE_Y, UiKit.sizeResult, Theme.TEXT_WIN)
        }
    }

    override fun onTouch(x: Float, y: Float): ScreenId? {
        if (backBtn.hit(x, y)) {
            return ScreenId.LOBBY
        }
        if (btnReset.hit(x, y)) {
            if (awaitingConfirm) {
                save.resetAll()
                awaitingConfirm = false
                btnReset.label = Consts.LABEL_RESET
                message = Consts.MSG_RESET_DONE
            } else {
                awaitingConfirm = true
                confirmDeadlineMs = System.currentTimeMillis() + Consts.RESET_CONFIRM_MS
                btnReset.label = Consts.LABEL_RESET_CONFIRM
                message = ""
            }
        }
        return null
    }

    override fun onBack(): ScreenId = ScreenId.LOBBY
}
