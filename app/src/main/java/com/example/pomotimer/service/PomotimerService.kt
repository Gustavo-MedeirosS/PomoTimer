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
import com.example.pomotimer.Constants.ALARM_NOTIFICATION_CHANNEL_ID
import com.example.pomotimer.Constants.ALARM_NOTIFICATION_ID
import com.example.pomotimer.Constants.NOTIFICATION_TIMER_FINISHED_NAME
import com.example.pomotimer.Constants.NOTIFICATION_TIMER_UPDATES_NAME
import com.example.pomotimer.Constants.SILENT_NOTIFICATION_CHANNEL_ID
import com.example.pomotimer.Constants.SILENT_NOTIFICATION_ID
import com.example.pomotimer.ui.PomotimerState
import com.example.pomotimer.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PomotimerService : Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

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
                }

                ACTION_SERVICE_PAUSE -> { setResumeButton() }

                ACTION_SERVICE_RESUME -> { setPauseButton() }

                ACTION_SERVICE_FINISH -> {  }

                ACTION_SERVICE_CANCEL_NOTIFICATIONS -> {
                    stopForegroundService()
                    cancelNotifications()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    fun changePomotimerState() {
        if (!isForegroundServiceRunning) {
            startForegroundService()
        }
        removeFinishButton()
    }

    private fun startForegroundService() {
        createNotificationChannels()
        startForeground(SILENT_NOTIFICATION_ID, notificationBuilder.build())
        isForegroundServiceRunning = true
    }

    private fun stopForegroundService() {
        notificationManager.cancelAll()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        isForegroundServiceRunning = false
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val silentChannel = NotificationChannel(
                SILENT_NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_TIMER_UPDATES_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                enableVibration(false)
            }

            val alertChannel = NotificationChannel(
                ALARM_NOTIFICATION_CHANNEL_ID,
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

    fun updateSilentNotification(totalTime: Int, timer: Int, minutes: Int, seconds: Int) {
        if (totalTime == timer) {
            setStartButton()
        }
        notificationManager.notify(
            SILENT_NOTIFICATION_ID,
            notificationBuilder
                .setChannelId(SILENT_NOTIFICATION_CHANNEL_ID)
                .setContentText(formatTimeForNotification(minutes, seconds))
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()
        )
    }

    fun updateAlarmNotification(pomotimerState: PomotimerState) {
        notificationManager.cancel(ALARM_NOTIFICATION_ID)
        notificationManager.notify(
            ALARM_NOTIFICATION_ID,
            notificationBuilder
                .setChannelId(ALARM_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(
                    if (pomotimerState == PomotimerState.FOCUS) applicationContext.getString(R.string.ntf_alarm_title_focus)
                    else applicationContext.getString(R.string.ntf_alarm_title_break)
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
        )
    }

    @SuppressLint("DefaultLocale")
    private fun formatTimeForNotification(minutes: Int, seconds: Int): String {
        return String.format("%02d:%02d", minutes, seconds)
    }

    @SuppressLint("RestrictedApi")
    private fun setResumeButton() {
        notificationBuilder.setChannelId(SILENT_NOTIFICATION_CHANNEL_ID)
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                applicationContext.getString(R.string.btn_resume),
                ServiceHelper.resumePendingIntent(this)
            )
        )
        notificationManager.notify(SILENT_NOTIFICATION_ID, notificationBuilder.build())
    }

    @SuppressLint("RestrictedApi")
    private fun setPauseButton() {
        notificationBuilder.setChannelId(SILENT_NOTIFICATION_CHANNEL_ID)
        if (notificationBuilder.mActions.size > 0) {
            notificationBuilder.mActions.removeAt(0)
        }
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                applicationContext.getString(R.string.btn_pause),
                ServiceHelper.pausePendingIntent(this)
            )
        )
        notificationManager.notify(SILENT_NOTIFICATION_ID, notificationBuilder.build())
    }

    @SuppressLint("RestrictedApi")
    private fun setStartButton() {
        notificationBuilder.setChannelId(SILENT_NOTIFICATION_CHANNEL_ID)
        if (notificationBuilder.mActions.size > 0) {
            notificationBuilder.mActions.removeAt(0)
        }
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                applicationContext.getString(R.string.btn_start),
                ServiceHelper.startPendingIntent(this)
            )
        )
//        notificationManager.notify(SILENT_NOTIFICATION_ID, notificationBuilder.build())
    }

    @SuppressLint("RestrictedApi")
    private fun setFinishButton() {
        notificationBuilder.setChannelId(SILENT_NOTIFICATION_CHANNEL_ID)
        val actionsListSize = notificationBuilder.mActions.size
        if (actionsListSize == 1) {
            notificationBuilder.mActions.add(
                1,
                NotificationCompat.Action(
                    0,
                    applicationContext.getString(R.string.btn_finish),
                    ServiceHelper.finishPendingIntent(this)
                )
            )
            notificationManager.notify(SILENT_NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    @SuppressLint("RestrictedApi")
    fun removeFinishButton() {
        if (notificationBuilder.mActions.size > 1) {
            notificationBuilder.setChannelId(SILENT_NOTIFICATION_CHANNEL_ID)
            notificationBuilder.mActions.removeAt(1)
            notificationManager.notify(SILENT_NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    fun setFocusTimeTitle() {
        notificationManager.notify(
            SILENT_NOTIFICATION_ID,
            notificationBuilder.setContentTitle(
                applicationContext.getString(R.string.ntf_silent_title_focus)
            ).build()
        )
    }

    fun setBreakTimeTitle() {
        notificationManager.notify(
            SILENT_NOTIFICATION_ID,
            notificationBuilder.setContentTitle(
                applicationContext.getString(R.string.ntf_silent_title_break)
            ).build()
        )
    }

    private fun cancelNotifications() {
        notificationManager.cancelAll()
    }

    fun setProgressBarNotification(totalTime: Int, timer: Int) {
        notificationManager.notify(
            SILENT_NOTIFICATION_ID,
            notificationBuilder.setProgress(
                totalTime,
                totalTime - timer,
                false
            ).build()
        )
    }

    inner class PomotimerBinder : Binder() {
        fun getService(): PomotimerService = this@PomotimerService
    }
}