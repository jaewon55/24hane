package com.hane24.hoursarenotenough24.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GradientCircularProgress(
    percentage: Float,
    radius: Dp,
    stroke: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier // diameter
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "${(percentage * 100).toInt()}",
                fontSize = 32.sp,
                color = Color(0xFF333333),
            )
            Text(
                text = "%",
                fontSize = 14.sp,
                color = Color(0xFF333333),
                modifier = Modifier.paddingFromBaseline(bottom = 8.dp)
            )
        }

        Canvas(
            modifier = Modifier
                .size(radius * 2)
                .rotate(90f)
        ) {
            drawCircle(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0x4DBCF8F9),
                        Color(0x4D735BF2)
                    )
                ),
                style = Stroke(
                    stroke.toPx(),
                    cap = StrokeCap.Round,
                ),
            )

            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x4D00BABC),
                        Color(0xFF735BF2),
                    )
                ),
                startAngle = 270f,
                sweepAngle = 360 * percentage,
                useCenter = false,
                style = Stroke(
                    stroke.toPx(),
                    cap = StrokeCap.Round,
                ),
            )
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 330, heightDp = 260)
private fun progressPreview() {
    GradientCircularProgress(
        percentage = 0.5f,
        radius = 60.dp,
        stroke = 8.dp
    )
}
