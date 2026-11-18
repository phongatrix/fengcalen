package fengcalen.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lunar_dates")
data class LunarDateEntity(
    @PrimaryKey val solarDate: String,
    val lunarDate: String,
    val day: String,
    val month: String,
    val year: String,
    val canChiYear: String,
    val canChiMonth: String,
    val canChiDay: String,
    val zodiac: String,
    val element: String,
    val moonPhase: String,
    val fengShui: String,
    val auspiciousHours: String,
    val holidays: String,
    val notes: String
)

@Entity(tableName = "moon_phases")
data class MoonPhaseEntity(
    @PrimaryKey val date: String,
    val phase: String,
    val phaseNameVi: String,
    val illumination: Float,
    val ageDays: Float,
    val constellation: String,
    val energy: String,
    val color: String,
    val keywords: String,
    val recommended: Boolean
)

@Entity(tableName = "feng_shui")
data class FengShuiEntity(
    @PrimaryKey val date: String,
    val element: String,
    val branch: String,
    val yinYang: String,
    val energy: String,
    val direction: String,
    val luckyColor: String,
    val luckyNumber: String,
    val livestock: String,
    val gemstone: String,
    val luckyAspects: String,
    val taboos: String,
    val fortuneSummary: String
)

@Entity(tableName = "content_items")
data class ContentEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val content: String,
    val theme: String,
    val tags: String,
    val source: String
)
