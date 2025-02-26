package com.example.pomofocus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pomofocus.PomofocusState
import com.example.pomofocus.R
import com.example.pomofocus.ui.theme.GreenShortBreak
import com.example.pomofocus.ui.theme.RedFocus

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    pomofocusState: PomofocusState
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
        )
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(15))
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.txt_alert_change_to_break),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
                ) {
                TextButton(onClick = onDismissRequest) {
                    Text(
                        text = stringResource(id = R.string.btn_cancel),
                        fontSize = 20.sp,
                        color = Color.Gray
                    )
                }
                TextButton(onClick = onConfirmClick) {
                    Text(
                        text = stringResource(id = R.string.btn_confirm),
                        fontSize = 20.sp,
                        color = if (pomofocusState == PomofocusState.FOCUS) RedFocus else GreenShortBreak
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun AlertDialogPreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AlertDialog({}, {}, PomofocusState.FOCUS)
    }
}