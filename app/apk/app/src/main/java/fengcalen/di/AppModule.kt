package fengcalen.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fengcalen.data.dao.EventReminderDao
import fengcalen.data.dao.LunarDateDao
import fengcalen.data.database.FengCalendarDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFengCalendarDatabase(
        @ApplicationContext context: Context
    ): FengCalendarDatabase {
        return Room.databaseBuilder(
            context,
            FengCalendarDatabase::class.java,
            FengCalendarDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideLunarDateDao(
        database: FengCalendarDatabase
    ): LunarDateDao {
        return database.lunarDateDao()
    }

    @Provides
    @Singleton
    fun provideEventReminderDao(
        database: FengCalendarDatabase
    ): EventReminderDao {
        return database.eventReminderDao()
    }
}
