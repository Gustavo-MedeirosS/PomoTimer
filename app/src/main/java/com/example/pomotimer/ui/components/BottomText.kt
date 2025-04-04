package com.example.pomotimer.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.pomotimer.ui.PomotimerState
import com.example.pomotimer.R

@Composable
fun BottomText(pomotimerState: PomotimerState) {
    Text(
        text =
        if (pomotimerState == PomotimerState.FOCUS) stringResource(id = R.string.txt_focus)
        else stringResource(id = R.string.txt_break),
        style = TextStyle(
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
        )
    )
}

@Preview
@Composable
private fun BottomTextPreview() {
    BottomText(pomotimerState = PomotimerState.FOCUS)
}