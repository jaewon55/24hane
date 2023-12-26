package com.hane24.hoursarenotenough24.widget.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.AndroidResourceImageProvider
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.widget.WidgetState
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun WidgetTitle(onClick: Action, state: WidgetState) {
    val titleStyle = TextStyle(
        color = ColorProvider(Color(0xff735bf2)),
        fontSize = TextUnit(12.0f, TextUnitType.Sp)
    )

    val timeStyle = TextStyle(
        color = ColorProvider(Color(0x80000000)),
        fontSize = TextUnit(10.0f, TextUnitType.Sp)
    )

    val currTimeFormat = SimpleDateFormat("MM.dd HH:mm", Locale("ko"))

    Row(modifier = GlanceModifier.fillMaxWidth()) {
        Column {
            Text(text = "24HANE", style = titleStyle)
            Text(text = "${currTimeFormat.format(System.currentTimeMillis())} 기준", style = timeStyle)
        }
        if (state == WidgetState.LOADING || state == WidgetState.INIT) {
            Box(modifier = GlanceModifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                CircularProgressIndicator(modifier = GlanceModifier.size(16.dp))
            }
        } else {
            Box(modifier = GlanceModifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                Image(
                    modifier = GlanceModifier.clickable(
                        onClick = onClick
                    )
                    .size(16.dp),
                    provider = AndroidResourceImageProvider(R.drawable.ic_widget_refresh),
                    contentDescription = "widget_refresh"
                )
            }
        }
    }
}
