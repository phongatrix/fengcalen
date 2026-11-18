package fengcalen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import fengcalen.data.dao.EventReminderDao
import fengcalen.service.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var eventReminderDao: EventReminderDao

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("AlarmReceiver", "Received alarm intent action: ${intent.action}")

        // Lên lịch cho ngày tiếp theo
        ReminderScheduler.scheduleDailyAlarm(context)

        // Kiểm tra và phát thông báo các sự kiện sắp tới
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ReminderScheduler.checkAndNotifyUpcomingReminders(context, eventReminderDao)
            } catch (e: Exception) {
                Log.e("AlarmReceiver", "Error checking reminders: ${e.message}", e)
            }
        }
    }
}

class LocaleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_LOCALE_CHANGED) {
            Log.i("LocaleReceiver", "System locale changed, refreshing formatters.")
        }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i("BootReceiver", "Device rebooted, restoring alarm schedules.")
            ReminderScheduler.scheduleDailyAlarm(context)
        }
    }
}
