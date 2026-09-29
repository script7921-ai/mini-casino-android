package com.just.casino.core

object Consts {

    // Цикл отрисовки
    const val FRAME_MS = 16L
    const val MAX_DT_MS = 50L

    // Сохранение
    const val PREFS_NAME = "casino_save"
    const val KEY_BALANCE = "balance"
    const val KEY_BET = "bet"
    const val KEY_SPINS = "spins"
    const val KEY_WINS = "wins"
    const val KEY_BEST_WIN = "best_win"
    const val KEY_TOTAL_WON = "total_won"
    const val KEY_TOTAL_SPENT = "total_spent"
    const val KEY_LAST_BONUS_MS = "last_bonus_ms"

    // Стартовые значения
    const val START_BALANCE = 1000
    const val START_BET = 10
    const val BET_MIN = 5
    const val BET_MAX = 500

    // Ставка
    const val BET_STEP = 5

    // Бонусы
    const val DAILY_BONUS = 200
    const val DAILY_PERIOD_MS = 24L * 60L * 60L * 1000L
    const val RESCUE_BONUS = 100
    const val RESCUE_THRESHOLD = 5

    // Тексты кнопок и сообщений
    const val TITLE_LOBBY = "🎰 МИНИ КАЗИНО 🎰"
    const val LABEL_SLOTS = "🎰 СЛОТЫ"
    const val LABEL_WHEEL = "🎡 КОЛЕСО ФОРТУНЫ"
    const val LABEL_STATS = "📊 СТАТИСТИКА"
    const val LABEL_DAILY_TAKE = "🎁 ЗАБРАТЬ +200"
    const val LABEL_DAILY_WAIT = "🎁 ЧЕРЕЗ %d ч %d мин"
    const val LABEL_RESCUE = "🆘 СПАСИТЕЛЬНЫЙ БОНУС +100"
    const val LABEL_BACK = "← НАЗАД"
    const val LABEL_SPIN = "КРУТИТЬ"
    const val LABEL_BET_MINUS = "СТАВКА -"
    const val LABEL_BET_PLUS = "СТАВКА +"
    const val LABEL_WHEEL_SPIN = "КРУТИТЬ (СТАВКА %d)"
    const val MSG_WIN = "ВЫИГРЫШ: +%d"
    const val MSG_JACKPOT = "💎 ДЖЕКПОТ! +%d"
    const val MSG_LOSE = "НЕ ПОВЕЗЛО"
    const val LABEL_RESET = "СБРОСИТЬ ПРОГРЕСС"
    const val LABEL_RESET_CONFIRM = "ТОЧНО СБРОСИТЬ?"
    const val MSG_RESET_DONE = "ПРОГРЕСС СБРОШЕН"
    const val LABEL_BALANCE = "БАЛАНС: %d"
    const val LABEL_BET = "СТАВКА: %d"

    // ID кнопок
    const val BTN_GO_SLOTS = "go_slots"
    const val BTN_GO_WHEEL = "go_wheel"
    const val BTN_GO_STATS = "go_stats"
    const val BTN_DAILY = "daily"
    const val BTN_RESCUE = "rescue"
    const val BTN_BACK = "back"
    const val BTN_BET_MINUS = "bet_minus"
    const val BTN_BET_PLUS = "bet_plus"
    const val BTN_SPIN = "spin"
    const val BTN_RESET = "reset"

    // Lobby: доли экрана
    const val LOBBY_TITLE_Y = 0.10f
    const val LOBBY_BALANCE_Y = 0.16f
    const val LOBBY_BTN_W = 0.70f
    const val LOBBY_SLOTS_TOP = 0.30f
    const val LOBBY_SLOTS_BOTTOM = 0.40f
    const val LOBBY_WHEEL_TOP = 0.44f
    const val LOBBY_WHEEL_BOTTOM = 0.54f
    const val LOBBY_STATS_TOP = 0.58f
    const val LOBBY_STATS_BOTTOM = 0.68f
    const val LOBBY_DAILY_TOP = 0.72f
    const val LOBBY_DAILY_BOTTOM = 0.80f
    const val LOBBY_RESCUE_TOP = 0.84f
    const val LOBBY_RESCUE_BOTTOM = 0.92f

    // Back-кнопка
    const val BACK_RIGHT_FRAC = 0.28f
    const val BACK_BOTTOM_FRAC = 0.055f

    // Slots: доли экрана
    const val SLOTS_BALANCE_Y = 0.11f
    const val SLOTS_BET_Y = 0.15f
    const val SLOTS_REELS_TOP = 0.20f
    const val SLOTS_REELS_BOTTOM = 0.52f
    const val SLOTS_RESULT_Y = 0.58f
    const val SLOTS_BTNS_TOP = 0.66f
    const val SLOTS_BTNS_BOTTOM = 0.76f
    const val SLOTS_RESCUE_TOP = 0.80f
    const val SLOTS_RESCUE_BOTTOM = 0.88f
    const val SLOTS_REEL_W = 0.26f
    const val SLOTS_REEL_GAP = 0.04f
    const val SLOTS_SMALL_BTN_W = 0.20f
    const val SLOTS_SPIN_BTN_W = 0.48f
    const val SLOTS_RESCUE_W = 0.60f

    // Slots: символы и веса
    val SYMBOLS = arrayOf("🍒", "🍋", "🍇", "💎", "BAR", "777")
    val SYMBOL_WEIGHTS = intArrayOf(30, 25, 20, 15, 7, 3)
    const val PAYOUT_TRIPLE_CHERRY = 2
    const val PAYOUT_DOUBLE_LEMON = 3
    const val PAYOUT_TRIPLE_GRAPE = 4
    const val PAYOUT_TRIPLE_DIAMOND = 10
    const val PAYOUT_TRIPLE_BAR = 25
    const val PAYOUT_TRIPLE_777 = 100
    const val PAYOUT_DOUBLE_CHERRY = 1
    const val DOUBLE_COUNT = 2

    // Slots: анимация
    const val SPIN_SYMBOL_INTERVAL_MS = 70L
    const val STOP_REEL1_MS = 700L
    const val STOP_REEL2_MS = 1100L
    const val STOP_REEL3_MS = 1500L

    // Wheel: доли экрана
    const val WHEEL_BALANCE_Y = 0.10f
    const val WHEEL_BET_Y = 0.14f
    const val WHEEL_CX_FRAC = 0.5f
    const val WHEEL_CY_FRAC = 0.42f
    const val WHEEL_RADIUS_FRAC = 0.32f
    const val WHEEL_RESULT_Y = 0.78f
    const val WHEEL_BTNS_TOP = 0.84f
    const val WHEEL_BTNS_BOTTOM = 0.94f
    const val WHEEL_SIDE_BTN_W = 0.18f
    const val WHEEL_SPIN_BTN_W = 0.44f

    // Wheel: сектора и физика
    const val WHEEL_SECTORS = 8
    val WHEEL_MULTIPLIERS = intArrayOf(0, 1, 2, 3, 5, 10, 0, 2)
    const val WHEEL_GOLD_MULT = 10
    const val WHEEL_ZERO_MULT = 0
    const val WHEEL_OMEGA0 = 1200f
    const val WHEEL_DECEL = 400f

    // Stats: раскладка
    const val STATS_FIRST_Y = 0.15f
    const val STATS_STEP_Y = 0.07f
    const val STATS_RESET_TOP = 0.75f
    const val STATS_RESET_BOTTOM = 0.83f
    const val STATS_RESET_W = 0.60f
    const val RESET_CONFIRM_MS = 3000L
    const val PERCENT_BASE = 100
    const val STATS_MESSAGE_Y = 0.87f

    // Stats: подписи строк
    const val STATS_SPINS = "Всего спинов: %d"
    const val STATS_WINS = "Побед: %d"
    const val STATS_PERCENT = "Процент побед: %d%%"
    const val STATS_BEST = "Лучший выигрыш: %d"
    const val STATS_TOTAL_WON = "Всего выиграно: %d"
    const val STATS_TOTAL_SPENT = "Всего потрачено: %d"
    const val MINUTE_MS = 60000L

    // Slots: количество барабанов
    const val REELS_COUNT = 3

    // Wheel: геометрия указателя и текста секторов (доли радиуса)
    const val POINTER_W_FRAC = 0.09f
    const val POINTER_H_FRAC = 0.16f
    const val POINTER_TIP_IN_FRAC = 0.4f
    const val RIM_WIDTH_FRAC = 0.02f
    const val SECTOR_TEXT_R_FRAC = 0.68f
    const val FULL_ANGLE_DEG = 360f
    const val QUARTER_TURN_DEG = 90f
    const val MS_PER_SEC = 1000f

    // Размеры текста в sp
    const val TEXT_SP_TITLE = 28f
    const val TEXT_SP_INFO = 18f
    const val TEXT_SP_REEL = 44f
    const val TEXT_SP_BTN = 16f
    const val TEXT_SP_RESULT = 22f

    // UiKit
    const val DISABLED_BUTTON_ALPHA = 102
    const val BUTTON_CORNER_PX = 24f
}
