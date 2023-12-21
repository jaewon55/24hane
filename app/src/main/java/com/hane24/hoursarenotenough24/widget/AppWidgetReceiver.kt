package com.hane24.hoursarenotenough24.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.AndroidResourceImageProvider
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
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
import com.hane24.hoursarenotenough24.App
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.network.Hane24Apis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()
}

class MyAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val viewModel = WidgetViewModel(
            Hane24Apis.hane24ApiService,
            App.sharedPreferenceUtilss.getAccessToken()
        )

        viewModel.updateWidget(context)

        provideContent {
            // create your AppWidget here
            WidgetUI(viewModel)
        }
    }
}

@Composable
fun WidgetUI(viewModel: WidgetViewModel) {


    val state by viewModel.state.collectAsState()
    val monthTime by viewModel.monthAccumulationTimeInfo.collectAsState()
    val acceptedTime by viewModel.acceptedAccumulationTime.collectAsState()
    val context = LocalContext.current

    val onClick = suspend {
        viewModel.updateWidget(context)
    }

    when (state) {
        WidgetState.LOADING, WidgetState.COMPLETE -> WidgetCompletePage(
            onClick = onClick,
            state = state,
            monthTime = monthTime,
            acceptedTime = acceptedTime
        )

        WidgetState.LOGIN_ERROR -> WidgetErrorPage("로그인이 되어있지 않습니다.")
        WidgetState.UNKNOWN_ERROR -> WidgetErrorPage("에러가 발생했습니다.\n\n잠시 후 다시 시도해주세요.")
    }

}

@Composable
fun WidgetCompletePage(
    onClick: suspend () -> Unit,
    state: WidgetState,
    monthTime: Pair<String, String>,
    acceptedTime: Pair<String, String>
) {
    Column(
        modifier = GlanceModifier.fillMaxSize()
            .background(ImageProvider(R.drawable.app_widget_background))
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WidgetTitle(onClick, state)
        Spacer(modifier = GlanceModifier.height(13.dp))
        WidgetContent(title = "월 누적 시간", time = monthTime, isChecked = false)
        Spacer(modifier = GlanceModifier.height(16.dp))
        WidgetContent(title = "인정 시간", time = acceptedTime, isChecked = true)
    }
}

@Composable
fun WidgetErrorPage(msg: String) {
    Column(
        modifier = GlanceModifier.fillMaxSize()
            .background(ImageProvider(R.drawable.app_widget_background))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(msg)
    }
}


@Composable
fun WidgetTitle(onClick: suspend () -> Unit, state: WidgetState) {
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
        if (state == WidgetState.LOADING) {
            CircularProgressIndicator(modifier = GlanceModifier.size(16.dp))
        } else {
            Image(
                modifier = GlanceModifier.clickable {
                    CoroutineScope(Dispatchers.IO).launch {
                        onClick()
                    }
                }.size(16.dp),
                provider = AndroidResourceImageProvider(R.drawable.ic_widget_refresh),
                contentDescription = "widget_refresh"
            )
        }
    }
}

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