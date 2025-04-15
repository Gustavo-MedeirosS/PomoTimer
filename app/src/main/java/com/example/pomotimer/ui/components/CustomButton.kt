package com.example.pomotimer.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import medeiros.dev.pomotimer.R
import com.example.pomotimer.ui.theme.RedFocus

@Composable
fun CustomButton(
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    contentPadding: PaddingValues,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Button(
        onClick = { onClick() },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor
        ),
        shape = RoundedCornerShape(15),
        contentPadding = contentPadding,
        content = { content() }
    )
}

@Preview
@Composable
private fun CustomButtonPreview() {
    CustomButton(
        onClick = {},
        containerColor = Color.White,
        contentColor = RedFocus,
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
}