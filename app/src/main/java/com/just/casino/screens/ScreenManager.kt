package com.just.casino.screens

import android.content.Context
import com.just.casino.core.SaveManager

class ScreenManager(context: Context) {

    val save: SaveManager = SaveManager(context)

    private val screens: HashMap<ScreenId, Screen> = HashMap()
    var current: ScreenId = ScreenId.LOBBY
        private set

    init {
        save.load()
        screens[ScreenId.LOBBY] = LobbyScreen(save)
        screens[ScreenId.SLOTS] = SlotsScreen(save)
        screens[ScreenId.WHEEL] = WheelScreen(save)
        screens[ScreenId.STATS] = StatsScreen(save)
    }

    fun screen(id: ScreenId): Screen? = screens[id]

    fun go(id: ScreenId) {
        current = id
    }

    // true — «назад» обработан (переход), false — дефолт (выход из приложения)
    fun back(): Boolean {
        val target = screens[current]?.onBack() ?: return false
        current = target
        return true
    }

    fun layout(w: Float, h: Float) {
        for (s in screens.values) {
            s.layout(w, h)
        }
    }

    fun update(dtMs: Long) {
        screens[current]?.update(dtMs)
    }

    fun touch(x: Float, y: Float) {
        val target = screens[current]?.onTouch(x, y) ?: return
        current = target
    }

    // Экстренное дозавершение анимаций (свернул приложение и т.п.)
    fun finishAnimations() {
        for (s in screens.values) {
            if (s is SlotsScreen) s.forceFinishSpin()
            if (s is WheelScreen) s.forceFinishSpin()
        }
    }
}
