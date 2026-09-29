package com.just.casino.screens

import android.graphics.Canvas
import android.graphics.RectF
import com.just.casino.core.Consts
import com.just.casino.core.SaveManager
import com.just.casino.core.Theme
import com.just.casino.core.UiButton
import com.just.casino.core.UiKit

class LobbyScreen(private val save: SaveManager) : Screen {

    private var w = 0f
    private var h = 0f

    // Кэш строк, чтобы не аллоцировать в кадре
    private var balanceLine = ""

    private val btnSlots = UiButton(
        Consts.BTN_GO_SLOTS, RectF(), Consts.LABEL_SLOTS, true, Theme.BTN_GREEN
    )
    private val btnWheel = UiButton(
        Consts.BTN_GO_WHEEL, RectF(), Consts.LABEL_WHEEL, true, Theme.BTN_GREEN
    )
    private val btnStats = UiButton(
        Consts.BTN_GO_STATS, RectF(), Consts.LABEL_STATS, true, Theme.BTN_GRAY
    )
    private val btnDaily = UiButton(
        Consts.BTN_DAILY, RectF(), Consts.LABEL_DAILY_TAKE, true, Theme.GOLD
    )
    private val btnRescue = UiButton(
        Consts.BTN_RESCUE, RectF(), Consts.LABEL_RESCUE, true, Theme.BTN_RED
    )

    override fun layout(w: Float, h: Float) {
        this.w = w
        this.h = h
        val left = w * (1f - Consts.LOBBY_BTN_W) / 2f
        val right = w - left
        btnSlots.rect.set(left, h * Consts.LOBBY_SLOTS_TOP, right, h * Consts.LOBBY_SLOTS_BOTTOM)
        btnWheel.rect.set(left, h * Consts.LOBBY_WHEEL_TOP, right, h * Consts.LOBBY_WHEEL_BOTTOM)
        btnStats.rect.set(left, h * Consts.LOBBY_STATS_TOP, right, h * Consts.LOBBY_STATS_BOTTOM)
        btnDaily.rect.set(left, h * Consts.LOBBY_DAILY_TOP, right, h * Consts.LOBBY_DAILY_BOTTOM)
        btnRescue.rect.set(left, h * Consts.LOBBY_RESCUE_TOP, right, h * Consts.LOBBY_RESCUE_BOTTOM)
    }

    override fun update(dtMs: Long) {
        balanceLine = String.format(Consts.LABEL_BALANCE, save.balance)
        // Ежедневный бонус
        val now = System.currentTimeMillis()
        val elapsed = now - save.lastBonusMs
        if (save.lastBonusMs == 0L || elapsed >= Consts.DAILY_PERIOD_MS) {
            btnDaily.enabled = true
            btnDaily.label = Consts.LABEL_DAILY_TAKE
            btnDaily.color = Theme.GOLD
        } else {
            val remainMin = (Consts.DAILY_PERIOD_MS - elapsed) / Consts.MINUTE_MS
            val hours = (remainMin / 60L).toInt()
            val minutes = (remainMin % 60L).toInt()
            btnDaily.enabled = false
            btnDaily.label = String.format(Consts.LABEL_DAILY_WAIT, hours, minutes)
            btnDaily.color = Theme.BTN_GRAY
        }
        // Спасительный бонус — только если баланс критически низкий
        btnRescue.visible = save.balance < Consts.RESCUE_THRESHOLD
    }

    override fun draw(c: Canvas) {
        c.drawColor(Theme.BG)
        val cx = w / 2f
        UiKit.drawCenteredText(c, Consts.TITLE_LOBBY, cx, h * Consts.LOBBY_TITLE_Y, UiKit.sizeTitle, Theme.GOLD)
        UiKit.drawCenteredText(c, balanceLine, cx, h * Consts.LOBBY_BALANCE_Y, UiKit.sizeInfo, Theme.TEXT_MAIN)
        btnSlots.draw(c)
        btnWheel.draw(c)
        btnStats.draw(c)
        btnDaily.draw(c)
        btnRescue.draw(c)
    }

    override fun onTouch(x: Float, y: Float): ScreenId? {
        if (btnSlots.hit(x, y)) {
            return ScreenId.SLOTS
        } else if (btnWheel.hit(x, y)) {
            return ScreenId.WHEEL
        } else if (btnStats.hit(x, y)) {
            return ScreenId.STATS
        } else if (btnDaily.hit(x, y)) {
            save.balance += Consts.DAILY_BONUS
            save.lastBonusMs = System.currentTimeMillis()
            save.save()
        } else if (btnRescue.hit(x, y)) {
            save.balance += Consts.RESCUE_BONUS
            save.save()
        }
        return null
    }

    override fun onBack(): ScreenId? = null
}
