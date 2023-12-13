package com.hane24.hoursarenotenough24.overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.hane24.hoursarenotenough24.R


@Composable
@Preview
fun DayTimeCard(modifier: Modifier = Modifier) {
    val textStyle = TextStyle(
        color = Color(ActivityCompat.getColor(LocalContext.current, R.color.default_text)),
        fontSize = TextUnit(16.0f, TextUnitType.Sp),
        fontWeight = FontWeight.SemiBold,
    )
    val numericTextStyle = TextStyle(
        color = Color(ActivityCompat.getColor(LocalContext.current, R.color.default_text)),
        fontSize = TextUnit(24.0f, TextUnitType.Sp),
        fontWeight = FontWeight.Bold,
    )
    var expandedState by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (!expandedState) 0.0f else 90.0f,
        label = "card_btn_anim"
    )


    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { expandedState = !expandedState }
        .animateContentSize(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(top = 28.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TimeTitle(
                title = "이용 시간",
                time = "2" to "5",
                textStyle = textStyle,
                numericTextStyle = numericTextStyle,
                rotation = rotation
            )
            AnimatedVisibility(visible = expandedState) {
                TimePart(
                    textStyle = textStyle,
                    numericTextStyle = numericTextStyle,
                    rotation = rotation
                )
            }
        }
    }
}

@Composable
fun TimePart(
    textStyle: TextStyle,
    numericTextStyle: TextStyle,
    rotation: Float
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TimeTitle(
            title = "목표 시간",
            time = "160" to "",
            textStyle = textStyle,
            numericTextStyle = numericTextStyle,
            rotation = rotation
        )
        TimeProgressBar()
    }

}

@Composable
fun TimeTitle(
    title: String,
    time: Pair<String, String>,
    textStyle: TextStyle,
    numericTextStyle: TextStyle,
    rotation: Float
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, style = textStyle)
        TimeContent(
            time = time,
            textStyle = textStyle,
            numericTextStyle = numericTextStyle,
            rotation = rotation
        )
    }
}

@Composable
fun ExpandableCardButton(state: Float) {
    Icon(
        modifier = Modifier.rotate(state),
        painter = painterResource(id = R.drawable.arrow),
        contentDescription = "card_btn",
        tint = Color(0xff9b9797)
    )
}

@Composable
fun TimeContent(
    time: Pair<String, String>,
    textStyle: TextStyle,
    numericTextStyle: TextStyle,
    rotation: Float
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = time.first, style = numericTextStyle, modifier = Modifier.alignByBaseline())
        Text(text = "시간 ", style = textStyle, modifier = Modifier.alignByBaseline())

        if (time.second.isNotBlank()) {
            Text(
                text = time.second,
                style = numericTextStyle,
                modifier = Modifier.alignByBaseline()
            )
            Text(text = "분", style = textStyle, modifier = Modifier.alignByBaseline())
            Spacer(modifier = Modifier.width(10.dp))
            ExpandableCardButton(rotation)
        } else {
            Spacer(modifier = Modifier.width(18.dp))
        }
    }
}
//#00BABC0A, #735BF2
@Composable
@Preview
fun TimeProgressBar(modifier: Modifier = Modifier) {

}

@Composable
@Preview(backgroundColor = 0xffffffff)
fun CustomCircularProgressIndicator(
    progress: Float = 0.31f,
    backgroundColor: Color = Color(0xffbcf8f9).copy(alpha = 0.3f),
    gradientStartColor: Color = Color(0x00babc0a),
    gradientEndColor: Color = Color(0xff735bf2),
    height: Dp = 120.dp,
    width: Dp = 120.dp
) {
    Box(modifier = Modifier.padding(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.progressSemantics(progress).size(width + 10.dp, height + 10.dp).padding(8.dp)) {
            val startAngle = 270f
            val sweep = progress * 360f
            // Adding track with this line 👇
            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(10f, 10f),
                style = Stroke(20f),
            )
//            drawDeterminateCircularIndicator(startAngle, 360f, trackColor, stroke)
//            drawDeterminateCircularIndicator(startAngle, sweep, color, stroke)
        }
//        CircularProgressIndicator(
//            modifier = Modifier
//                .size(120.dp, 120.dp)
//                .rotate(90.0f),
//            strokeWidth = 8.dp,
//            progress = 0.31f,
//            strokeCap = StrokeCap.Round,
//            color = Brush.sweepGradient(colors = listOf(Color(0x00babc0a), Color(0xff735bf2))),
//            backgroundColor = Color(0xffbcf8f9).copy(alpha = 0.3f)
//        )
        Row {
            Text(
                text = (progress * 100).toInt().toString(),
                style = TextStyle(fontSize = TextUnit(32.0f, TextUnitType.Sp)),
                modifier = Modifier.alignByBaseline()
            )
            Text(
                text = "%",
                style = TextStyle(fontSize = TextUnit(14.0f, TextUnitType.Sp)),
                modifier = Modifier.alignByBaseline()
            )
        }
    }
}

@Composable
@Preview
fun MonthTimeCard() {

}