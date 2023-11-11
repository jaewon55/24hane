package com.hane24.hoursarenotenough24.inoutlog

import android.content.res.Configuration
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import com.hane24.hoursarenotenough24.App
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.data.TagLog
import com.hane24.hoursarenotenough24.database.TimeDatabase
import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.repository.TimeDBRepository
import com.hane24.hoursarenotenough24.repository.TimeServerRepository
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtilss
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils.isToday
import com.hane24.hoursarenotenough24.utils.calculateDaysOfMonth
import com.hane24.hoursarenotenough24.utils.clickableWithoutRipple
import com.hane24.hoursarenotenough24.utils.getDayOfWeekString
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
private fun CalendarPageHeader(
    modifier: Modifier = Modifier,
    year: Int,
    month: Int,
    loadingState: Boolean,
    updateLogs: (Int, Int, Int) -> Unit = { _, _, _ -> }
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "last month",
            tint = colorResource(
                id = if (!loadingState && !(year == 2022 && month == 8)) {
                    R.color.calendar_button_color
                } else {
                    R.color.calendar_button_disable_color
                }
            ),
            modifier = Modifier
                .clickableWithoutRipple(
                    enabled = !loadingState && !(year == 2022 && month == 8),
                    onClick = {
                        var newYear = year
                        val newMonth = month
                            .minus(1)
                            .let {
                                if (it == 0) {
                                    --newYear
                                    12
                                } else {
                                    it
                                }
                            }
                        updateLogs(newYear, newMonth, 1)
                    }
                )
                .padding(start = 20.dp, end = 50.dp)
                .size(24.dp)
        )
        Text(text = "$year.$month",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = colorResource(id = R.color.etc_title_color),
            modifier = Modifier
                .clickableWithoutRipple {
                    Toast
                        .makeText(App.instance.applicationContext, "text", Toast.LENGTH_SHORT)
                        .show()
                }
                .padding(horizontal = 20.dp)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "next month",
            tint = colorResource(
                id = if (!loadingState && !isToday(year, month, TodayCalendarUtils.day)) {
                    R.color.calendar_button_color
                } else {
                    R.color.calendar_button_disable_color
                }
            ),
            modifier = Modifier
                .clickableWithoutRipple(
                    enabled = !loadingState && !isToday(year, month, TodayCalendarUtils.day),
                    onClick = {
                        var newYear = year
                        val newMonth = month
                            .plus(1)
                            .let {
                                if (12 < it) {
                                    ++newYear
                                    1
                                } else {
                                    it
                                }
                            }
                        updateLogs(newYear, newMonth, 1)
                    },
                )
                .padding(start = 50.dp, end = 20.dp)
                .size(24.dp)
        )
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
            Text(
                text = text,
                fontSize = 14.sp,
                color = colorResource(id = R.color.calendar_week_of_day_text)
            )
        }
    }
}

@Composable
private fun CalendarItem(
    item: CalendarItem,
    dayOnClick: (Int?) -> Unit,
) {
    Text(
        text = item.dayText,
        fontSize = 14.sp,
        color = colorResource(id = R.color.etc_title_color),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .clickableWithoutRipple(
                onClick = { dayOnClick(item.dayText.toIntOrNull()) }
            )
            .background(colorResource(id = item.background), RoundedCornerShape(10.dp))
            .size(40.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
    )
}

@Composable
private fun CalendarSelectedItem(
    item: CalendarItem,
    dayOnClick: (Int?) -> Unit,
) {
    Text(
        text = item.dayText,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = colorResource(id = R.color.selected_text_color),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .clickableWithoutRipple(
                onClick = { dayOnClick(item.dayText.toIntOrNull()) }
            )
            .background(
                colorResource(id = R.color.selected_background_color), CircleShape
            )
            .size(40.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
    )
}

@Composable
private fun CalendarNextDayItem(
    item: CalendarItem,
) {
    Text(
        text = item.dayText,
        fontSize = 14.sp,
        color = colorResource(id = R.color.next_day_text),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .background(Color.Transparent)
            .size(40.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
    )
}

@Composable
private fun CalendarTodayItem(
    item: CalendarItem,
    dayOnClick: (Int?) -> Unit,
) {
    Text(
        text = item.dayText,
        fontSize = 14.sp,
        color = colorResource(id = R.color.today_select_color),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .clickableWithoutRipple(onClick = { dayOnClick(item.dayText.toIntOrNull()) })
            .background(Color.Transparent)
            .border(
                2.dp,
                colorResource(id = R.color.today_select_color),
                RoundedCornerShape(10.dp)
            )
            .size(40.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
    )
}

@Composable
private fun LogCalendarRow(
    modifier: Modifier = Modifier,
    items: List<CalendarItem>,
    dayOnClick: (Int?) -> Unit,
    year: Int,
    month: Int,
    day: Int
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        for (item in items) {
            val itemDate = item.dayText.toIntOrNull()
            val isThisMonth = year == TodayCalendarUtils.year && month == TodayCalendarUtils.month
            when {
                itemDate == null -> Spacer(modifier = Modifier.size(40.dp))
                isThisMonth && itemDate > TodayCalendarUtils.day -> CalendarNextDayItem(item = item)
                day == itemDate -> CalendarSelectedItem(item = item, dayOnClick = dayOnClick)
                isThisMonth && itemDate == TodayCalendarUtils.day -> CalendarTodayItem(
                    item = item, dayOnClick = dayOnClick
                )

                else -> CalendarItem(item = item, dayOnClick = dayOnClick)
            }
        }
    }
}

@Composable
private fun LogCalendarGrid(
    modifier: Modifier = Modifier,
    gridItems: List<CalendarItem>,
    dayOnClick: (Int?) -> Unit,
    year: Int,
    month: Int,
    day: Int,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        var fromIndex = 0
        var toIndex = 7
        while (fromIndex < gridItems.size) {
            val items = gridItems.subList(fromIndex, toIndex).also {
                fromIndex = toIndex
                toIndex = if (gridItems.size < toIndex + 7) gridItems.size else toIndex + 7
            }
            LogCalendarRow(
                items = items, dayOnClick = dayOnClick, year = year, month = month, day = day
            )
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
private fun LogTableOfDay(
    modifier: Modifier = Modifier,
    year: Int,
    month: Int,
    day: Int,
    logs: List<TagLog>,
    inOutState: Boolean
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = PaddingValues(vertical = 2.dp),
        modifier = modifier
    ) {
        items(logs) { log ->
            val inTimeText = parseInOutTimeStamp(log.inTimeStamp)
            val outTimeText = parseInOutTimeStamp(log.outTimeStamp)
            val isMissing =
                { !inOutState || !isToday(year, month, day) || logs.lastOrNull() != log }
            val durationSecondText = log.durationSecond?.let { parseDurationSecond(it) }
                ?: if (isMissing()) "누락" else "-"

            Row(/*TODO 누락시 배경 설정 로직 필요*/
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth().height(26.dp).let {
                    if (durationSecondText != "누락") {
                        it
                    } else {
                        it.background(
                            color = colorResource(id = R.color.missing_background),
                            RoundedCornerShape(20.dp)
                        )
                    }
                }) {
                Text(
                    text = inTimeText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.etc_title_color),
                    modifier = Modifier.width(80.dp)
                )
                Text(
                    text = outTimeText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.etc_title_color),
                    modifier = Modifier.width(80.dp)
                )
                Text(
                    text = durationSecondText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.etc_title_color),
                    modifier = Modifier.width(80.dp)
                )
            }
        }
    }
}

//@Composable
//private fun LogCalendarLayout(
//    modifier: Modifier = Modifier,
//    year: Int,
//    month: Int,
//    day: Int,
//    loadingState
//) {
//    Column(modifier = modifier.padding(horizontal = 20.dp)) {
//        CalendarPageHeader(
//            year = viewModel.year,
//            month = viewModel.month,
//            loadingState = loadingState,
//            updateLogs = { y, m, d -> viewModel.updateLogs(y, m, d) }
//        )
//        DayOfWeekRow()
//        LogCalendarGrid(
//            gridItems = viewModel.tagLogs.asCalendarItems(viewModel.year, viewModel.month),
//            dayOnClick = { viewModel.updateDay(it) },
//            year = viewModel.year,
//            month = viewModel.month,
//            day = viewModel.day
//        )
//        if (loadingState) {
//        } else {/* TODO loading animation view */
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        AccumulationTimeCard(
//            text = "총 " + parseAccumulationTime(viewModel.tagLogs.sumOf { it.durationSecond ?: 0L })
//        )
//        Spacer(modifier = Modifier.height(20.dp))
//        TableDateAndAccumulationTime(
//            dateText = "${viewModel.month}.${viewModel.day} " + getDayOfWeekString(
//                viewModel.year, viewModel.month, viewModel.day
//            ), accumulationTimeText = parseAccumulationTime(viewModel.tagLogsOfTheDay.sumOf {
//                it.durationSecond ?: 0L
//            })
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//        LogTableOfDayHeader()
//        LogTableOfDay(
//            logs = viewModel.tagLogsOfTheDay.reversed(),
//            year = viewModel.year,
//            month = viewModel.month,
//            day = viewModel.day,
//            inOutState = viewModel.inOutState
//        )
//    }
//
//}

@Composable
fun LogCalendarScreen(modifier: Modifier = Modifier, viewModel: LogViewModel) {
    val loadingState by viewModel.loadingState.collectAsStateWithLifecycle()

    Column(modifier = modifier.padding(horizontal = 20.dp)) {
        CalendarPageHeader(year = viewModel.year,
            month = viewModel.month,
            loadingState = loadingState,
            updateLogs = { y, m, d -> viewModel.updateLogs(y, m, d) })
        DayOfWeekRow()
        LogCalendarGrid(
            gridItems = viewModel.tagLogs.asCalendarItems(viewModel.year, viewModel.month),
            dayOnClick = { viewModel.updateDay(it) },
            year = viewModel.year,
            month = viewModel.month,
            day = viewModel.day
        )
        if (loadingState) {
        } else {/* TODO loading animation view */
        }
        Spacer(modifier = Modifier.height(16.dp))
        AccumulationTimeCard(
            text = "총 " + parseAccumulationTime(viewModel.tagLogs.sumOf { it.durationSecond ?: 0L })
        )
        Spacer(modifier = Modifier.height(20.dp))
        TableDateAndAccumulationTime(
            dateText = "${viewModel.month}.${viewModel.day} " + getDayOfWeekString(
                viewModel.year, viewModel.month, viewModel.day
            ), accumulationTimeText = parseAccumulationTime(viewModel.tagLogsOfTheDay.sumOf {
                it.durationSecond ?: 0L
            })
        )
        Spacer(modifier = Modifier.height(8.dp))
        LogTableOfDayHeader()
        LogTableOfDay(
            logs = viewModel.tagLogsOfTheDay.reversed(),
            year = viewModel.year,
            month = viewModel.month,
            day = viewModel.day,
            inOutState = viewModel.inOutState
        )
    }
}


@Composable
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun HeaderPreview() {
    CalendarPageHeader(
        year = TodayCalendarUtils.year, month = TodayCalendarUtils.month, loadingState = false
    )
}

@Composable
@Preview(showBackground = true)
private fun DayOfWeekRowPreview() {
    DayOfWeekRow()
}

@Composable
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun CalendarItemPreview() {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Spacer(modifier = Modifier.size(40.dp))
        CalendarItem(CalendarItem("1", R.color.calendar_color1), {})
        CalendarSelectedItem(CalendarItem("2", R.color.calendar_color1), {})
        CalendarTodayItem(CalendarItem("3", R.color.calendar_color1), {})
        CalendarNextDayItem(CalendarItem("4", R.color.calendar_color1))
    }
}

@Composable
@Preview(showBackground = true)
private fun LogCalendarRowItemPreview() {
    val items = listOf(
        CalendarItem("", R.color.transparent),
        CalendarItem("", R.color.transparent),
        CalendarItem("", R.color.transparent),
        CalendarItem("1", R.color.calendar_color1),
        CalendarItem("2", R.color.calendar_color2),
        CalendarItem("3", R.color.calendar_color3),
        CalendarItem("4", R.color.calendar_color4),
    )
    LogCalendarRow(items = items, dayOnClick = {}, year = 2023, month = 11, day = 1)
}

@Composable
@Preview(showBackground = true)
private fun LogCalendarPreview() {
    val colorArray = arrayOf(
        R.color.calendar_color1,
        R.color.calendar_color2,
        R.color.calendar_color3,
        R.color.calendar_color4,
    )
    val gridItems = List(35) { i ->
        if (i < 3 || 32 < i) {
            CalendarItem("", R.color.transparent)
        } else {
            CalendarItem("${i - 2}", colorArray.random())
        }
    }
    LogCalendarGrid(gridItems = gridItems, dayOnClick = {}, year = 2023, month = 11, day = 1)
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
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun LogTableOfDayPreview() {
    val logs = listOf(
        TagLog(1698925447, 1698928450, null),
        TagLog(1698923101, 1698924982, 1881),
        TagLog(1698922413, 1698923048, 635),
        TagLog(1698915178, 1698922126, null),
        TagLog(1698907081, 1698914835, 7754),
        TagLog(1698903787, 1698906701, 2914)
    )
    LogTableOfDay(
        logs = logs.reversed(),
        year = TodayCalendarUtils.year,
        month = TodayCalendarUtils.month,
        day = TodayCalendarUtils.day,
        inOutState = true
    )
}

@Composable
@Preview(showBackground = true)
private fun CalendarPagePreview() {
    val sharedPreferenceUtilss = SharedPreferenceUtilss.initialize(
        LocalContext.current
    )
    sharedPreferenceUtilss.saveAccessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoxMDAwMDQsImVtYWlsIjoiamFld2Nob2lAc3R1ZGVudC40MnNlb3VsLmtyIiwibG9naW4iOiJqYWV3Y2hvaSIsImltYWdlX3VybCI6Imh0dHBzOi8vY2RuLmludHJhLjQyLmZyL3VzZXJzLzhhOWVlNWQ2ZmQyM2RlNDgxMWJkYjczYzczY2Y4ZDVlL2phZXdjaG9pLmpwZyIsImlzX3N0YWZmIjpmYWxzZSwiaWF0IjoxNjk5NDQ2MDAzLCJleHAiOjE3MDAwNTA4MDN9._h0QqGfocZgBL3IbbzuZgOgl9asYKczjzWj9Gd1ewOU")
    val viewModel = LogViewModel(
        TimeServerRepository(
            Hane24Apis.hane24ApiService, sharedPreferenceUtilss
        ), TimeDBRepository(
            Room.inMemoryDatabaseBuilder(
                LocalContext.current, TimeDatabase::class.java
            ).build()
        )
    )
    LogCalendarScreen(viewModel = viewModel)
}

private data class CalendarItem(
    val dayText: String,
    @ColorRes val background: Int,
)

private fun parseAccumulationTime(time: Long): String {
    var second = time
    val hour = second / 3600
    second -= hour * 3600
    val min = second / 60
    return String.format("%d시간 %d분", hour, min)
}

private fun parseDurationSecond(durationSecond: Long): String {
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
    return dateFormat.format(timeStamp * 1000).split(' ').let { "${it[1]}:${it[2]}:${it[3]}" }
}

private fun List<TagLog>.asCalendarItems(year: Int, month: Int): List<CalendarItem> {
    val calendar = Calendar.getInstance()
    val durationSecondArray = Array(32) { 0L }
    this.map { tagLog ->
        if (tagLog.inTimeStamp != null) {
            calendar.timeInMillis = tagLog.inTimeStamp * 1000
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            durationSecondArray[day] += tagLog.durationSecond ?: 0
        }
    }
    calendar.set(year, month - 1, 1)
    val itemCount = calendar.calculateDaysOfMonth() + (7 - calendar.calculateDaysOfMonth() % 7)
    val startIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1
    val lastIndex = startIndex + calendar.calculateDaysOfMonth() - 1
    return List(itemCount) {
        if (it < startIndex || lastIndex < it) {
            CalendarItem("", R.color.transparent)
        } else {
            val day = it - startIndex + 1
            CalendarItem(
                day.toString(), when {
                    durationSecondArray[day] == 0L -> R.color.transparent
                    durationSecondArray[day] <= 3L * 3600 -> R.color.calendar_color1
                    durationSecondArray[day] <= 6L * 3600 -> R.color.calendar_color2
                    durationSecondArray[day] <= 9L * 3600 -> R.color.calendar_color3
                    else -> R.color.calendar_color4
                }
            )
        }
    }
}