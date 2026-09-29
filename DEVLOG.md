# DEVLOG — Мини Казино

## 2026-09-29: Архитектура SurfaceView + ScreenManager
Решение: чистый Canvas на SurfaceView, без XML-разметки и View-виджетов.
Причина: требование «без WebView/View-виджетов», единый игровой поток отрисовки ~60 FPS;
вся разметка — доли экрана (w/h), пересчёт в onSizeChanged.
Структура: core/ (Consts — все числа и тексты, Theme, UiKit+UiButton, SaveManager=SharedPreferences),
screens/ (Screen-интерфейс: layout/update/draw/onTouch->ScreenId?/onBack; Lobby, Slots, Wheel, Stats;
ScreenManager хранит HashMap<ScreenId, Screen> и текущий экран).
Потоки: touches через ConcurrentLinkedQueue (UI-поток -> игровой); layout() синхронно из onSizeChanged
(после super — surface ещё не рисуется); смена current только с игрового потока.
Анимации: slots — результат рандомизируется СРАЗУ при нажатии spin, барабаны останавливаются 700/1100/1500 мс;
wheel — ω0=1200°/с, замедление 400°/с², сектор под указателем = floor((360-angle%360)/45).
Pause: finishAnimations() дозавершает спин с уже просчитанным результатом + save(); resume — старт потока.
Back: Activity.onBackPressed -> manager.back(): из игр/статистики в лобби, из лобби — дефолт (выход).
Мультитач: обрабатывается только event.action == ACTION_DOWN (первый указатель).
В кадре не создаются Paint/RectF/String.format — всё кэшируется в update()/layout().
Бонусы: daily +200 раз в 24 ч (System.currentTimeMillis), спасительный +100 при балансе < 5 (ленивая кнопка).
Статистика: spins/wins/%/bestWin/totalWon/totalSpent, сброс с двухэтапным подтверждением (3 сек).
Размеры текста задаются в sp (TypedValue.applyDimension), а не в px.

## Исправления после ревью
- gradle.properties: android.aapt2FromMavenOverride указывал на несуществующий /usr/bin/aapt2 — сборка падала.
  Исправлено на /opt/android-sdk/build-tools/35.0.0/aapt2 (закоммичено отдельным коммитом fix(build)).
- Удалён неиспользуемый res/layout/activity_main.xml (setContentView вызывается программно).
- UiButton: вместо ручной подстановки p.alpha (не работал для текста) — canvas.saveLayerAlpha для disabled.
- Все магические числа/строки вынесены в Consts (включая подписи статистики и размеры шрифтов в sp).
