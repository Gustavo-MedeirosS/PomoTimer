package com.example.pomotimer.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import medeiros.dev.pomotimer.R
import com.example.pomotimer.ui.theme.RedFocus

@Composable
fun ChronometerButtons(
    currentColor: Color,
    isTimerRunning: Boolean,
    timer: Int,
    totalTime: Int,
    onMainButtonClick: () -> Unit,
    onPlayerNextClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CustomButton(
                onClick = { onMainButtonClick() },
                containerColor = Color.White,
                contentColor = currentColor,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
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

        if (timer != totalTime || isTimerRunning) {
            Box(
                modifier = Modifier.fillMaxWidth(0.9f),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton (
                    onClick = { onPlayerNextClick() }
                ) {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(id = R.drawable.player_next),
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChronometerButtonsPreview() {
    ChronometerButtons(
        currentColor = RedFocus,
        isTimerRunning = false,
        timer = 10,
        totalTime = 10,
        onMainButtonClick = {},
        onPlayerNextClick = {}
    )
}