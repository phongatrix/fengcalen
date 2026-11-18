package fengcalen.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_reminders")
data class EventReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val eventType: String = "MEMORIAL", // BIRTHDAY, MEMORIAL, ANNIVERSARY, OTHER
    val isLunar: Boolean = true,        // true = Âm lịch, false = Dương lịch
    val day: Int,                       // 1-31
    val month: Int,                     // 1-12
    val year: Int? = null,              // Năm gốc (tùy chọn)
    val repeatType: String = "YEARLY",  // YEARLY, MONTHLY, ONCE
    val remindDaysBefore: String = "0,7,10", // "0"=đúng ngày, "1"=1 ngày, "3"=3 ngày, "7"=1 tuần, "10"=10 ngày
    val remindTime: String = "08:00",   // "HH:mm"
    val ringtone: String = "nokia_tune",// nokia_tune, nokia_alarm, nokia_sms, nokia_polyphonic, default
    val note: String = "",
    val isEnabled: Boolean = true
)
