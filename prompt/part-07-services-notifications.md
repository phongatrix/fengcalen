# PART 07 — Services & Notifications (NotificationService, AlarmReceiver, LocaleReceiver, BootReceiver)

## Mục tiêu

Tạo:
- `NotificationService` — gửi thông báo lịch sự kiện, tết, rằm
- `AlarmReceiver` — nhận lệnh alarm để thông báo sự kiện
- `LocaleReceiver` — xử lý thay đổi ngôn ngữ
- `BootReceiver` — khởi động lạiNotification sau reboot

## Thực hiện

```bash
mkdir -p app/src/main/java/fengcalen/service
mkdir -p app/src/main/java/fengcalen/receiver
```

## Tạo app/src/main/java/fengcalen/service/NotificationService.kt

```kotlin
package fengcalen.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import fengcalen.ui.MainActivity
import fengcalen.R
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : Service() {
    companion object {
        const val CHANNEL_ID = "feng_calendar_channel"
        const val NOTIFICATION_ID = 1001
    }

    @Inject lateinit var contentService: ContentService

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val notificationManager: NotificationManager? by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Feng Calendar Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Thông báo lịch âm, phong thủy, sự kiện"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 100, 200, 300)
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): android.app.Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Lịch Âm - Feng Calen")
            .setContentText("Chờ sự kiện mới")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }

    fun scheduleNotification(dateString: String, title: String, message: String) {
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("date", dateString)
            putExtra("title", title)
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, dateString.hashCode(),
            intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val calendar = Calendar.getInstance()
        val parts = dateString.split("/")
        calendar.set(Calendar.YEAR, parts[2].toInt())
        calendar.set(Calendar.MONTH, parts[1].toInt() - 1)
        calendar.set(Calendar.DAY_OF_MONTH, parts[0].toInt())
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val triggerTime = calendar.timeInMillis
        if (triggerTime < System.currentTimeMillis()) {
            alarmManager.setExactAndAllowWhileIdle(
                android.app.AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
```

## Tạo app/src/main/java/fengcalen/receiver/AlarmReceiver.kt

```kotlin
package fengcalen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import fengcalen.service.NotificationService
import fengcalen.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmReceiver @Inject constructor() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val date = intent.getStringExtra("date")
        val title = intent.getStringExtra("title")
        val message = intent.getStringExtra("message")
        Log.d("AlarmReceiver", "Received alarm: $date - $title")

        // Show notification directly
        val notificationIntent = Intent(context, NotificationService::class.java)
        context.startService(notificationIntent)

        // Launch app
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(launchIntent)
    }
}
```

## Tạo app/src/main/java/fengcalen/receiver/LocaleReceiver.kt

```kotlin
package fengcalen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LocaleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == android.intent.action.LOCALE_CHANGED) {
            // Reinitialize locale
            val appContext = context.applicationContext
            val prefs = android.content.SharedPreferences
                .getDefaultSharedPreferences(appContext)
            val locale = prefs.getString("locale", "vi") ?: "vi"
            Locale.setDefault(Locale(locale))
            android.util.Log.d("LocaleReceiver", "Locale changed to $locale")
        }
    }
}
```

## Tạo app/src/main/java/fengcalen/receiver/BootReceiver.kt

```kotlin
package fengcalen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == android.intent.action.BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device booted, scheduling notifications")
            // Reload alarm schedules
        }
    }
}
```

## Kiểm tra Part 07

Sau khi chạy, kiểm tra:
- [ ] Cấu trúc thư mục `service/`, `receiver/`
- [ ] `NotificationService.kt` có
- [ ] `AlarmReceiver.kt` có
- [ ] `LocaleReceiver.kt` có
- [ ] `BootReceiver.kt` có

## Lệnh kiểm tra

```bash
cd /home/ibm/rea-analyze
echo "=== Kiểm tra Part 07 ==="
find app/src/main/java/fengcalen/service/ app/src/main/java/fengcalen/receiver/ -name "*.kt" | sort
```
