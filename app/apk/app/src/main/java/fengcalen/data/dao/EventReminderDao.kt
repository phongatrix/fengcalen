package fengcalen.data.dao

import androidx.room.*
import fengcalen.data.entity.EventReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventReminderDao {

    @Query("SELECT * FROM event_reminders ORDER BY id DESC")
    fun getAllReminders(): Flow<List<EventReminderEntity>>

    @Query("SELECT * FROM event_reminders WHERE isEnabled = 1")
    fun getActiveReminders(): Flow<List<EventReminderEntity>>

    @Query("SELECT * FROM event_reminders WHERE isEnabled = 1")
    suspend fun getActiveRemindersList(): List<EventReminderEntity>

    @Query("SELECT * FROM event_reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): EventReminderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: EventReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: EventReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: EventReminderEntity)

    @Query("DELETE FROM event_reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Long)
}
