package com.example.pomofocus

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomofocus.ui.theme.BlackTransparent
import com.example.pomofocus.ui.theme.GreenShortBreak
import com.example.pomofocus.ui.theme.RedFocus
import com.example.pomofocus.ui.theme.WhiteTransparent
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
            Text(
                text = stringResource(id = R.string.app_name),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            )

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
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlackTransparent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(15),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        content = {
                            Text(
                                text = stringResource(id = R.string.btn_focus),
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            )
                        }
                    )
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(15),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        content = {
                            Text(
                                text = stringResource(id = R.string.btn_short_break),
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Normal,
                                )
                            )
                        }
                    )
//                    Button(
//                        onClick = {},
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color.Transparent,
//                            contentColor = Color.White
//                        ),
//                        shape = RoundedCornerShape(15),
//                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
//                        content = {
//                            Text(
//                                text = stringResource(id = R.string.btn_long_break),
//                                style = TextStyle(
//                                    fontSize = 20.sp,
//                                    fontWeight = FontWeight.Normal,
//                                )
//                            )
//                        }
//                    )
                }
                Box(
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .size(260.dp)
                        .background(WhiteTransparent),
                    contentAlignment = Alignment.Center
                ) {
                    Row {
                        AnimatedContent(
                            targetState = minutes,
                            label = "Decreasing minutes animation",
                            transitionSpec = { slideInVertically { -it } togetherWith slideOutVertically { it } }
                        ) { minutes ->
                            Text(
                                text = String.format("%02d", minutes),
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 60.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            )
                        }
                        Text(
                            text = ":",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 60.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        )
                        AnimatedContent(
                            targetState = seconds,
                            label = "Decreasing seconds animation",
                            transitionSpec = { slideInVertically { -it } togetherWith slideOutVertically { it } }
                        ) { seconds ->
                            Text(
                                text = String.format("%02d", seconds),
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 60.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            )
                        }
                    }

                    CircularProgressIndicator(
                        progress = { progressTimerIndicator },
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White,
                        strokeWidth = 5.dp,
                        trackColor = BlackTransparent,
                    )
                }
                Box(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            modifier = Modifier,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = currentColor
                            ),
                            shape = RoundedCornerShape(15),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                            onClick = {
                                if (isTimerRunning) {
                                    viewModel.pauseTimer()
                                } else {
                                    viewModel.startTimer()
                                }
                            },
                            content = {
                                val buttonText = if (isTimerRunning) {
                                    stringResource(id = R.string.btn_pause)
                                } else if (timer == totalTime) {
                                    stringResource(id = R.string.btn_start)
                                } else {
                                    stringResource(id = R.string.btn_resume)
                                }

                                Text(
                                    text = buttonText.uppercase(),
                                    style = TextStyle(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                )
                            }
                        )
                    }
//                    Box(
//                        modifier = Modifier.fillMaxWidth(0.9f),
//                        contentAlignment = Alignment.CenterEnd
//                    ) {
//                        IconButton(
//                            onClick = {}
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.PlayArrow,
//                                contentDescription = null,
//                                tint = Color.White
//                            )
//                        }
//                    }
                }
            }

            Text(
                text =
                    if (pomodoroState == PomodoroState.FOCUS) stringResource(id = R.string.txt_focus)
                    else stringResource(id = R.string.txt_break),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                )
            )
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen()
}