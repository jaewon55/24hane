package com.hane24.hoursarenotenough24.overview

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
            .animateContentSize()
            .width(330.dp)
            .height(if (expanded) expandedHeight else 80.dp)
            .padding(1.dp)
            .clickableWithoutRipple {
                expanded = !expanded
                rotationState = (rotationState + 1) % 4
            },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 20.dp, end = 14.dp, top = 28.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                content(
                    Modifier.width(270.dp),
                    if (cardBackground == Color.White) Color(0xFF333333) else Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = if (cardBackground == Color.White) Color(0xFF9B9797) else Color.White,
                    modifier = Modifier.rotate(rotationAngle),
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            contentBehind(
                Modifier
                    .fillMaxWidth()
                    .alpha(if (expanded) 1f else 0f)
            )
        }
    }
}

@Composable
fun ContentOfDayTimeCard(
    modifier: Modifier = Modifier,
    contentColor: Color,
    mainMessage: String,
    subMessage: String,
) {
    var openDialog by remember { mutableStateOf(false) }
    val inTimeStamp = 1703550806445L
    var durationHour by remember { mutableLongStateOf(0L) }
    var durationMinute by remember { mutableLongStateOf(0L) }
    var durationSecond by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            val second = (System.currentTimeMillis() - inTimeStamp) / 1000

            durationHour = second / 3600
            durationMinute = (second % 3600) / 60
            durationSecond = second % 60
            delay(1000)
        }
    }

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
            modifier = Modifier.clickableWithoutRipple { openDialog = true }
        )
        Spacer(modifier = Modifier.width(3.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .paddingFromBaseline(bottom = 8.dp)
        ) {
            CustomText(
                text = "이용 시간",
                color = contentColor,
                fontSize = 16.sp,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                CustomText(text = "$durationHour", color = contentColor, fontSize = 20.sp)
                CustomText(text = "시간 ", color = contentColor, fontSize = 16.sp)
                CustomText(text = "$durationMinute", color = contentColor, fontSize = 20.sp)
                CustomText(text = "분 ", color = contentColor, fontSize = 16.sp)
                CustomText(text = "$durationSecond", color = contentColor, fontSize = 20.sp)
                CustomText(text = "초", color = contentColor, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun BehindContentOfDayTimeCard(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 19.dp, end = 26.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .paddingFromBaseline(bottom = 8.dp)
            ) {
                CustomText(
                    text = "목표 시간",
                    color = Color(0xFF333333),
                    fontSize = 16.sp,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CustomText(text = "12", color = Color(0xFF333333), fontSize = 20.sp)
                    CustomText(text = "시간 ", color = Color(0xFF333333), fontSize = 16.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        GradientCircularProgress(percentage = 0.5f, radius = 60.dp, stroke = 8.dp)
    }
}

@Composable
fun ContentOfMonthTimeCard(modifier: Modifier = Modifier, contentColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.paddingFromBaseline(bottom = 8.dp)
    ) {
        CustomText(text = "월 누적 시간", color = contentColor, fontSize = 16.sp)
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CustomText(text = "0", color = contentColor, fontSize = 20.sp)
            CustomText(text = "시간 ", color = contentColor, fontSize = 16.sp)
            CustomText(text = "5", color = contentColor, fontSize = 20.sp)
            CustomText(text = "분", color = contentColor, fontSize = 16.sp)
        }
    }
}

@Composable
fun BehindContentOfMonthTimeCard(
    modifier: Modifier = Modifier,
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
        modifier = modifier.padding(end = 26.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.info_circle),
            contentDescription = "information modal open",
            tint = Color(0x9B, 0x97, 0x97),
            modifier = Modifier.clickableWithoutRipple { openDialog = true }
        )
        Spacer(modifier = Modifier.width(3.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .paddingFromBaseline(bottom = 8.dp)
        ) {
            CustomText(text = "인정 시간", color = Color(0xFF735BF2), fontSize = 16.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                CustomText(text = "0", color = Color(0xFF735BF2), fontSize = 20.sp)
                CustomText(text = "시간 ", color = Color(0xFF735BF2), fontSize = 16.sp)
                CustomText(text = "5", color = Color(0xFF735BF2), fontSize = 20.sp)
                CustomText(text = "분", color = Color(0xFF735BF2), fontSize = 16.sp)
            }
        }
    }
}

@Preview
@Composable
fun ExpandedAnimationDayCardPreview() {
    ExpandedAnimationCard(
        260.dp,
        background = Color.White,
        { p1, p2 -> ContentOfDayTimeCard(p1, p2, "", "") },
        { BehindContentOfDayTimeCard(it) }
    )
}

@Preview
@Composable
fun ExpandedAnimationMonthCardPreview() {
    ExpandedAnimationCard(
        130.dp,
        background = Color(0xFF735BF2),
        { p1, p2 -> ContentOfMonthTimeCard(p1, p2) },
        { BehindContentOfMonthTimeCard(it, "", "") }
    )
}

@Preview()
@Composable
fun TimeCardPreview() {
    val m1 = "입실 중 이용 시간은 \n실제 기록과 다를 수 있습니다."
    val s1 = "입실 / 퇴실 태깅에 유의해주세요."
    val m2 = "인정 시간은 지원금 산정 시\n반영 되는 시간입니다."
    val s2 = "1일 최대 12시간"
    Log.d("tt", "${System.currentTimeMillis()}")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFF5F5F5))
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        ExpandedAnimationCard(
            260.dp,
            background = Color.White,
            { p1, p2 -> ContentOfDayTimeCard(p1, p2, m1, s1) },
            { BehindContentOfDayTimeCard(it) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        ExpandedAnimationCard(
            130.dp,
            background = Color(0xFF735BF2),
            { p1, p2 -> ContentOfMonthTimeCard(p1, p2) },
            { BehindContentOfMonthTimeCard(it, m2, s2) }
        )
    }
}
