package com.just.casino

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager

class MainActivity : Activity() {

    private lateinit var view: GameSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        view = GameSurfaceView(this)
        setContentView(view)
    }

    override fun onPause() {
        super.onPause()
        view.pauseGame()
    }

    override fun onResume() {
        super.onResume()
        view.resumeGame()
    }

    override fun onBackPressed() {
        if (!view.handleBack()) {
            super.onBackPressed()
        }
    }
}

