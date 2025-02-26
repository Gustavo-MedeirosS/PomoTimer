package com.example.pomofocus.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomofocus.R
import com.example.pomofocus.ui.theme.BlackTransparent

@Composable
fun PomodoroStateButton(
    onClick: () -> Unit,
    isCurrentState: Boolean,
    @StringRes stringRes: Int
) {
    CustomButton(
        onClick = onClick,
        containerColor = if (isCurrentState) BlackTransparent else Color.Transparent,
        contentColor = Color.White,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        enabled = !isCurrentState,
        content = {
            Text(
                text = stringResource(id = stringRes),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = if (isCurrentState) FontWeight.SemiBold else FontWeight.Normal,
                )
            )
        }
    )
}

@Preview
@Composable
private fun PomodoroStateButtonPreview() {
    PomodoroStateButton(
        onClick = {},
        isCurrentState = false,
        stringRes = R.string.btn_short_break,
    )
}