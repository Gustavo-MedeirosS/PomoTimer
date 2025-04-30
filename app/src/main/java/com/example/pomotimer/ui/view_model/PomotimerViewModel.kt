package com.example.pomotimer.ui.view_model

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.pomotimer.Constants.ACTION_SERVICE_CANCEL_NOTIFICATIONS
import com.example.pomotimer.Constants.ACTION_SERVICE_FINISH
import com.example.pomotimer.Constants.ACTION_SERVICE_PAUSE
import com.example.pomotimer.Constants.ACTION_SERVICE_RESUME
import com.example.pomotimer.Constants.ACTION_SERVICE_START
import com.example.pomotimer.Constants.FOCUS_TIMER
import com.example.pomotimer.Constants.SHORT_BREAK_TIMER
import com.example.pomotimer.service.PomotimerService
import com.example.pomotimer.ui.PomotimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PomotimerViewModel @Inject constructor() : ViewModel() {
    val totalTime = MutableStateFlow(FOCUS_TIMER)
    val timer = MutableStateFlow(totalTime.value)
    val isTimerRunning = MutableStateFlow(false)
    val isDialogOpened = MutableStateFlow(false)

    val minutes = MutableStateFlow(timer.value / 60)
    val seconds = MutableStateFlow(timer.value % 60)

    val progressTimerIndicator = MutableStateFlow(0f)

    val pomotimerState = MutableStateFlow(PomotimerState.FOCUS)

    @SuppressLint("StaticFieldLeak")
    private var pomotimerService: PomotimerService? = null

    fun setService(service: PomotimerService?) {
        pomotimerService = service

        if (service != null && (isTimerRunning.value || totalTime.value != timer.value)) {
            if (isTimerRunning.value) {
                pomotimerService!!.setPauseButton()
            } else {
                pomotimerService!!.setResumeButton()
            }
            pomotimerService!!.setSilentNotificationTitle(pomotimerState = pomotimerState.value)
            pomotimerService!!.setFinishButton()
            updateSilentNotification()
        }
    }

    fun triggerForegroundService(context: Context?, action: String?) {
        if (action != null && pomotimerService != null) {
            when (action) {
                ACTION_SERVICE_START -> {
                    startResumeTimer()
                }

                ACTION_SERVICE_PAUSE -> {
                    pauseTimer()
                }

                ACTION_SERVICE_RESUME -> {
                    startResumeTimer()
                }

                ACTION_SERVICE_FINISH -> {
                    finishTimer()
                    showAlertNotification()
                }
            }
        }

        Intent(context, PomotimerService::class.java).apply {
            this.action = action
            context?.startService(this)
        }
    }

    private fun startResumeTimer() {
        isTimerRunning.update { true }
        pomotimerService!!.setSilentNotificationTitle(pomotimerState = pomotimerState.value)
    }

    private fun pauseTimer() {
        isTimerRunning.update { false }
    }

    private fun finishTimer() {
        pauseTimer()
        progressTimerIndicator.update { 0f }
        if (pomotimerState.value == PomotimerState.FOCUS) {
            totalTime.update { SHORT_BREAK_TIMER }
            timer.update { SHORT_BREAK_TIMER }
            pomotimerState.update { PomotimerState.SHORT_BREAK }
        } else {
            totalTime.update { FOCUS_TIMER }
            timer.update { FOCUS_TIMER }
            pomotimerState.update { PomotimerState.FOCUS }
        }
        formatTime()

        if (isDialogOpened.value) closeAlertDialog()

        if (pomotimerService != null) updateSilentNotification()
    }

    private fun showAlertNotification() {
        if (pomotimerService != null) {
            pomotimerService!!.showAlertNotification(pomotimerState = pomotimerState.value)
        }
    }

    fun decreaseTimer() {
        timer.update { it - 1 }
        formatTime()
        calculateProgressTimerIndicator()
        if (pomotimerService != null) updateSilentNotification()
    }

    private fun updateSilentNotification() {
        pomotimerService!!.updateSilentNotification(
            totalTime = totalTime.value,
            timer = timer.value,
            minutes = minutes.value,
            seconds = seconds.value
        )
    }

    private fun formatTime() {
        minutes.update { timer.value / 60 }
        seconds.update { timer.value % 60 }
    }

    private fun calculateProgressTimerIndicator() {
        progressTimerIndicator.update {
            1f - (timer.value.toFloat() / totalTime.value)
        }
    }

    fun openAlertDialog() {
        isDialogOpened.update { true }
    }

    fun closeAlertDialog() {
        isDialogOpened.update { false }
    }

    fun changePomotimerState(context: Context?) {
        finishTimer()
        Intent(context, PomotimerService::class.java).apply {
            this.action = ACTION_SERVICE_CANCEL_NOTIFICATIONS
            context?.startService(this)
        }
    }
}