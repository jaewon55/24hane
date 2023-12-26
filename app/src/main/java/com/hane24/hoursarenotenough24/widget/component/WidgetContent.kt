package com.hane24.hoursarenotenough24.widget.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.hane24.hoursarenotenough24.R

@Composable
fun WidgetContent(title: String, time: Pair<String, String>, isChecked: Boolean) {
    val titleStyle = TextStyle(
        fontSize = TextUnit(15.0f, TextUnitType.Sp),
        fontWeight = FontWeight.Bold
    )
    val timeStyle = TextStyle(
        fontSize = TextUnit(12.0f, TextUnitType.Sp),
        fontWeight = FontWeight.Medium,
        color = ColorProvider(Color(0x80000000))
    )
    val dividerResId =
        if (isChecked) R.drawable.widget_divider_purple else R.drawable.widget_divider

    Row(modifier = GlanceModifier ,verticalAlignment = Alignment.CenterVertically) {
        Box {
            Spacer(
                modifier = GlanceModifier
                    .background(ImageProvider(dividerResId))
                    .size(width = 2.dp, height = 32.dp)
            )
        }
        Spacer(modifier = GlanceModifier.width(6.dp))
        Column {
            Text(text = title, style = titleStyle)
            Text(text = String.format("%s시간 %s분", time.first, time.second), style = timeStyle)
        }
    }
}