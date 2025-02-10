package com.example.pomofocus

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomofocus.components.BottomText
import com.example.pomofocus.components.ChronometerBox
import com.example.pomofocus.components.ChronometerButtons
import com.example.pomofocus.components.HeaderText
import com.example.pomofocus.components.PomodoroStateButton
import com.example.pomofocus.ui.theme.GreenShortBreak
import com.example.pomofocus.ui.theme.RedFocus
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val totalTime by viewModel.totalTime.collectAsState()
    val timer by viewModel.timer.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val minutes by viewModel.minutes.collectAsState()
    val seconds by viewModel.seconds.collectAsState()
    val progressTimerIndicator by viewModel.progressTimerIndicator.collectAsState()
    val pomodoroState by viewModel.pomodoroState.collectAsState()

    LaunchedEffect(key1 = timer, key2 = isTimerRunning) {
        if (timer > 0 && isTimerRunning) {
            delay(1000L)
            viewModel.decreaseTimer()
        } else if (timer == 0) {
            viewModel.finishTimer()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        val currentColor = if (pomodoroState == PomodoroState.FOCUS) RedFocus else GreenShortBreak

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(currentColor)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            HeaderText()

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PomodoroStateButton(
                        onClick = {},
                        isCurrentState = pomodoroState == PomodoroState.FOCUS,
                        stringRes = R.string.btn_focus
                    )
                    PomodoroStateButton(
                        onClick = {},
                        isCurrentState = pomodoroState == PomodoroState.SHORT_BREAK,
                        stringRes = R.string.btn_short_break
                    )

                }

                ChronometerBox(
                    minutes = minutes,
                    seconds = seconds,
                    progressTimerIndicator = progressTimerIndicator
                )

                ChronometerButtons(
                    currentColor = currentColor,
                    isTimerRunning = isTimerRunning,
                    timer = timer,
                    totalTime = totalTime,
                    onMainButtonClick = {
                        if (isTimerRunning) {
                            viewModel.pauseTimer()
                        } else {
                            viewModel.startTimer()
                        }
                    },
                    onPlayerNextClick = { viewModel.finishTimer() }
                )
            }

            BottomText(pomodoroState = pomodoroState)
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen()
}