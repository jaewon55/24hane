package com.hane24.hoursarenotenough24.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hane24.hoursarenotenough24.R

@Composable
fun InfoModalDialog(
    mainMessage: String,
    subMessage: String,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, bottom = 30.dp, start = 40.dp, end = 40.dp)
            ) {
                Text(
                    text = mainMessage,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subMessage,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.dialog_color),
                    fontSize = 14.sp,
                )
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = { onDismissRequest() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xEA, 0xEA, 0xEA)
                    ),
                    modifier = Modifier.width(220.dp)
                ) {
                    Text(
                        text = "닫기",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun InfoModalDialogPreview() {
    InfoModalDialog("입실 중 이용 시간은 \n" +
            "실제 기록과 다를 수 있습니다.", "입실 / 퇴실 태깅에 유의해주세요.", {})
}
