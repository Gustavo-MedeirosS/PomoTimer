package com.example.pomotimer.ui.screen

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pomotimer.Constants
import com.example.pomotimer.ui.PomotimerState
import com.example.pomotimer.ui.components.AlertDialog
import com.example.pomotimer.ui.layout.LandscapeLayout
import com.example.pomotimer.ui.layout.PortraitLayout
import com.example.pomotimer.ui.theme.GreenShortBreak
import com.example.pomotimer.ui.theme.RedFocus
import com.example.pomotimer.ui.view_model.PomotimerViewModel
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@Composable
fun MainScreen(
    pomotimerViewModel: PomotimerViewModel = hiltViewModel(),
    windowSizeClass: WindowSizeClass
) {
    val context = LocalContext.current
    val totalTime by pomotimerViewModel.totalTime.collectAsState()
    val timer by pomotimerViewModel.timer.collectAsState()
    val isTimerRunning by pomotimerViewModel.isTimerRunning.collectAsState()
    val minutes by pomotimerViewModel.minutes.collectAsState()
    val seconds by pomotimerViewModel.seconds.collectAsState()
    val progressTimerIndicator by pomotimerViewModel.progressTimerIndicator.collectAsState()
    val pomodoroState by pomotimerViewModel.pomotimerState.collectAsState()
    val isDialogOpened by pomotimerViewModel.isDialogOpened.collectAsState()

    LaunchedEffect(key1 = timer, key2 = isTimerRunning) {
        if (timer > 0 && isTimerRunning) {
            delay(1000L)
            pomotimerViewModel.decreaseTimer()
        } else if (timer == 0) {
            pomotimerViewModel.triggerForegroundService(
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
                onDismissRequest = { pomotimerViewModel.closeAlertDialog() },
                onConfirmClick = { pomotimerViewModel.changePomotimerState() },
                pomotimerState = pomodoroState
            )
        }

        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> {
                PortraitLayout(
                    context = LocalContext.current,
                    pomotimerViewModel = pomotimerViewModel,
                    currentColor = currentColor,
                    innerPadding = innerPadding,
                    pomodoroState = pomodoroState,
                    onPomofocusButtonStateClick = {
                        if (isTimerRunning) {
                            pomotimerViewModel.openAlertDialog()
                        } else {
                            pomotimerViewModel.changePomotimerState()
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
                    pomotimerViewModel = pomotimerViewModel,
                    currentColor = currentColor,
                    innerPadding = innerPadding,
                    pomodoroState = pomodoroState,
                    onPomofocusButtonStateClick = {
                        if (isTimerRunning) {
                            pomotimerViewModel.openAlertDialog()
                        } else {
                            pomotimerViewModel.changePomotimerState()
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
