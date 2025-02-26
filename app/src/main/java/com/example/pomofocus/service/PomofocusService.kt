package com.example.pomofocus.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.os.Binder
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.pomofocus.Constants.ACTION_SERVICE_FINISH
import com.example.pomofocus.Constants.ACTION_SERVICE_PAUSE
import com.example.pomofocus.Constants.ACTION_SERVICE_RESUME
import com.example.pomofocus.Constants.ACTION_SERVICE_START
import com.example.pomofocus.Constants.ALARM_NOTIFICATION_CHANNEL_ID
import com.example.pomofocus.Constants.ALARM_NOTIFICATION_ID
import com.example.pomofocus.Constants.FOCUS_TIMER
import com.example.pomofocus.Constants.NOTIFICATION_TIMER_FINISHED_NAME
import com.example.pomofocus.Constants.NOTIFICATION_TIMER_UPDATES_NAME
import com.example.pomofocus.Constants.SHORT_BREAK_TIMER
import com.example.pomofocus.Constants.SILENT_NOTIFICATION_CHANNEL_ID
import com.example.pomofocus.Constants.SILENT_NOTIFICATION_ID
import com.example.pomofocus.PomofocusState
import com.example.pomofocus.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@AndroidEntryPoint
class PomofocusService : Service() {
    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    private val binder = PomofocusBinder()

    val totalTime = MutableStateFlow(FOCUS_TIMER)
    val timer = MutableStateFlow(totalTime.value)
    val isTimerRunning = MutableStateFlow(false)
    val isDialogOpened = MutableStateFlow(false)

    val minutes = MutableStateFlow(timer.value / 60)
    val seconds = MutableStateFlow(timer.value % 60)

    val progressTimerIndicator = MutableStateFlow(0f)

    val pomofocusState = MutableStateFlow(PomofocusState.FOCUS)

    override fun onBind(intent: Intent?) = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // actions from screen and notification
        intent?.action.let { action ->
            when (action) {
                ACTION_SERVICE_START -> {
                    startResumeTimer()
                    startForegroundService()
                    if (pomofocusState.value == PomofocusState.FOCUS) setFocusTimeTitle() else setBreakTimeTitle()
                    setPauseButton()
                    setFinishButton()
                }

                ACTION_SERVICE_PAUSE -> {
                    pauseTimer()
                    setResumeButton()
                }

                ACTION_SERVICE_RESUME -> {
                    startResumeTimer()
                    setPauseButton()
                }

                ACTION_SERVICE_FINISH -> {
                    finishTimer()
//                    stopForegroundService()
                    removeFinishButton()
                    updateSilentNotification()
                    updateAlarmNotification()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    fun decreaseTimer() {
        timer.update { it - 1 }
        formatTime()
        calculateProgressTimerIndicator()
        updateSilentNotification()
    }

    private fun startResumeTimer() {
        isTimerRunning.update { true }
    }

    private fun pauseTimer() {
        isTimerRunning.update { false }
    }

    private fun finishTimer() {
        pauseTimer()
        progressTimerIndicator.update { 0f }
        if (pomofocusState.value == PomofocusState.FOCUS) {
            totalTime.update { SHORT_BREAK_TIMER }
            timer.update { SHORT_BREAK_TIMER }
            pomofocusState.update { PomofocusState.SHORT_BREAK }
            setBreakTimeTitle()
        } else {
            totalTime.update { FOCUS_TIMER }
            timer.update { FOCUS_TIMER }
            pomofocusState.update { PomofocusState.FOCUS }
            setFocusTimeTitle()
        }
        formatTime()
        setProgressBarNotification()

        if (isDialogOpened.value) {
            closeAlertDialog()
        }
    }

    private fun formatTime() {
        minutes.update { timer.value / 60 }
        seconds.update { timer.value % 60 }
    }

    private fun calculateProgressTimerIndicator() {
        progressTimerIndicator.update {
            1f - (timer.value.toFloat() / totalTime.value)
        }
        setProgressBarNotification()
    }

    fun changePomofocusState() {
        finishTimer()
        updateSilentNotification()
        removeFinishButton()
    }

    fun openAlertDialog() {
        isDialogOpened.update { true }
    }

    fun closeAlertDialog() {
        isDialogOpened.update { false }
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        createNotificationChannels()
        startForeground(SILENT_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun stopForegroundService() {
        notificationManager.cancel(SILENT_NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
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
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null)
            }

            notificationManager.createNotificationChannels(listOf(silentChannel, alertChannel))
        }
    }

    private fun updateSilentNotification() {
        if (totalTime.value == timer.value) {
            setStartButton()
        }
        notificationManager.notify(
            SILENT_NOTIFICATION_ID,
            notificationBuilder
                .setChannelId(SILENT_NOTIFICATION_CHANNEL_ID)
                .setContentText(formatTimeForNotification())
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()
        )
    }

    private fun updateAlarmNotification() {
        notificationManager.cancel(ALARM_NOTIFICATION_ID)
        notificationManager.notify(
            ALARM_NOTIFICATION_ID,
            notificationBuilder
                .setChannelId(ALARM_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(
                    if (pomofocusState.value == PomofocusState.FOCUS) applicationContext.getString(R.string.ntf_alarm_title_focus)
                    else applicationContext.getString(R.string.ntf_alarm_title_break)
                )
                .setContentText(
                    if (pomofocusState.value == PomofocusState.FOCUS) applicationContext.getString(R.string.ntf_msg_focus)
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
    private fun formatTimeForNotification(): String {
        return String.format("%02d:%02d", minutes.value, seconds.value)
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
    private fun removeFinishButton() {
        if (notificationBuilder.mActions.size > 1) {
            notificationBuilder.setChannelId(SILENT_NOTIFICATION_CHANNEL_ID)
            notificationBuilder.mActions.removeAt(1)
            notificationManager.notify(SILENT_NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    private fun setFocusTimeTitle() {
        notificationManager.notify(
            SILENT_NOTIFICATION_ID,
            notificationBuilder.setContentTitle(
                applicationContext.getString(R.string.ntf_silent_title_focus)
            ).build()
        )
    }

    private fun setBreakTimeTitle() {
        notificationManager.notify(
            SILENT_NOTIFICATION_ID,
            notificationBuilder.setContentTitle(
                applicationContext.getString(R.string.ntf_silent_title_break)
            ).build()
        )
    }

    private fun setProgressBarNotification() {
        notificationManager.notify(
            SILENT_NOTIFICATION_ID,
            notificationBuilder.setProgress(
                totalTime.value,
                totalTime.value - timer.value,
                false
            ).build()
        )
    }

    inner class PomofocusBinder : Binder() {
        fun getService(): PomofocusService = this@PomofocusService
    }
}