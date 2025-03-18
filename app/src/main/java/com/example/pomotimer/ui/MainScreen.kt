package com.example.pomotimer.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.pomotimer.Constants
import com.example.pomotimer.PomotimerState
import com.example.pomotimer.service.PomotimerService
import com.example.pomotimer.service.ServiceHelper
import com.example.pomotimer.ui.components.AlertDialog
import com.example.pomotimer.ui.layout.LandscapeLayout
import com.example.pomotimer.ui.layout.PortraitLayout
import com.example.pomotimer.ui.theme.GreenShortBreak
import com.example.pomotimer.ui.theme.RedFocus
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@Composable
fun MainScreen(
    pomotimerService: PomotimerService,
    windowSizeClass: WindowSizeClass
) {
    val context = LocalContext.current
    val totalTime by pomotimerService.totalTime.collectAsState()
    val timer by pomotimerService.timer.collectAsState()
    val isTimerRunning by pomotimerService.isTimerRunning.collectAsState()
    val minutes by pomotimerService.minutes.collectAsState()
    val seconds by pomotimerService.seconds.collectAsState()
    val progressTimerIndicator by pomotimerService.progressTimerIndicator.collectAsState()
    val pomodoroState by pomotimerService.pomotimerState.collectAsState()
    val isDialogOpened by pomotimerService.isDialogOpened.collectAsState()

    LaunchedEffect(key1 = timer, key2 = isTimerRunning) {
        if (timer > 0 && isTimerRunning) {
            delay(1000L)
            pomotimerService.decreaseTimer()
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
        val currentColor = if (pomodoroState == PomotimerState.FOCUS) RedFocus else GreenShortBreak

        if (isDialogOpened) {
            AlertDialog(
                onDismissRequest = { pomotimerService.closeAlertDialog() },
                onConfirmClick = { pomotimerService.changePomotimerState() },
                pomotimerState = pomodoroState
            )
        }

        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> {
                PortraitLayout(
                    context = LocalContext.current,
                    currentColor = currentColor,
                    innerPadding = innerPadding,
                    pomodoroState = pomodoroState,
                    onPomofocusButtonStateClick = {
                        if (isTimerRunning) {
                            pomotimerService.openAlertDialog()
                        } else {
                            pomotimerService.changePomotimerState()
                        }
                    },
                    isTimerRunning = isTimerRunning,
                    totalTime = totalTime,
                    timer = timer,
                    minutes = minutes,
                    seconds = seconds,
                    progressTimerIndicator = progressTimerIndicator,
                )
            }

            WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> {
                LandscapeLayout(
                    context = LocalContext.current,
                    currentColor = currentColor,
                    innerPadding = innerPadding,
                    pomodoroState = pomodoroState,
                    onPomofocusButtonStateClick = {
                        if (isTimerRunning) {
                            pomotimerService.openAlertDialog()
                        } else {
                            pomotimerService.changePomotimerState()
                        }
                    },
                    isTimerRunning = isTimerRunning,
                    totalTime = totalTime,
                    timer = timer,
                    minutes = minutes,
                    seconds = seconds,
                    progressTimerIndicator = progressTimerIndicator,
                )
            }
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
//    MainScreen()
}