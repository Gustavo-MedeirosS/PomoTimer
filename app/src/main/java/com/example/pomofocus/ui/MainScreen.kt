package com.example.pomofocus.ui

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
import com.example.pomofocus.Constants
import com.example.pomofocus.PomofocusState
import com.example.pomofocus.service.PomofocusService
import com.example.pomofocus.service.ServiceHelper
import com.example.pomofocus.ui.layout.LandscapeLayout
import com.example.pomofocus.ui.layout.PortraitLayout
import com.example.pomofocus.ui.theme.GreenShortBreak
import com.example.pomofocus.ui.theme.RedFocus
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@Composable
fun MainScreen(
    pomofocusService: PomofocusService,
    windowSizeClass: WindowSizeClass
) {
    val context = LocalContext.current
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

        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> {
                PortraitLayout(
                    context = LocalContext.current,
                    currentColor = currentColor,
                    innerPadding = innerPadding,
                    pomodoroState = pomodoroState,
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