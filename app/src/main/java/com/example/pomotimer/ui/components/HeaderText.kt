package com.example.pomotimer.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.pomotimer.R

@Composable
fun HeaderText() {
    Text(
        text = stringResource(id = R.string.app_name),
        style = TextStyle(
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
        )
    )
}

@Preview
@Composable
private fun HeaderTextPreview() {
    HeaderText()
}