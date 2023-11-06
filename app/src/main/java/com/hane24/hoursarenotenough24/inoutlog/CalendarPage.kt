package com.hane24.hoursarenotenough24.inoutlog

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import com.hane24.hoursarenotenough24.App
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.network.InOutLog
import java.text.SimpleDateFormat
import java.util.Locale

private data class CalendarItem(
    val dayText: String,
    val background: Color,
)

@Composable
private fun CalendarPageHeader(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        IconButton(
            onClick = {
                Toast.makeText(App.instance.applicationContext, "left", Toast.LENGTH_SHORT).show()
            },
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "last month",
                modifier = Modifier
                    .padding(end = 50.dp)
                    .size(24.dp)
            )
        }
        TextButton(
            onClick = {
                Toast.makeText(App.instance.applicationContext, "text", Toast.LENGTH_SHORT).show()
            },
        ) {
            Text(
                text = "2023.11",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
        IconButton(
            onClick = {
                Toast.makeText(App.instance.applicationContext, "right", Toast.LENGTH_SHORT).show()
            },
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "next month",
                modifier = Modifier
                    .padding(start = 50.dp)
                    .size(24.dp)
            )
        }
    }
}

@Composable
private fun DayOfWeekRow(modifier: Modifier = Modifier) {
    val dayOfWeekStringArray = arrayOf("일", "월", "화", "수", "목", "금", "토")
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        for (text in dayOfWeekStringArray) {
            Text(text = text, fontSize = 14.sp)
        }
    }
}

@Composable
private fun LogCalendarRow(modifier: Modifier = Modifier, items: List<CalendarItem>) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        for (item in items) {
            TextButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .background(
                        item.background, RoundedCornerShape(10.dp)
                    )
                    .size(40.dp)
            ) {
                Text(
                    text = item.dayText,
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun LogCalendarGrid(modifier: Modifier = Modifier, gridItems: List<CalendarItem>) {
    Column(modifier = modifier.fillMaxWidth()) {
        var fromIndex = 0
        var toIndex = 7
        while (fromIndex < gridItems.size) {
            val items = gridItems.subList(fromIndex, toIndex).also {
                fromIndex = toIndex
                toIndex = if (gridItems.size < toIndex + 7) gridItems.size else toIndex + 7
            }
            LogCalendarRow(items = items)
        }
    }
}

@Composable
private fun AccumulationTimeCard(modifier: Modifier = Modifier, text: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, colorResource(id = R.color.etc_title_color), RoundedCornerShape(10.dp))
            .background(colorResource(id = R.color.default_text), RoundedCornerShape(10.dp))
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun TableDateAndAccumulationTime(
    modifier: Modifier = Modifier, dateText: String, accumulationTimeText: String
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = dateText,
            fontSize = 14.sp,
            color = colorResource(id = R.color.log_list_accumulation_color)
        )
        Text(
            text = accumulationTimeText,
            fontSize = 14.sp,
            color = colorResource(id = R.color.log_list_accumulation_color)
        )
    }
}

@Composable
private fun LogTableOfDayHeader(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "입실",
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = "퇴실",
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = "체류시간",
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
        )
    }
}

@Composable
private fun LogTableOfDay(modifier: Modifier = Modifier, logs: List<InOutLog>) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = PaddingValues(vertical = 2.dp),
        modifier = modifier
    ) {
        items(logs) { log ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth().height(26.dp)
            ) {
                Text(
                    text = parseInOutTimeStamp(log.inTimeStamp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.etc_title_color),
                    modifier = Modifier.width(80.dp)
                )
                Text(
                    text = parseInOutTimeStamp(log.outTimeStamp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.etc_title_color),
                    modifier = Modifier.width(80.dp)
                )
                Text(
                    text = parseDurationSecond(log.durationSecond),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.etc_title_color),
                    modifier = Modifier.width(80.dp)
                )
            }
        }
    }
}

@Composable
fun CalendarPage(modifier: Modifier = Modifier) {
    val colorArray = arrayOf(
        colorResource(id = R.color.calendar_color1),
        colorResource(id = R.color.calendar_color2),
        colorResource(id = R.color.calendar_color3),
        colorResource(id = R.color.calendar_color4),
    )
    val gridItems = List(35) { i ->
        if (i < 3 || 32 < i) {
            CalendarItem("", Color.Transparent)
        } else {
            CalendarItem("${i - 2}", colorArray.random())
        }
    }
    val logs = listOf(
        InOutLog(1698925447, 1698928450, 3003),
        InOutLog(1698923101, 1698924982, 1881),
        InOutLog(1698922413, 1698923048, 635),
        InOutLog(1698915178, 1698922126, 6948),
        InOutLog(1698907081, 1698914835, 7754),
        InOutLog(1698903787, 1698906701, 2914)
    )
    Column(modifier = modifier.padding(horizontal = 20.dp)) {
        CalendarPageHeader()
        DayOfWeekRow()
        LogCalendarGrid(gridItems = gridItems)
        Spacer(modifier = Modifier.height(16.dp))
        AccumulationTimeCard(text = "총 " + parseAccumulationTime(23135))
        Spacer(modifier = Modifier.height(20.dp))
        TableDateAndAccumulationTime(
            dateText = "11.2 목요일",
            accumulationTimeText = parseAccumulationTime(23135)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LogTableOfDayHeader()
        LogTableOfDay(logs = logs.reversed())
    }
}


@Composable
@Preview(showBackground = true)
private fun HeaderPreview() {
    CalendarPageHeader()
}

@Composable
@Preview(showBackground = true)
private fun DayOfWeekRowPreview() {
    DayOfWeekRow()
}

@Composable
@Preview(showBackground = true)
private fun LogCalendarRowItemPreview() {
    val items = listOf(
        CalendarItem("", Color.Transparent),
        CalendarItem("", Color.Transparent),
        CalendarItem("", Color.Transparent),
        CalendarItem("1", colorResource(id = R.color.calendar_color1)),
        CalendarItem("2", colorResource(id = R.color.calendar_color2)),
        CalendarItem("3", colorResource(id = R.color.calendar_color3)),
        CalendarItem("4", colorResource(id = R.color.calendar_color4)),
    )
    LogCalendarRow(items = items)
}

@Composable
@Preview(showBackground = true)
private fun LogCalendarPreview() {
    val colorArray = arrayOf(
        colorResource(id = R.color.calendar_color1),
        colorResource(id = R.color.calendar_color2),
        colorResource(id = R.color.calendar_color3),
        colorResource(id = R.color.calendar_color4),
    )
    val gridItems = List(35) { i ->
        if (i < 3 || 32 < i) {
            CalendarItem("", Color.Transparent)
        } else {
            CalendarItem("${i - 2}", colorArray.random())
        }
    }
    LogCalendarGrid(gridItems = gridItems)
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun AccumulationTimeCardPreview() {
    AccumulationTimeCard(text = "총 " + parseAccumulationTime(23135))
}

@Composable
@Preview(showBackground = true)
private fun TableDateAndAccumulationTimePreview() {
    TableDateAndAccumulationTime(
        dateText = "11. 2 금요일", accumulationTimeText = parseAccumulationTime(23135)
    )
}

@Composable
@Preview(showBackground = true)
private fun LogTableOfDayHeaderPreview() {
    LogTableOfDayHeader()
}

@Composable
@Preview(showBackground = true)
private fun LogTableOfDayPreview() {
    val logs = listOf(
        InOutLog(1698925447, 1698928450, 3003),
        InOutLog(1698923101, 1698924982, 1881),
        InOutLog(1698922413, 1698923048, 635),
        InOutLog(1698915178, 1698922126, 6948),
        InOutLog(1698907081, 1698914835, 7754),
        InOutLog(1698903787, 1698906701, 2914)
    )
    LogTableOfDay(logs = logs.reversed())
}

@Composable
@Preview(showBackground = true)
private fun CalendarPagePreview() {
    CalendarPage()
}

private fun parseAccumulationTime(time: Long): String {
    var second = time
    val hour = second / 3600
    second -= hour * 3600
    val min = second / 60
    return String.format("%d시간 %d분", hour, min)
}

private fun parseDurationSecond(durationSecond: Long?): String {
    /*TODO 누락 인지 확인 하는 로직 필요*/
    if (durationSecond == null) return "-"
    var second = durationSecond
    val hour = second / 3600
    second -= hour * 3600
    val min = second / 60
    second -= min * 60
    return String.format("%02d:%02d:%02d", hour, min, second)
}

private fun parseInOutTimeStamp(timeStamp: Long?): String {
    val dateFormat = SimpleDateFormat("dd HH mm ss", Locale("ko", "KR"))
    if (timeStamp == null) return "-"
    return dateFormat.format(timeStamp * 1000).split(' ')
        .let { "${it[1]}:${it[2]}:${it[3]}" }
}
