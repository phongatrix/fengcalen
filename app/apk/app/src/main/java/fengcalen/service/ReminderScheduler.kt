package fengcalen.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import fengcalen.R
import fengcalen.data.dao.EventReminderDao
import fengcalen.data.entity.EventReminderEntity
import fengcalen.receiver.AlarmReceiver
import fengcalen.ui.MainActivity
import fengcalen.util.LunarCalendar
import fengcalen.util.NokiaSoundHelper
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

object ReminderScheduler {

    const val CHANNEL_ID_PREFIX = "fengcalen_reminder_"

    fun scheduleDailyAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "fengcalen.CHECK_REMINDERS"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            9001,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 7)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.e("ReminderScheduler", "Exact alarm permission not granted, falling back to inexact alarm: ${e.message}")
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    suspend fun checkAndNotifyUpcomingReminders(context: Context, dao: EventReminderDao) {
        val today = LocalDate.now()
        val reminders = dao.getActiveRemindersList()

        for (reminder in reminders) {
            val daysUntil = LunarCalendar.getDaysUntilNextOccurrence(reminder, today)
            val allowedDays = reminder.remindDaysBefore
                .split(",")
                .mapNotNull { it.trim().toLongOrNull() }

            if (daysUntil in allowedDays) {
                showReminderNotification(context, reminder, daysUntil)
            }
        }
    }

    fun showReminderNotification(
        context: Context,
        reminder: EventReminderEntity,
        daysUntil: Long
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "${CHANNEL_ID_PREFIX}${reminder.ringtone}"
        val soundUri = NokiaSoundHelper.getSoundUri(context, reminder.ringtone)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val toneName = NokiaSoundHelper.TONES.find { it.id == reminder.ringtone }?.name ?: "Mặc định"
            val channel = NotificationChannel(
                channelId,
                "Nhắc nhở sự kiện ($toneName)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kênh thông báo nhắc nhở ngày sinh nhật, ngày giỗ, kỷ niệm"
                enableVibration(true)
                if (soundUri != null) {
                    val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                    setSound(soundUri, audioAttributes)
                }
            }
            manager.createNotificationChannel(channel)
        }

        val typePrefix = when (reminder.eventType) {
            "BIRTHDAY" -> "🎂 Sinh nhật"
            "MEMORIAL" -> "🕯️ Ngày giỗ"
            "ANNIVERSARY" -> "💍 Kỷ niệm"
            else -> "📌 Sự kiện"
        }

        val timeNotice = when (daysUntil) {
            0L -> "Hôm nay!"
            1L -> "Ngày mai!"
            3L -> "Còn 3 ngày nữa"
            7L -> "Còn 1 tuần nữa (7 ngày)"
            10L -> "Còn 10 ngày nữa"
            else -> "Còn $daysUntil ngày nữa"
        }

        val calendarType = if (reminder.isLunar) "Âm lịch" else "Dương lịch"
        val dateText = "ngày ${reminder.day}/${reminder.month} ($calendarType)"

        val title = "[$timeNotice] $typePrefix: ${reminder.title}"
        val message = "Sắp đến $dateText. Nhắc nhở để bạn chuẩn bị chu đáo!"

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("route", "reminders")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            reminder.id.toInt(),
            openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$message\n${reminder.note}".trim()))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .apply {
                if (soundUri != null) {
                    setSound(soundUri)
                }
            }
            .build()

        manager.notify((10000 + reminder.id).toInt(), notification)

        // Đồng thời phát âm thanh Nokia nếu cần
        try {
            NokiaSoundHelper.playAlarmSound(context, reminder.ringtone)
        } catch (e: Exception) {
            Log.e("ReminderScheduler", "Could not play Nokia alarm sound: ${e.message}")
        }
    }
}
