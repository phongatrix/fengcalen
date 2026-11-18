package fengcalen

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FengCalendarApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("FengCalendarApp", "FATAL CRASH on thread ${thread.name}: ${throwable.message}", throwable)
        }
    }
}
