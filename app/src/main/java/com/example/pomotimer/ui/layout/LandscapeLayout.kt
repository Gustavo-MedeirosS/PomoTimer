package com.example.pomotimer.ui.layout

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pomotimer.Constants
import com.example.pomotimer.ui.PomotimerState
import com.example.pomotimer.ui.view_model.PomotimerViewModel
import medeiros.dev.pomotimer.R
import com.example.pomotimer.ui.components.BottomText
import com.example.pomotimer.ui.components.ChronometerBox
import com.example.pomotimer.ui.components.ChronometerButtons
import com.example.pomotimer.ui.components.HeaderText
import com.example.pomotimer.ui.components.PomodoroStateButton
import com.example.pomotimer.ui.theme.RedFocus

@Composable
fun LandscapeLayout(
    context: Context,
    pomotimerViewModel: PomotimerViewModel,
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(currentColor)
            .padding(innerPadding),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            HeaderText()
        }
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(fraction = 0.5f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ChronometerBox(
                        minutes = minutes,
                        seconds = seconds,
                        progressTimerIndicator = progressTimerIndicator
                    )
                }

                Column(
                    modifier = Modifier.height(260.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
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
                    ChronometerButtons(
                        currentColor = currentColor,
                        isTimerRunning = isTimerRunning,
                        timer = timer,
                        totalTime = totalTime,
                        onMainButtonClick = {
                            if (isTimerRunning) {
                                pomotimerViewModel.triggerForegroundService(
                                    context = context,
                                    action = Constants.ACTION_SERVICE_PAUSE
                                )
                            } else {
                                pomotimerViewModel.triggerForegroundService(
                                    context = context,
                                    action = Constants.ACTION_SERVICE_START
                                )
                            }
                        },
                        onPlayerNextClick = {
                            pomotimerViewModel.triggerForegroundService(
                                context = context,
                                action = Constants.ACTION_SERVICE_FINISH
                            )
                        }
                    )
                    BottomText(pomotimerState = pomodoroState)
                }
            }
        }
    }
}

@Preview(widthDp = 800, heightDp = 300)
@Composable
private fun LandscapeLayoutPreview() {
    LandscapeLayout(
        context = LocalContext.current,
        pomotimerViewModel = hiltViewModel(),
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