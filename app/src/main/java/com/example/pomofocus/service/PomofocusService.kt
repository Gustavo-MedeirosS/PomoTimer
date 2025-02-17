package com.example.pomofocus.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.pomofocus.Constants.ACTION_SERVICE_FINISH
import com.example.pomofocus.Constants.ACTION_SERVICE_PAUSE
import com.example.pomofocus.Constants.ACTION_SERVICE_RESUME
import com.example.pomofocus.Constants.ACTION_SERVICE_START
import com.example.pomofocus.Constants.FOCUS_TIMER
import com.example.pomofocus.Constants.NOTIFICATION_CHANNEL_ID
import com.example.pomofocus.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.pomofocus.Constants.NOTIFICATION_ID
import com.example.pomofocus.Constants.SHORT_BREAK_TIMER
import com.example.pomofocus.PomofocusState
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
                    setStartButton()
//                    stopForegroundService()
                    removeFinishButton()
                    updateNotification()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    fun decreaseTimer() {
        timer.update { it - 1 }
        formatTime()
        calculateProgressTimerIndicator()
        updateNotification()
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

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun stopForegroundService() {
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                notificationManager.importance
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification() {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText(
                formatTimeForNotification()
            ).build()
        )
    }

    @SuppressLint("DefaultLocale")
    private fun formatTimeForNotification(): String {
        return String.format("%02d:%02d", minutes.value, seconds.value)
    }

    @SuppressLint("RestrictedApi")
    private fun setResumeButton() {
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0, "Resume", ServiceHelper.resumePendingIntent(this)
            )
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    @SuppressLint("RestrictedApi")
    private fun setPauseButton() {
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0, "Pause", ServiceHelper.pausePendingIntent(this)
            )
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    @SuppressLint("RestrictedApi")
    private fun setStartButton() {
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0, "Start", ServiceHelper.startPendingIntent(this)
            )
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    @SuppressLint("RestrictedApi")
    private fun setFinishButton() {
        val actionsListSize = notificationBuilder.mActions.size
        if (actionsListSize == 1) {
            notificationBuilder.mActions.add(
                1,
                NotificationCompat.Action(
                    0, "Finish", ServiceHelper.finishPendingIntent(this)
                )
            )
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    @SuppressLint("RestrictedApi")
    private fun removeFinishButton() {
        notificationBuilder.mActions.removeAt(1)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun setFocusTimeTitle() {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentTitle(
                "Focus Time"
            ).build()
        )
    }

    private fun setBreakTimeTitle() {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentTitle(
                "Break Time"
            ).build()
        )
    }

    private fun setProgressBarNotification() {
        notificationManager.notify(
            NOTIFICATION_ID,
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