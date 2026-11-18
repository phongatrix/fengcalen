package fengcalen.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import fengcalen.R
import fengcalen.ui.MainActivity
import fengcalen.util.LunarCalendar
import java.time.LocalDate

class LunarWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action
        if (action == Intent.ACTION_DATE_CHANGED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED ||
            action == Intent.ACTION_BOOT_COMPLETED ||
            action == ACTION_UPDATE_WIDGET
        ) {
            updateAllWidgets(context)
        }
    }

    companion object {
        const val ACTION_UPDATE_WIDGET = "fengcalen.widget.ACTION_UPDATE_WIDGET"
        private const val TAG = "LunarWidgetProvider"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            try {
                val now = LocalDate.now()
                val dayOfWeekVi = when (now.dayOfWeek.value) {
                    1 -> "Thứ Hai"
                    2 -> "Thứ Ba"
                    3 -> "Thứ Tư"
                    4 -> "Thứ Năm"
                    5 -> "Thứ Sáu"
                    6 -> "Thứ Bảy"
                    7 -> "Chủ Nhật"
                    else -> ""
                }

                val dateStr = "%02d/%02d/%04d".format(now.dayOfMonth, now.monthValue, now.year)
                val lunarDate = LunarCalendar.solarToLunar(dateStr)

                val views = RemoteViews(context.packageName, R.layout.widget_lunar_small)

                // Header: "Thứ Ba, 17/11/2026"
                val headerText = "$dayOfWeekVi, ${now.dayOfMonth}/${now.monthValue}/${now.year}"
                views.setTextViewText(R.id.tv_widget_header, headerText)

                // Solar day: "17"
                views.setTextViewText(R.id.tv_widget_solar_day, now.dayOfMonth.toString())
                views.setTextViewText(R.id.tv_widget_solar_sub, "Dương lịch")

                // Lunar day: "9"
                views.setTextViewText(R.id.tv_widget_lunar_day, lunarDate.day)
                views.setTextViewText(R.id.tv_widget_lunar_sub, "Tháng ${lunarDate.month} ÂL")

                // Can Chi: "Ngày Ất Mùi • Năm Bính Ngọ"
                val canChiText = "${lunarDate.canChiDay} • ${lunarDate.canChiYear}"
                views.setTextViewText(R.id.tv_widget_can_chi, canChiText)

                // Click Intent to open MainActivity
                val launchIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update widget $appWidgetId: ${e.message}", e)
            }
        }

        fun updateAllWidgets(context: Context) {
            try {
                val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
                val componentName = ComponentName(context, LunarWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
                if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                    for (id in appWidgetIds) {
                        updateAppWidget(context, appWidgetManager, id)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update all widgets: ${e.message}", e)
            }
        }
    }
}
