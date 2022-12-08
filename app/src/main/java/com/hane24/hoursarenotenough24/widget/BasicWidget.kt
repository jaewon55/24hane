package com.hane24.hoursarenotenough24.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.data.AccumulationTimeInfo
import com.hane24.hoursarenotenough24.login.SplashActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var accumulationData: AccumulationTimeInfo? = null
private var inOutStateData: String? = null

class BasicWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        context?.let {
            val views = createRemoteViews(context)
            val widgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, BasicWidget::class.java)

            when (intent?.action) {
                "REFRESH" -> {
                    Log.i("widget", "REFRESH BroadCast Recv")
                    updateRefreshAnimationOn(views, R.id.widget_refresh_progress, R.id.widget_refresh_button)
                    for (appWidgetId in widgetManager.getAppWidgetIds(componentName)) {
                        widgetManager.updateAppWidget(appWidgetId, views)
                    }
                    CoroutineScope(Dispatchers.Default).launch {
                        for (appWidgetId in widgetManager.getAppWidgetIds(componentName)) {
                            updateAppWidget(context, widgetManager, appWidgetId)
                        }
                    }

                }
                else -> Log.i("widget", "${intent?.action} BroadCast Recv")
            }
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

private fun createRemoteViews(context: Context)
    = if (inOutStateData == null || inOutStateData == "IN") RemoteViews(context.packageName, R.layout.basic_widget)
    else RemoteViews(context.packageName, R.layout.basic_widget_out)

private suspend fun getData() {
    accumulationData = getAccumulationInfo()
    inOutStateData = getInOutState()
}

private fun setSuccessCondition(context: Context, views: RemoteViews) {
    Log.i("widget", "setSuccessCondition Called")
    val refreshIntent = Intent(context, BasicWidget::class.java).also {
        it.action = "REFRESH"
    }
    val openIntent = Intent(context, SplashActivity::class.java)
    val refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_IMMUTABLE)
    val openPendingIntent = PendingIntent.getActivity(context, 1, openIntent, PendingIntent.FLAG_IMMUTABLE)

    views.setTextViewText(R.id.widget_accumulation_month_text, parseTimeMonth(accumulationData!!.monthAccumulationTime))
    views.setTextViewText(R.id.widget_accumulation_today_text, parseTimeToday(accumulationData!!.todayAccumulationTime))
    views.setProgressBar(R.id.widget_progressbar, 100, getProgressPercent(accumulationData!!.monthAccumulationTime), false)
    views.setTextViewText(R.id.widget_progress_text, "${getProgressPercent(accumulationData!!.monthAccumulationTime)}%")
    views.setOnClickPendingIntent(R.id.widget_layout, openPendingIntent)
    views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent)
    views.setOnClickPendingIntent(R.id.widget_today_text, refreshPendingIntent)
    views.setOnClickPendingIntent(R.id.widget_accumulation_today_text, refreshPendingIntent)
    updateRefreshAnimationOff(views, R.id.widget_refresh_progress, R.id.widget_refresh_button)
}

private suspend fun setErrorCondition(context: Context, views: RemoteViews) {
    Log.i("widget", "setErrorCondition Called")
    val widgetManager = AppWidgetManager.getInstance(context)
    val componentName = ComponentName(context, BasicWidget::class.java)
    val refreshIntent = Intent(context, BasicWidget::class.java).also {
        it.action = "REFRESH"
    }
    val openIntent = Intent(context, SplashActivity::class.java)
    val refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_IMMUTABLE)
    val openPendingIntent = PendingIntent.getActivity(context, 1, openIntent, PendingIntent.FLAG_IMMUTABLE)


    views.setViewVisibility(R.id.widget_error_layout, View.VISIBLE)
    views.setViewVisibility(R.id.widget_success_layout, View.GONE)

    for (widgetId in widgetManager.getAppWidgetIds(componentName)) {
        widgetManager.updateAppWidget(widgetId, views)
    }
    CoroutineScope(Dispatchers.IO).launch {
        delay(3000)
        views.setViewVisibility(R.id.widget_error_layout, View.GONE)
        views.setViewVisibility(R.id.widget_success_layout, View.VISIBLE)
        views.setOnClickPendingIntent(R.id.widget_layout, openPendingIntent)
        views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent)
        views.setOnClickPendingIntent(R.id.widget_today_text, refreshPendingIntent)
        views.setOnClickPendingIntent(R.id.widget_accumulation_today_text, refreshPendingIntent)
        updateRefreshAnimationOff(views, R.id.widget_refresh_progress, R.id.widget_refresh_button)
        for (widgetId in widgetManager.getAppWidgetIds(componentName)) {
            widgetManager.updateAppWidget(widgetId, views)
        }
    }.join()
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = createRemoteViews(context)

    CoroutineScope(Dispatchers.Default).launch {
        getData()
        if (accumulationData == null)
            setErrorCondition(context, views)
        else
            setSuccessCondition(context, views)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}