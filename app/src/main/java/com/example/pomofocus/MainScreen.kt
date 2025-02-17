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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pomofocus.ui.components.BottomText
import com.example.pomofocus.ui.components.ChronometerBox
import com.example.pomofocus.ui.components.ChronometerButtons
import com.example.pomofocus.ui.components.HeaderText
import com.example.pomofocus.ui.components.PomodoroStateButton
import com.example.pomofocus.service.PomofocusService
import com.example.pomofocus.service.ServiceHelper
import com.example.pomofocus.ui.theme.GreenShortBreak
import com.example.pomofocus.ui.theme.RedFocus
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@Composable
fun MainScreen(pomofocusService: PomofocusService) {
    val context = LocalContext.current
//    val windowSizeClass =
    val totalTime by pomofocusService.totalTime.collectAsState()
    val timer by pomofocusService.timer.collectAsState()
    val isTimerRunning by pomofocusService.isTimerRunning.collectAsState()
    val minutes by pomofocusService.minutes.collectAsState()
    val seconds by pomofocusService.seconds.collectAsState()
    val progressTimerIndicator by pomofocusService.progressTimerIndicator.collectAsState()
    val pomodoroState by pomofocusService.pomofocusState.collectAsState()

    LaunchedEffect(key1 = timer, key2 = isTimerRunning) {
        if (timer > 0 && isTimerRunning) {
            delay(1000L)
            pomofocusService.decreaseTimer()
        } else if (timer == 0) {
            ServiceHelper.triggerForegroundService(
                context = context,
                action = Constants.ACTION_SERVICE_FINISH
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        val currentColor = if (pomodoroState == PomofocusState.FOCUS) RedFocus else GreenShortBreak

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
                        isCurrentState = pomodoroState == PomofocusState.FOCUS,
                        stringRes = R.string.btn_focus
                    )
                    PomodoroStateButton(
                        onClick = {},
                        isCurrentState = pomodoroState == PomofocusState.SHORT_BREAK,
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
                            ServiceHelper.triggerForegroundService(
                                context = context,
                                action = Constants.ACTION_SERVICE_PAUSE
                            )
                        } else {
                            ServiceHelper.triggerForegroundService(
                                context = context,
                                action = Constants.ACTION_SERVICE_START
                            )
                        }
                    },
                    onPlayerNextClick = {
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = Constants.ACTION_SERVICE_FINISH
                        )
                    }
                )
            }

            BottomText(pomofocusState = pomodoroState)
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
//    MainScreen()
}