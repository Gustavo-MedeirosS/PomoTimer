package com.example.pomotimer.ui.layout

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pomotimer.Constants
import com.example.pomotimer.PomotimerState
import com.example.pomotimer.R
import com.example.pomotimer.service.ServiceHelper
import com.example.pomotimer.ui.components.BottomText
import com.example.pomotimer.ui.components.ChronometerBox
import com.example.pomotimer.ui.components.ChronometerButtons
import com.example.pomotimer.ui.components.HeaderText
import com.example.pomotimer.ui.components.PomodoroStateButton
import com.example.pomotimer.ui.theme.RedFocus

@Composable
fun PortraitLayout(
    context: Context,
    currentColor: Color,
    innerPadding: PaddingValues,
    pomodoroState: PomotimerState,
    onPomofocusButtonStateClick: () -> Unit,
    isTimerRunning: Boolean,
    totalTime: Int,
    timer: Int,
    minutes: Int,
    seconds: Int,
    progressTimerIndicator: Float,
) {
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
                    onClick = onPomofocusButtonStateClick,
                    isCurrentState = pomodoroState == PomotimerState.FOCUS,
                    stringRes = R.string.btn_focus
                )
                PomodoroStateButton(
                    onClick = onPomofocusButtonStateClick,
                    isCurrentState = pomodoroState == PomotimerState.SHORT_BREAK,
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

        BottomText(pomotimerState = pomodoroState)
    }
}

@Preview
@Composable
private fun PortraitLayoutPreview() {
    PortraitLayout(
        context = LocalContext.current,
        currentColor = RedFocus,
        innerPadding = PaddingValues(),
        pomodoroState = PomotimerState.FOCUS,
        onPomofocusButtonStateClick = {},
        isTimerRunning = true,
        totalTime = 25,
        timer = 5,
        minutes = 0,
        seconds = 20,
        progressTimerIndicator = 0.2F,
    )
}