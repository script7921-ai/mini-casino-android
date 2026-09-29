package com.just.casino.screens

import android.graphics.Canvas

interface Screen {
    fun layout(w: Float, h: Float)
    fun update(dtMs: Long)
    fun draw(c: Canvas)
    // null — экран не менялся; id — перейти на этот экран (обработчик вызовет на игровом потоке)
    fun onTouch(x: Float, y: Float): ScreenId?
    // null = экран не обработал «назад» (дефолт: выход из приложения)
    fun onBack(): ScreenId?
}
