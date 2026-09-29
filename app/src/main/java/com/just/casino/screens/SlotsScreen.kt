package com.just.casino.screens

import android.graphics.Canvas
import android.graphics.RectF
import com.just.casino.core.Consts
import com.just.casino.core.DrawCache
import com.just.casino.core.SaveManager
import com.just.casino.core.Theme
import com.just.casino.core.UiButton
import com.just.casino.core.UiKit
import java.util.Random

class SlotsScreen(private val save: SaveManager) : Screen {

    private enum class State { IDLE, SPINNING, RESULT }

    private var w = 0f
    private var h = 0f

    private val backBtn: UiButton = UiKit.makeBackButton(1f, 1f)
    private val btnBetMinus = UiButton(Consts.BTN_BET_MINUS, RectF(), Consts.LABEL_BET_MINUS, true, Theme.BTN_GRAY)
    private val btnBetPlus = UiButton(Consts.BTN_BET_PLUS, RectF(), Consts.LABEL_BET_PLUS, true, Theme.BTN_GRAY)
    private val btnSpin = UiButton(Consts.BTN_SPIN, RectF(), Consts.LABEL_SPIN, true, Theme.BTN_RED)
    private val btnRescue = UiButton(Consts.BTN_RESCUE, RectF(), Consts.LABEL_RESCUE, true, Theme.BTN_RED)

    private val random = Random()

    private var state = State.IDLE
    private var spinElapsedMs = 0L
    private var symbolTimerMs = 0L

    // Индексы итогового результата; результат просчитывается СРАЗУ при нажатии spin
    private val finalIdx = IntArray(Consts.REELS_COUNT)
    // Что показываем на барабанах (строки из кэша Consts.SYMBOLS)
    private val shownSymbols = arrayOf("", "", "")
    private val reelStopped = booleanArrayOf(false, false, false)

    private var resultText = ""
    private var resultColor = Theme.TEXT_MAIN

    // Кэш строк и геометрии, чтобы не аллоцировать в кадре
    private var balanceLine = ""
    private var betLine = ""
    private val reelRects = arrayOf(RectF(), RectF(), RectF())
    private var reelsTop = 0f
    private var reelsBottom = 0f
    private var reelsCenterY = 0f

    override fun layout(w: Float, h: Float) {
        this.w = w
        this.h = h
        backBtn.rect.set(0f, 0f, w * Consts.BACK_RIGHT_FRAC, h * Consts.BACK_BOTTOM_FRAC)

        val rowTop = h * Consts.SLOTS_BTNS_TOP
        val rowBottom = h * Consts.SLOTS_BTNS_BOTTOM
        val smallW = w * Consts.SLOTS_SMALL_BTN_W
        val spinW = w * Consts.SLOTS_SPIN_BTN_W
        val gap = (w - smallW * 2f - spinW) / 4f
        var x = gap
        btnBetMinus.rect.set(x, rowTop, x + smallW, rowBottom)
        x += smallW + gap
        btnSpin.rect.set(x, rowTop, x + spinW, rowBottom)
        x += spinW + gap
        btnBetPlus.rect.set(x, rowTop, x + smallW, rowBottom)

        val rLeft = w * (1f - Consts.SLOTS_RESCUE_W) / 2f
        btnRescue.rect.set(
            rLeft, h * Consts.SLOTS_RESCUE_TOP,
            w - rLeft, h * Consts.SLOTS_RESCUE_BOTTOM
        )

        reelsTop = h * Consts.SLOTS_REELS_TOP
        reelsBottom = h * Consts.SLOTS_REELS_BOTTOM
        reelsCenterY = (reelsTop + reelsBottom) / 2f
        val rw = w * Consts.SLOTS_REEL_W
        val rgap = w * Consts.SLOTS_REEL_GAP
        val blockW2 = rw * Consts.REELS_COUNT + rgap * (Consts.REELS_COUNT - 1)
        val bLeft = (w - blockW2) / 2f
        for (i in 0 until Consts.REELS_COUNT) {
            val left = bLeft + i * (rw + rgap)
            reelRects[i].set(left, reelsTop, left + rw, reelsBottom)
        }
    }

    override fun update(dtMs: Long) {
        btnRescue.visible = save.balance < Consts.RESCUE_THRESHOLD
        balanceLine = String.format(Consts.LABEL_BALANCE, save.balance)
        betLine = String.format(Consts.LABEL_BET, save.bet)

        if (state == State.SPINNING) {
            spinElapsedMs += dtMs
            symbolTimerMs += dtMs
            if (symbolTimerMs >= Consts.SPIN_SYMBOL_INTERVAL_MS) {
                symbolTimerMs -= Consts.SPIN_SYMBOL_INTERVAL_MS
                for (i in 0 until Consts.REELS_COUNT) {
                    if (!reelStopped[i]) {
                        shownSymbols[i] = Consts.SYMBOLS[random.nextInt(Consts.SYMBOLS.size)]
                    }
                }
            }
            if (spinElapsedMs >= Consts.STOP_REEL1_MS && !reelStopped[0]) stopReel(0)
            if (spinElapsedMs >= Consts.STOP_REEL2_MS && !reelStopped[1]) stopReel(1)
            if (spinElapsedMs >= Consts.STOP_REEL3_MS && !reelStopped[2]) stopReel(2)
        }

        val spinning = state == State.SPINNING
        btnBetMinus.enabled = !spinning
        btnBetPlus.enabled = !spinning
        btnSpin.enabled = !spinning && save.balance >= save.bet
    }

    private fun stopReel(index: Int) {
        reelStopped[index] = true
        shownSymbols[index] = Consts.SYMBOLS[finalIdx[index]]
        if (reelStopped[0] && reelStopped[1] && reelStopped[2]) {
            settleWin()
        }
    }

    private fun settleWin() {
        val mult = payoutMultiplier(finalIdx[0], finalIdx[1], finalIdx[2])
        val win = save.bet * mult
        if (win > 0) {
            save.balance += win
            save.wins += 1
            save.totalWon += win
            if (win > save.bestWin) save.bestWin = win
            if (mult >= Consts.PAYOUT_TRIPLE_777) {
                resultText = String.format(Consts.MSG_JACKPOT, win)
                resultColor = Theme.GOLD
            } else {
                resultText = String.format(Consts.MSG_WIN, win)
                resultColor = Theme.TEXT_WIN
            }
        } else {
            resultText = Consts.MSG_LOSE
            resultColor = Theme.TEXT_LOSE
        }
        save.save()
        state = State.RESULT
    }

    override fun draw(c: Canvas) {
        c.drawColor(Theme.BG)
        UiKit.drawTopLeftBack(c, backBtn)
        val cx = w / 2f
        UiKit.drawCenteredText(c, balanceLine, cx, h * Consts.SLOTS_BALANCE_Y, UiKit.sizeInfo, Theme.TEXT_MAIN)
        UiKit.drawCenteredText(c, betLine, cx, h * Consts.SLOTS_BET_Y, UiKit.sizeInfo, Theme.TEXT_MAIN)
        for (i in 0 until Consts.REELS_COUNT) {
            val r = reelRects[i]
            DrawCache.fill.color = Theme.REEL_BG
            c.drawRect(r, DrawCache.fill)
            UiKit.drawCenteredText(
                c, shownSymbols[i], r.centerX(), reelsCenterY,
                UiKit.sizeReel, Theme.TEXT_MAIN
            )
        }

        if (resultText.isNotEmpty()) {
            UiKit.drawCenteredText(c, resultText, cx, h * Consts.SLOTS_RESULT_Y, UiKit.sizeResult, resultColor)
        }

        btnBetMinus.draw(c)
        btnBetPlus.draw(c)
        btnSpin.draw(c)
        btnRescue.draw(c)
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
        } else if (btnRescue.hit(x, y)) {
            save.balance += Consts.RESCUE_BONUS
            save.save()
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
        for (i in 0 until Consts.REELS_COUNT) {
            finalIdx[i] = randomSymbolIndex()
            shownSymbols[i] = Consts.SYMBOLS[finalIdx[i]]
            reelStopped[i] = false
        }
        resultText = ""
        spinElapsedMs = 0L
        symbolTimerMs = 0L
        state = State.SPINNING
    }

    // Экстренное дозавершение: показать финальные символы и начислить выигрыш
    fun forceFinishSpin() {
        if (state != State.SPINNING) return
        for (i in 0 until Consts.REELS_COUNT) {
            reelStopped[i] = true
            shownSymbols[i] = Consts.SYMBOLS[finalIdx[i]]
        }
        settleWin()
    }

    private fun randomSymbolIndex(): Int {
        val totalWeight = Consts.SYMBOL_WEIGHTS.sum()
        var roll = random.nextInt(totalWeight)
        for (i in Consts.SYMBOL_WEIGHTS.indices) {
            roll -= Consts.SYMBOL_WEIGHTS[i]
            if (roll < 0) return i
        }
        return 0
    }

    // Индексы: 0=🍒 1=🍋 2=🍇 3=💎 4=BAR 5=777
    private fun payoutMultiplier(a: Int, b: Int, c: Int): Int {
        if (a == b && b == c) {
            when (a) {
                IDX_CHERRY -> return Consts.PAYOUT_TRIPLE_CHERRY
                IDX_GRAPE -> return Consts.PAYOUT_TRIPLE_GRAPE
                IDX_DIAMOND -> return Consts.PAYOUT_TRIPLE_DIAMOND
                IDX_BAR -> return Consts.PAYOUT_TRIPLE_BAR
                IDX_SEVEN -> return Consts.PAYOUT_TRIPLE_777
            }
        }
        if (a == IDX_LEMON && b == IDX_LEMON) return Consts.PAYOUT_DOUBLE_LEMON
        var cherries = 0
        if (a == IDX_CHERRY) cherries++
        if (b == IDX_CHERRY) cherries++
        if (c == IDX_CHERRY) cherries++
        if (cherries == Consts.DOUBLE_COUNT) return Consts.PAYOUT_DOUBLE_CHERRY
        return 0
    }

    companion object {
        private const val IDX_CHERRY = 0
        private const val IDX_LEMON = 1
        private const val IDX_GRAPE = 2
        private const val IDX_DIAMOND = 3
        private const val IDX_BAR = 4
        private const val IDX_SEVEN = 5
    }

    override fun onBack(): ScreenId {
        forceFinishSpin()
        return ScreenId.LOBBY
    }
}
