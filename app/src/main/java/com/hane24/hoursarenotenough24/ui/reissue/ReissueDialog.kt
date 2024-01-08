package com.hane24.hoursarenotenough24.ui.reissue

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hane24.hoursarenotenough24.R


@Composable
fun ReissueDialog(
    isApply: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    val message: String
    val subMessage: String
    val okButtonText: String
    if (isApply) {
        message = stringResource(id = R.string.reissue_dialog_main_warning_apply_text)
        subMessage = stringResource(id = R.string.reissue_dialog_sub_warning_apply_text)
        okButtonText = stringResource(id = R.string.reissue_dialog_apply_ok_text)
    } else {
        message = stringResource(id = R.string.reissue_dialog_main_warning_done_text)
        subMessage = stringResource(id = R.string.reissue_dialog_sub_warning_done_text)
        okButtonText = stringResource(id = R.string.reissue_dialog_done_ok_text)
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = message,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.default_text),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subMessage,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.front_gradient_end),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = {
                        onConfirmation()
                        onDismissRequest()
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(id = R.color.front_gradient_end)
                    ),
                    modifier = Modifier
                        .width(180.dp)
                ) {
                    Text(
                        text = okButtonText,
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 11.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { onDismissRequest() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(id = R.color.reissue_dialog_cancel)
                    ),
                    modifier = Modifier
                        .width(180.dp)
                ) {
                    Text(
                        text = "취소",
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.default_text),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 11.dp)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun ReissueDialogPreview() {
    ReissueDialog(isApply = true, onDismissRequest = {}, onConfirmation = {})
}

@Composable
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun ReissueDialogDonePreview() {
    ReissueDialog(isApply = false, onDismissRequest = {}, onConfirmation = {})
}
