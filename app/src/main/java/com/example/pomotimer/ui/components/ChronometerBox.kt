package com.example.pomotimer.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomotimer.ui.theme.BlackTransparent
import com.example.pomotimer.ui.theme.WhiteTransparent

@SuppressLint("DefaultLocale")
@Composable
fun ChronometerBox(
    minutes: Int,
    seconds: Int,
    progressTimerIndicator: Float
) {
    val circularAnimationState by animateFloatAsState(
        targetValue = progressTimerIndicator,
        label = "Circular Progress Animation"
    )

    Box(
        modifier = Modifier
            .clip(shape = CircleShape)
            .size(260.dp)
            .background(WhiteTransparent),
        contentAlignment = Alignment.Center
    ) {
        val minutesToString = String.format("%02d", minutes)
        val tenMinutes = minutesToString[0]
        val unityMinutes = minutesToString[1]
        val secondsToString = String.format("%02d", seconds)
        val tenSeconds  = secondsToString[0]
        val unitySeconds = secondsToString[1]

        Row {
            AnimatedContent(
                targetState = tenMinutes,
                label = "Decreasing ten minutes animation",
                transitionSpec = { slideInVertically { -it } togetherWith slideOutVertically { it } }
            ) { tenMinutes ->
                Text(
                    text = tenMinutes.toString(),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 60.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                )
            }
            AnimatedContent(
                targetState = unityMinutes,
                label = "Decreasing unity minutes animation",
                transitionSpec = { slideInVertically { -it } togetherWith slideOutVertically { it } }
            ) { unityMinutes ->
                Text(
                    text = unityMinutes.toString(),
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
                targetState = tenSeconds ,
                label = "Decreasing ten seconds animation",
                transitionSpec = { slideInVertically { -it } togetherWith slideOutVertically { it } }
            ) { tenSeconds  ->
                Text(
                    text = tenSeconds .toString(),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 60.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                )
            }
            AnimatedContent(
                targetState = unitySeconds,
                label = "Decreasing unity seconds animation",
                transitionSpec = { slideInVertically { -it } togetherWith slideOutVertically { it } }
            ) { unitySeconds ->
                Text(
                    text = unitySeconds.toString(),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 60.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                )
            }
        }

        CircularProgressIndicator(
            progress = { circularAnimationState },
            modifier = Modifier.fillMaxSize(),
            color = Color.White,
            strokeWidth = 5.dp,
            trackColor = BlackTransparent,
        )
    }
}

@Preview
@Composable
private fun ChronometerBoxPreview() {
    ChronometerBox(
        minutes = 10,
        seconds = 0,
        progressTimerIndicator = 0.5f,
    )
}