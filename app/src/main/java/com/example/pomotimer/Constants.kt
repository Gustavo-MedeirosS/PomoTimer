package com.example.pomotimer

import android.app.PendingIntent

object Constants {
    const val FOCUS_TIMER = 25 * 60
    const val SHORT_BREAK_TIMER = 5 * 60

    const val SILENT_NOTIFICATION_CHANNEL_ID = "SILENT_NOTIFICATION_CHANNEL_ID"
    const val ALERT_NOTIFICATION_CHANNEL_ID = "ALERT_NOTIFICATION_CHANNEL_ID"
    const val NOTIFICATION_TIMER_UPDATES_NAME = "Timer"
    const val NOTIFICATION_TIMER_FINISHED_NAME = "Fim do Timer"
    const val SILENT_NOTIFICATION_ID = 10
    const val ALERT_NOTIFICATION_ID = 20

    const val ACTION_SERVICE_START = "ACTION_SERVICE_START"
    const val ACTION_SERVICE_PAUSE = "ACTION_SERVICE_PAUSE"
    const val ACTION_SERVICE_RESUME = "ACTION_SERVICE_RESUME"
    const val ACTION_SERVICE_FINISH = "ACTION_SERVICE_FINISH"
    const val ACTION_SERVICE_CANCEL_NOTIFICATIONS = "ACTION_SERVICE_CANCEL_NOTIFICATIONS"

    const val CLICK_REQUEST_CODE = 100
    const val FINISH_REQUEST_CODE = 101
    const val PAUSE_REQUEST_CODE = 102
    const val START_RESUME_REQUEST_CODE = 103

    const val FLAG = PendingIntent.FLAG_IMMUTABLE
}