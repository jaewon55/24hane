package com.hane24.hoursarenotenough24.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.utils.NumberPicker

@Composable
fun TargetTimeModalDialog(
    currentTargetTime: Int,
    onDismissRequest: () -> Unit,
    onConfirmation: (Int) -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            var targetTimeValue by remember { mutableIntStateOf(currentTargetTime - 12) }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                        .padding(horizontal = 32.dp)
                ) {
                    NumberPicker(
                        minValue = 0,
                        maxValue = 12,
                        value = targetTimeValue,
                        displayedValues = Array(13) { "${it + 12} 시간" },
                        setOnValueChangedListener = { new ->
                            targetTimeValue = new
                        },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Text(
                        text = "취소",
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(id = R.color.dialog_color),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clickable(
                                onClick = { onDismissRequest() }
                            )
                            .size(32.dp)
                            .wrapContentHeight(align = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    Text(
                        text = "확인",
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(id = R.color.dialog_color),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    onConfirmation(targetTimeValue + 12)
                                    onDismissRequest()
                                }
                            )
                            .size(32.dp)
                            .wrapContentHeight(align = Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun TargetTimeModalDialogPreview() {
    TargetTimeModalDialog(currentTargetTime = 12, onDismissRequest = { /*TODO*/ }, onConfirmation = {})
}



