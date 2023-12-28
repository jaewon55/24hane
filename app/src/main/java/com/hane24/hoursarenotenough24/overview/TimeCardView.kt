package com.hane24.hoursarenotenough24.overview

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.utils.clickableWithoutRipple
import kotlinx.coroutines.delay


@Composable
private fun CustomText(
    text: String,
    color: Color,
    fontSize: TextUnit,
) {
    Text(text = text, fontSize = fontSize, fontWeight = FontWeight.Bold, color = color)
}

@Composable
fun ExpandedAnimationCard(
    expandedHeight: Dp,
    background: Color,
    content: @Composable (Modifier, Color) -> Unit,
    contentBehind: @Composable (Modifier) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var rotationState by remember { mutableIntStateOf(0) }
    val rotationAngle by animateFloatAsState(targetValue = if (expanded) 90f else 0f)
    val cardBackground = if (expanded) Color.White else background

    Card(
        backgroundColor = cardBackground,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .height(if (expanded) expandedHeight else 80.dp)
            .padding(1.dp)
            .clickableWithoutRipple {
                expanded = !expanded
                rotationState = (rotationState + 1) % 4
            },
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                content(
                    Modifier.fillMaxWidth(0.93f),
                    if (cardBackground == Color.White) Color(0xFF333333) else Color.White
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = if (cardBackground == Color.White) Color(0xFF9B9797) else Color.White,
                    modifier = Modifier.rotate(rotationAngle),
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            if (expanded) {
                contentBehind(Modifier.fillMaxWidth(0.93f))
            }
        }
    }
}

@Composable
fun ContentOfDayTimeCard(
    modifier: Modifier = Modifier,
    contentColor: Color,
    mainMessage: String,
    subMessage: String,
    durationSecond: Long
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        InfoModalDialog(
            mainMessage = mainMessage,
            subMessage = subMessage,
            onDismissRequest = { openDialog = false }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.info_circle),
            contentDescription = "information modal open",
            tint = Color(0x9B, 0x97, 0x97),
            modifier = Modifier
                .clickableWithoutRipple { openDialog = true }
        )
        Spacer(modifier = Modifier.width(3.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            CustomText(
                text = "이용 시간",
                color = contentColor,
                fontSize = 16.sp,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                CustomText(
                    text = "${durationSecond / 3600}",
                    color = contentColor,
                    fontSize = 20.sp
                )
                CustomText(text = "시간 ", color = contentColor, fontSize = 16.sp)
                CustomText(
                    text = "${(durationSecond % 3600) / 60}",
                    color = contentColor,
                    fontSize = 20.sp
                )
                CustomText(text = "분 ", color = contentColor, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun BehindContentOfDayTimeCard(
    modifier: Modifier = Modifier,
    durationSecond: Long,
    targetTime: Int,
    saveTargetTimeListener: (Int) -> Unit
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        TargetTimeModalDialog(
            currentTargetTime = targetTime,
            onDismissRequest = { openDialog = false },
            onConfirmation = saveTargetTimeListener
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(start = 19.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                CustomText(
                    text = "목표 시간",
                    color = Color(0xFF333333),
                    fontSize = 16.sp,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickableWithoutRipple { openDialog = true }
                ) {
                    CustomText(text = "$targetTime", color = Color(0xFF333333), fontSize = 20.sp)
                    CustomText(text = "시간 ", color = Color(0xFF333333), fontSize = 16.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        GradientCircularProgress(
            percentage = (durationSecond / (targetTime * 3600f)),
            radius = 60.dp,
            stroke = 8.dp,
        )
    }
}

@Composable
fun ContentOfMonthTimeCard(
    modifier: Modifier = Modifier,
    contentColor: Color,
    accumulationHours: String,
    accumulationMinutes: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        CustomText(text = "월 누적 시간", color = contentColor, fontSize = 16.sp)
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CustomText(text = accumulationHours, color = contentColor, fontSize = 20.sp)
            CustomText(text = "시간 ", color = contentColor, fontSize = 16.sp)
            CustomText(text = accumulationMinutes, color = contentColor, fontSize = 20.sp)
            CustomText(text = "분", color = contentColor, fontSize = 16.sp)
        }
    }
}

@Composable
fun BehindContentOfMonthTimeCard(
    modifier: Modifier = Modifier,
    acceptedHours: String,
    acceptedMinutes: String,
    mainMessage: String,
    subMessage: String,
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        InfoModalDialog(
            mainMessage = mainMessage,
            subMessage = subMessage,
            onDismissRequest = { openDialog = false }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(id = R.drawable.info_circle),
            contentDescription = "information modal open",
            tint = Color(0x9B, 0x97, 0x97),
            modifier = Modifier
                .clickableWithoutRipple { openDialog = true }
                .padding(top = 3.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            CustomText(text = "인정 시간", color = Color(0xFF735BF2), fontSize = 16.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                CustomText(text = acceptedHours, color = Color(0xFF735BF2), fontSize = 20.sp)
                CustomText(text = "시간 ", color = Color(0xFF735BF2), fontSize = 16.sp)
                CustomText(text = acceptedMinutes, color = Color(0xFF735BF2), fontSize = 20.sp)
                CustomText(text = "분", color = Color(0xFF735BF2), fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun TimeCardView(
    todayAccumulationTime: Long, /* 서버에 저장된 todayAccumulationTime(ms) */
    dayTargetTime: Int, /* 목표 시간(최소 12시간) */
    inTimeStamp: Long?, /* if (inOutState가 IN) 마지막 입장 시간(ms) else null */
    monthAccumulationTime: Pair<String, String>, /* 월 누적 시간 hour to minute */
    monthAcceptedTime: Pair<String, String>, /* v3에서 추가된 인정 시간 hour to minute */
    tagLatencyNotice: Pair<String, String>, /* v3에서 추가된 tagLatencyNotice 서버에서 받아온 title to content */
    fundInfoNotice: Pair<String, String>, /* v3에서 추가된 fundInfoNotice 서버에서 받아온 title to content */
    saveTargetTimeListener: (Int) -> Unit, /* targetTime 저장 함수 */
) {
    var durationSecond by remember {
        val ms = if (inTimeStamp != null) {
            System.currentTimeMillis() - inTimeStamp + todayAccumulationTime
        } else {
            todayAccumulationTime
        }
        mutableLongStateOf(ms / 1000)
    }

    LaunchedEffect(Unit) {
        while (inTimeStamp != null) {
            durationSecond =
                (System.currentTimeMillis() - inTimeStamp + todayAccumulationTime) / 1000
            delay((60 - durationSecond % 60) * 1000)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Transparent)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        ExpandedAnimationCard(
            expandedHeight = 270.dp,
            background = Color.White,
            content = { modifier, color ->
                ContentOfDayTimeCard(
                    modifier,
                    color,
                    tagLatencyNotice.first,
                    tagLatencyNotice.second,
                    durationSecond,
                )
            },
            contentBehind = { modifier ->
                BehindContentOfDayTimeCard(
                    modifier,
                    durationSecond,
                    dayTargetTime,
                    saveTargetTimeListener,
                )
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        ExpandedAnimationCard(
            expandedHeight = 130.dp,
            background = Color(0xFF735BF2),
            content = { modifier, color ->
                ContentOfMonthTimeCard(
                    modifier,
                    color,
                    monthAccumulationTime.first,
                    monthAccumulationTime.second
                )
            },
            contentBehind = { modifier ->
                BehindContentOfMonthTimeCard(
                    modifier,
                    monthAcceptedTime.first,
                    monthAcceptedTime.second,
                    fundInfoNotice.first,
                    fundInfoNotice.second
                )
            },
        )
    }
}

@Preview
@Composable
fun ExpandedAnimationDayCardPreview() {
    ExpandedAnimationCard(
        260.dp,
        background = Color.White,
        { p1, p2 ->
            ContentOfDayTimeCard(
                p1,
                p2,
                "",
                "",
                0L,
            )
        },
        { modifier ->
            BehindContentOfDayTimeCard(
                modifier,
                0,
                12,
                {}
            )
        }
    )
}

@Preview
@Composable
fun ExpandedAnimationMonthCardPreview() {
    ExpandedAnimationCard(
        130.dp,
        background = Color(0xFF735BF2),
        { p1, p2 -> ContentOfMonthTimeCard(p1, p2, "0", "5") },
        { BehindContentOfMonthTimeCard(it, "0", "5", "", "") }
    )
}

@Preview(backgroundColor = 0xFFF5F5F5, showBackground = true)
@Composable
fun TimeCardPreview() {
    val m1 = "입실 중 이용 시간은 \n실제 기록과 다를 수 있습니다."
    val s1 = "입실 / 퇴실 태깅에 유의해주세요."
    val m2 = "인정 시간은 지원금 산정 시\n반영 되는 시간입니다."
    val s2 = "1일 최대 12시간"

    TimeCardView(
        todayAccumulationTime = ((3600L * 1) + (60L * 0)) * 1000,
        dayTargetTime = 12,
        inTimeStamp = 1703577908187L,
        monthAccumulationTime = "0" to "5",
        monthAcceptedTime = "0" to "5",
        tagLatencyNotice = m1 to s1,
        fundInfoNotice = m2 to s2,
        {}
    )
}
