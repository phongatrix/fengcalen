package fengcalen.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fengcalen.data.dao.EventReminderDao
import fengcalen.data.dao.LunarDateDao
import fengcalen.data.entity.ContentEntity
import fengcalen.data.entity.EventReminderEntity
import fengcalen.data.entity.FengShuiEntity
import fengcalen.data.entity.LunarDateEntity
import fengcalen.data.entity.MoonPhaseEntity

@Database(
    entities = [
        LunarDateEntity::class,
        MoonPhaseEntity::class,
        FengShuiEntity::class,
        ContentEntity::class,
        EventReminderEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FengCalendarDatabase : RoomDatabase() {
    abstract fun lunarDateDao(): LunarDateDao
    abstract fun eventReminderDao(): EventReminderDao

    companion object {
        const val DATABASE_NAME = "feng_calendar.db"
    }
}
