package com.hane24.hoursarenotenough24.inoutlog

import android.util.Log
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.utils.NumberPicker
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils


@Composable
fun DateSelectDialog(
    year: Int,
    month: Int,
    onDismissRequest: () -> Unit,
    onConfirmation: (Int, Int) -> Unit
) {
    var yearValue by remember { mutableIntStateOf(year - 2022) }
    var monthValue by remember { mutableIntStateOf(if (year == 2022) month - 8 else month - 1) }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                        .padding(horizontal = 32.dp)
                ) {
                    NumberPicker(
                        minValue = 0,
                        maxValue = TodayCalendarUtils.year - 2022,
                        value = yearValue.let {
                            it
                        },
                        displayedValues = Array(TodayCalendarUtils.year - 2021) { "${2022 + it}년" },
                        setOnValueChangedListener = { new ->
                            yearValue = new
                            if (new == 0) monthValue = 0
                        },
                        modifier = Modifier
                            .weight(1f)
                    )
                    NumberPicker(
                        minValue = 0,
                        maxValue = when (yearValue) {
                            0 -> 4
                            TodayCalendarUtils.year - 2022 -> TodayCalendarUtils.month - 1
                            else -> 11
                        },
                        value = monthValue,
                        displayedValues = Array(
                            when (yearValue) {
                                0 -> 5
                                TodayCalendarUtils.year - 2022 -> TodayCalendarUtils.month
                                else -> 12
                            }
                        ) { "${if (yearValue == 0) 8 + it else 1 + it}월" },
                        modifier = Modifier
                            .weight(1f),
                        setOnValueChangedListener = { new ->
                            monthValue = new
                        }
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
                        modifier = Modifier
                            .size(32.dp)
                            .wrapContentHeight(align = Alignment.CenterVertically)
                            .clickable(
                                onClick = { onDismissRequest() }
                            )
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    Text(
                        text = "확인",
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(id = R.color.dialog_color),
                        modifier = Modifier
                            .size(32.dp)
                            .wrapContentHeight(align = Alignment.CenterVertically)
                            .clickable(
                                onClick = {
                                    onConfirmation(
                                        2022 + yearValue,
                                        monthValue + if (yearValue == 0) 8 else 1
                                    )
                                    onDismissRequest()
                                }
                            )
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun DateSelectDialogPreview() {
    DateSelectDialog(2023, 11, onDismissRequest = {}, onConfirmation = { _, _ -> })
}
