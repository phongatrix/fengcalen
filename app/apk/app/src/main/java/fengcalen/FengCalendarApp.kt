package fengcalen

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import fengcalen.service.ReminderScheduler

@HiltAndroidApp
class FengCalendarApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("FengCalendarApp", "FATAL CRASH on thread ${thread.name}: ${throwable.message}", throwable)
        }
        try {
            ReminderScheduler.scheduleDailyAlarm(this)
        } catch (e: Exception) {
            Log.e("FengCalendarApp", "Error scheduling daily reminder check: ${e.message}")
        }
    }
}
