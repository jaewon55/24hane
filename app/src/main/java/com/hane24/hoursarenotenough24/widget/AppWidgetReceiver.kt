package com.hane24.hoursarenotenough24.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.AndroidResourceImageProvider
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.hane24.hoursarenotenough24.R

class AppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()
}

class MyAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.
        provideContent {
            // create your AppWidget here
            WidgetUI()
        }
    }
}

@Composable
@Preview
fun WidgetUI() {
    Column(
        modifier = GlanceModifier.fillMaxSize()
            .background(ImageProvider(R.drawable.app_widget_background)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WidgetTitle()
        Spacer(modifier = GlanceModifier.height(13.dp))
        WidgetContent(title = "월 누적 시간", time = "112시간 42분", false)
        Spacer(modifier = GlanceModifier.height(16.dp))
        WidgetContent(title = "인정 시간", time = "112시간 42분", true)
    }
}

@Composable
fun WidgetTitle() {
    val titleStyle = TextStyle(
        color = ColorProvider(Color(0xff735bf2)),
        fontSize = TextUnit(12.0f, TextUnitType.Sp)
    )

    val timeStyle = TextStyle(
        color = ColorProvider(Color(0x80000000)),
        fontSize = TextUnit(10.0f, TextUnitType.Sp)
    )

    Row {
        Column {
            Text(text = "24HANE", style = titleStyle)
            Text(text = "12.24 11:01 기준", style = timeStyle)
        }
        Image(
            modifier = GlanceModifier.clickable {}.size(16.dp),
            provider = AndroidResourceImageProvider(R.drawable.ic_widget_refresh),
            contentDescription = "widget_refresh"
        )
    }
}

@Composable
fun WidgetContent(title: String, time: String, isChecked: Boolean) {
    val titleStyle = TextStyle(
        fontSize = TextUnit(15.0f, TextUnitType.Sp),
        fontWeight = FontWeight.Bold
    )
    val timeStyle = TextStyle(
        fontSize = TextUnit(12.0f, TextUnitType.Sp),
        fontWeight = FontWeight.Medium,
        color = ColorProvider(Color(0x80000000))
    )
    val dividerResId = if (isChecked) R.drawable.widget_divider_purple else R.drawable.widget_divider

    Row(verticalAlignment = Alignment.CenterVertically) {
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
            Text(text = time, style = timeStyle)
        }
    }
}