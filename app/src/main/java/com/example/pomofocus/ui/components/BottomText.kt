package com.example.pomofocus.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.pomofocus.PomofocusState
import com.example.pomofocus.R

@Composable
fun BottomText(pomofocusState: PomofocusState) {
    Text(
        text =
        if (pomofocusState == PomofocusState.FOCUS) stringResource(id = R.string.txt_focus)
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
    BottomText(pomofocusState = PomofocusState.FOCUS)
}