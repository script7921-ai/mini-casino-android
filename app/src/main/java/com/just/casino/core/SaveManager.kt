package com.just.casino.core

import android.content.Context
import android.content.SharedPreferences

class SaveManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(Consts.PREFS_NAME, Context.MODE_PRIVATE)

    var balance: Int = Consts.START_BALANCE
    var bet: Int = Consts.START_BET
    var spins: Int = 0
    var wins: Int = 0
    var bestWin: Int = 0
    var totalWon: Int = 0
    var totalSpent: Int = 0
    var lastBonusMs: Long = 0L

    fun load() {
        balance = prefs.getInt(Consts.KEY_BALANCE, Consts.START_BALANCE)
        bet = clampBet(prefs.getInt(Consts.KEY_BET, Consts.START_BET))
        spins = prefs.getInt(Consts.KEY_SPINS, 0)
        wins = prefs.getInt(Consts.KEY_WINS, 0)
        bestWin = prefs.getInt(Consts.KEY_BEST_WIN, 0)
        totalWon = prefs.getInt(Consts.KEY_TOTAL_WON, 0)
        totalSpent = prefs.getInt(Consts.KEY_TOTAL_SPENT, 0)
        lastBonusMs = prefs.getLong(Consts.KEY_LAST_BONUS_MS, 0L)
    }

    fun save() {
        prefs.edit()
            .putInt(Consts.KEY_BALANCE, balance)
            .putInt(Consts.KEY_BET, bet)
            .putInt(Consts.KEY_SPINS, spins)
            .putInt(Consts.KEY_WINS, wins)
            .putInt(Consts.KEY_BEST_WIN, bestWin)
            .putInt(Consts.KEY_TOTAL_WON, totalWon)
            .putInt(Consts.KEY_TOTAL_SPENT, totalSpent)
            .putLong(Consts.KEY_LAST_BONUS_MS, lastBonusMs)
            .apply()
    }

    fun resetAll() {
        balance = Consts.START_BALANCE
        bet = Consts.START_BET
        spins = 0
        wins = 0
        bestWin = 0
        totalWon = 0
        totalSpent = 0
        lastBonusMs = 0L
        save()
    }

    fun addBalance(delta: Int) {
        balance += delta
        save()
    }

    fun changeBet(delta: Int) {
        bet = clampBet(bet + delta)
        save()
    }

    private fun clampBet(v: Int): Int {
        if (v < Consts.BET_MIN) return Consts.BET_MIN
        if (v > Consts.BET_MAX) return Consts.BET_MAX
        return v
    }
}
