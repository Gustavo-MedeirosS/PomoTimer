package com.example.pomotimer.ui.view_model

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.pomotimer.Constants.ACTION_SERVICE_FINISH
import com.example.pomotimer.Constants.ACTION_SERVICE_PAUSE
import com.example.pomotimer.Constants.ACTION_SERVICE_RESUME
import com.example.pomotimer.Constants.ACTION_SERVICE_START
import com.example.pomotimer.Constants.FOCUS_TIMER
import com.example.pomotimer.Constants.SHORT_BREAK_TIMER
import com.example.pomotimer.ui.PomotimerState
import com.example.pomotimer.service.PomotimerService
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
    }

    fun triggerForegroundService(context: Context?, action: String?) {
        if (action != null && pomotimerService != null) {
            when (action) {
                ACTION_SERVICE_START -> {
                    startResumeTimer()
                    if (pomotimerState.value == PomotimerState.FOCUS)
                        pomotimerService!!.setFocusTimeTitle()
                    else pomotimerService!!.setBreakTimeTitle()
                }

                ACTION_SERVICE_PAUSE -> {
                    pauseTimer()
                }

                ACTION_SERVICE_RESUME -> {
                    startResumeTimer()
                }

                ACTION_SERVICE_FINISH -> {
                    finishTimer()
                    pomotimerService!!.updateSilentNotification(
                        pomotimerState = pomotimerState.value,
                        isTimerRunning = isTimerRunning.value,
                        totalTime = totalTime.value,
                        timer = timer.value,
                        minutes = minutes.value,
                        seconds = seconds.value
                    )
                    pomotimerService!!.removeFinishButton()
                    pomotimerService!!.updateAlarmNotification(pomotimerState = pomotimerState.value)
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
            if (pomotimerService != null) {
                pomotimerService!!.setBreakTimeTitle()
            }
        } else {
            totalTime.update { FOCUS_TIMER }
            timer.update { FOCUS_TIMER }
            pomotimerState.update { PomotimerState.FOCUS }
            if (pomotimerService != null) {
                pomotimerService!!.setFocusTimeTitle()
            }
        }
        formatTime()
        if (pomotimerService != null) {
            pomotimerService!!.setProgressBarNotification(
                totalTime = totalTime.value,
                timer = timer.value
            )
        }

        if (isDialogOpened.value) {
            closeAlertDialog()
        }
    }

    fun decreaseTimer() {
        timer.update { it - 1 }
        formatTime()
        calculateProgressTimerIndicator()
        if (pomotimerService != null) {
            pomotimerService!!.updateSilentNotification(
                pomotimerState = pomotimerState.value,
                isTimerRunning = isTimerRunning.value,
                totalTime = totalTime.value,
                timer = timer.value,
                minutes = minutes.value,
                seconds = seconds.value
            )
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
        if (pomotimerService != null) {
            pomotimerService!!.setProgressBarNotification(
                totalTime = totalTime.value,
                timer = timer.value
            )
        }
    }

    fun openAlertDialog() {
        isDialogOpened.update { true }
    }

    fun closeAlertDialog() {
        isDialogOpened.update { false }
    }

    fun changePomotimerState() {
        finishTimer()
        if (pomotimerService != null) {
            pomotimerService!!.updateSilentNotification(
                pomotimerState = pomotimerState.value,
                isTimerRunning = isTimerRunning.value,
                totalTime = totalTime.value,
                timer = timer.value,
                minutes = minutes.value,
                seconds = seconds.value
            )
            pomotimerService!!.changePomotimerState()
        }
    }
}