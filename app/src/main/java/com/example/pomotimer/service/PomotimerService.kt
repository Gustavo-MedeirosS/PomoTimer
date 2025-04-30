package com.example.pomotimer.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.pomotimer.Constants.ACTION_SERVICE_CANCEL_NOTIFICATIONS
import com.example.pomotimer.Constants.ACTION_SERVICE_FINISH
import com.example.pomotimer.Constants.ACTION_SERVICE_PAUSE
import com.example.pomotimer.Constants.ACTION_SERVICE_RESUME
import com.example.pomotimer.Constants.ACTION_SERVICE_START
import com.example.pomotimer.Constants.ALERT_NOTIFICATION_CHANNEL_ID
import com.example.pomotimer.Constants.ALERT_NOTIFICATION_ID
import com.example.pomotimer.Constants.NOTIFICATION_TIMER_FINISHED_NAME
import com.example.pomotimer.Constants.NOTIFICATION_TIMER_UPDATES_NAME
import com.example.pomotimer.Constants.SILENT_NOTIFICATION_CHANNEL_ID
import com.example.pomotimer.Constants.SILENT_NOTIFICATION_ID
import com.example.pomotimer.di.AlertNotificationBuilder
import com.example.pomotimer.di.SilentNotificationBuilder
import com.example.pomotimer.ui.PomotimerState
import dagger.hilt.android.AndroidEntryPoint
import medeiros.dev.pomotimer.R
import javax.inject.Inject

@AndroidEntryPoint
class PomotimerService : Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    @SilentNotificationBuilder
    lateinit var silentNotificationBuilder: NotificationCompat.Builder

    @Inject
    @AlertNotificationBuilder
    lateinit var alertNotificationBuilder: NotificationCompat.Builder

    private val binder = PomotimerBinder()

    private var isForegroundServiceRunning = false

    override fun onBind(intent: Intent?) = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // actions from screen and notification
        intent?.action.let { action ->
            when (action) {
                ACTION_SERVICE_START -> {
                    startForegroundService()
                    setPauseButton()
                    setFinishButton()
                    cancelNotificationByChannelId(channelId = ALERT_NOTIFICATION_ID)
                    notifyManager(notificationId = SILENT_NOTIFICATION_ID)
                }

                ACTION_SERVICE_PAUSE -> {
                    setResumeButton()
                    notifyManager(notificationId = SILENT_NOTIFICATION_ID)
                    stopForegroundService()
                }

                ACTION_SERVICE_RESUME -> {
                    setPauseButton()
                    setFinishButton()
                    startForegroundService()
                    notifyManager(notificationId = SILENT_NOTIFICATION_ID)
                }

                ACTION_SERVICE_FINISH -> {
                    stopForegroundService()
                    cancelNotificationByChannelId(channelId = SILENT_NOTIFICATION_ID)
                }

                ACTION_SERVICE_CANCEL_NOTIFICATIONS -> {
                    stopForegroundService()
                    cancelAllNotifications()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        createNotificationChannels()
        startForeground(SILENT_NOTIFICATION_ID, silentNotificationBuilder.build())
        isForegroundServiceRunning = true
    }

    private fun stopForegroundService() {
        isForegroundServiceRunning = false
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.notificationChannels.isNotEmpty()) {
                val silentChannel = NotificationChannel(
                    SILENT_NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_TIMER_UPDATES_NAME,
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    enableVibration(false)
                }

                val alertChannel = NotificationChannel(
                    ALERT_NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_TIMER_FINISHED_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 500, 500)
                    val soundUri = Uri.parse(
                        "android.resource://com.example.pomotimer/" + R.raw.classic_alarm_clock_sound
                    )
                    setSound(soundUri, null)
                }

                notificationManager.createNotificationChannels(listOf(silentChannel, alertChannel))
            }
        }
    }

    fun updateSilentNotification(
        totalTime: Int,
        timer: Int,
        minutes: Int,
        seconds: Int
    ) {
        silentNotificationBuilder
            .setContentText(formatTimeForNotification(minutes, seconds))
            .setProgress(totalTime, totalTime - timer, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        notifyManager(notificationId = SILENT_NOTIFICATION_ID)
    }

    fun showAlertNotification(pomotimerState: PomotimerState) {
        setStartButton()
        alertNotificationBuilder
            .setContentTitle(
                if (pomotimerState == PomotimerState.FOCUS) applicationContext.getString(R.string.ntf_alert_title_focus)
                else applicationContext.getString(R.string.ntf_alert_title_break)
            )
            .setContentText(
                if (pomotimerState == PomotimerState.FOCUS) applicationContext.getString(R.string.ntf_msg_focus)
                else applicationContext.getString(R.string.ntf_msg_break)
            )
            .setOngoing(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setProgress(0, 0, false)
            .build()
        notifyManager(notificationId = ALERT_NOTIFICATION_ID)
    }

    @SuppressLint("DefaultLocale")
    private fun formatTimeForNotification(minutes: Int, seconds: Int): String {
        return String.format("%02d:%02d", minutes, seconds)
    }

    @SuppressLint("RestrictedApi")
    fun setResumeButton() {
        if (silentNotificationBuilder.mActions.size > 0) {
            silentNotificationBuilder.mActions.removeAt(0)
        }
        silentNotificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                applicationContext.getString(R.string.btn_resume),
                ServiceHelper.resumePendingIntent(this)
            )
        )
    }

    @SuppressLint("RestrictedApi")
    fun setPauseButton() {
        if (silentNotificationBuilder.mActions.size > 0) {
            silentNotificationBuilder.mActions.removeAt(0)
        }
        silentNotificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                applicationContext.getString(R.string.btn_pause),
                ServiceHelper.pausePendingIntent(this)
            )
        )
    }

    @SuppressLint("RestrictedApi")
    private fun setStartButton() {
        if (alertNotificationBuilder.mActions.size > 0) {
            alertNotificationBuilder.mActions.removeAt(0)
        }
        alertNotificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                applicationContext.getString(R.string.btn_start),
                ServiceHelper.startPendingIntent(this)
            )
        )
    }

    @SuppressLint("RestrictedApi")
    fun setFinishButton() {
        if (silentNotificationBuilder.mActions.size == 1) {
            silentNotificationBuilder.mActions.add(
                1,
                NotificationCompat.Action(
                    0,
                    applicationContext.getString(R.string.btn_finish),
                    ServiceHelper.finishPendingIntent(this)
                )
            )
        }
    }

    fun setSilentNotificationTitle(pomotimerState: PomotimerState) {
        silentNotificationBuilder.setContentTitle(
            if (pomotimerState == PomotimerState.FOCUS) applicationContext.getString(R.string.ntf_silent_title_focus)
            else applicationContext.getString(R.string.ntf_silent_title_break)
        ).build()
    }

    private fun notifyManager(notificationId: Int) {
        when (notificationId) {
            SILENT_NOTIFICATION_ID -> notificationManager.notify(
                SILENT_NOTIFICATION_ID,
                silentNotificationBuilder.build()
            )

            ALERT_NOTIFICATION_ID -> notificationManager.notify(
                ALERT_NOTIFICATION_ID,
                alertNotificationBuilder.build()
            )
        }
    }

    private fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }

    private fun cancelNotificationByChannelId(channelId: Int) {
        notificationManager.cancel(channelId)
    }

    inner class PomotimerBinder : Binder() {
        fun getService(): PomotimerService = this@PomotimerService
    }
}