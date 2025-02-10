package com.example.pomofocus

import androidx.lifecycle.ViewModel
import com.example.pomofocus.Constants.FOCUS_TIMER
import com.example.pomofocus.Constants.SHORT_BREAK_TIMER
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    val totalTime = MutableStateFlow(FOCUS_TIMER)
    val timer = MutableStateFlow(totalTime.value)
    val isTimerRunning = MutableStateFlow(false)

    val minutes = MutableStateFlow(timer.value / 60)
    val seconds = MutableStateFlow(timer.value % 60)

    val progressTimerIndicator = MutableStateFlow(0f)

    val pomodoroState = MutableStateFlow(PomodoroState.FOCUS)

    fun decreaseTimer() {
        timer.update { it - 1 }
        formatTime()
        calculateProgressTimerIndicator()
    }

    fun startTimer() {
        isTimerRunning.update { true }
    }

    fun pauseTimer() {
        isTimerRunning.update { false }
    }

    fun finishTimer() {
        pauseTimer()
        progressTimerIndicator.update { 0f }
        if (pomodoroState.value == PomodoroState.FOCUS) {
            totalTime.update { SHORT_BREAK_TIMER }
            timer.update { SHORT_BREAK_TIMER }
            pomodoroState.update { PomodoroState.SHORT_BREAK }
        } else {
            totalTime.update { FOCUS_TIMER }
            timer.update { FOCUS_TIMER }
            pomodoroState.update { PomodoroState.FOCUS }
        }
        formatTime()
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
}