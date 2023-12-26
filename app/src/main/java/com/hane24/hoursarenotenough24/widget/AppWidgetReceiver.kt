package com.hane24.hoursarenotenough24.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.glance.AndroidResourceImageProvider
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import com.hane24.hoursarenotenough24.App
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.widget.component.WidgetContent
import com.hane24.hoursarenotenough24.widget.component.WidgetTitle

private val viewModel = WidgetViewModel(
    Hane24Apis.hane24ApiService,
    App.sharedPreferenceUtilss
)

class AppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()
}

class MyAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Log.i("widget", "provideGlance Called")

        provideContent {
            // create your AppWidget here
            WidgetUI(viewModel)
            LaunchedEffect(viewModel.state.collectAsState().value == WidgetState.INIT) {
                RefreshAction.refresh(viewModel, context)

            }
        }
    }
}

@Composable
fun WidgetUI(viewModel: WidgetViewModel) {
    val state by viewModel.state.collectAsState()
    val monthTime by viewModel.monthAccumulationTimeInfo.collectAsState()
    val acceptedTime by viewModel.acceptedAccumulationTime.collectAsState()

    val onClick =
        actionRunCallback<RefreshAction>()

    when (state) {
        WidgetState.INIT, WidgetState.LOADING, WidgetState.COMPLETE -> WidgetCompletePage(
            onClick = onClick,
            state = state,
            monthTime = monthTime,
            acceptedTime = acceptedTime
        )

        WidgetState.LOGIN_ERROR -> WidgetErrorPage("로그인 되어있지 않습니다.")
        WidgetState.UNKNOWN_ERROR -> WidgetErrorPage("에러가 발생했습니다.\n\n잠시 후 다시 시도해주세요.")
    }
}

@Composable
fun WidgetCompletePage(
    onClick: Action,
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
fun WidgetCompletePageV2(
    onClick: Action,
    state: WidgetState,
    monthTime: Pair<String, String>,
    acceptedTime: Pair<String, String>
) {
    Column(
        modifier = GlanceModifier.fillMaxSize()
            .background(ImageProvider(R.drawable.app_widget_background))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            WidgetContent(title = "월 누적 시간", time = monthTime, isChecked = false)
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
        Spacer(modifier = GlanceModifier.height(4.dp))
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

class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.i("widget", "onAction Called")

        refresh(viewModel, context)
    }

    companion object {
        suspend fun refresh(viewModel: WidgetViewModel, context: Context) {
            viewModel.updateLoading()
            MyAppWidget().updateAll(context)

            try {
                viewModel.refresh()
            } catch (err: Exception) {
                viewModel.updateError(err)
                MyAppWidget().updateAll(context)
            } finally {
                viewModel.refreshComplete()
                MyAppWidget().updateAll(context)
            }
        }
    }
}
