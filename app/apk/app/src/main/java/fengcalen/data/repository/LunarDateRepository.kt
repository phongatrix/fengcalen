package fengcalen.data.repository

import android.util.Log
import fengcalen.data.dao.LunarDateDao
import fengcalen.data.entity.FengShuiEntity
import fengcalen.data.entity.LunarDateEntity
import fengcalen.data.entity.MoonPhaseEntity
import fengcalen.data.model.FengShui
import fengcalen.data.model.LunarDate
import fengcalen.data.model.MoonPhase
import fengcalen.util.LunarCalendar
import fengcalen.util.MoonPhaseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LunarDateRepository @Inject constructor(
    private val dao: LunarDateDao
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun getAllLunarDatesFlow(): Flow<List<LunarDate>> {
        return try {
            dao.getAllLunarDates().map { entities ->
                entities.map { toModel(it) }
            }
        } catch (t: Throwable) {
            Log.w("LunarDateRepository", "Failed to stream all lunar dates: ${t.message}")
            flow { emit(emptyList()) }
        }
    }

    suspend fun getLunarDate(solarDate: String): LunarDate = withContext(Dispatchers.IO) {
        try {
            val cached = dao.getLunarDateBySolar(solarDate)
            if (cached != null) {
                return@withContext toModel(cached)
            }
        } catch (t: Throwable) {
            Log.w("LunarDateRepository", "DAO read error: ${t.message}")
        }

        val calculated = LunarCalendar.solarToLunar(solarDate)
        try {
            dao.insertLunarDate(toEntity(calculated))
        } catch (t: Throwable) {
            Log.w("LunarDateRepository", "DAO write error: ${t.message}")
        }
        calculated
    }

    suspend fun getMoonPhase(date: String): MoonPhase = withContext(Dispatchers.IO) {
        try {
            val cached = dao.getMoonPhase(date)
            if (cached != null) {
                return@withContext toModel(cached)
            }
        } catch (t: Throwable) {
            Log.w("LunarDateRepository", "MoonPhase DAO read error: ${t.message}")
        }

        val localDate = try {
            LocalDate.parse(date, dateFormatter)
        } catch (e: Exception) {
            LocalDate.now()
        }
        val calculated = MoonPhaseUtils.getMoonPhase(localDate)
        try {
            dao.insertMoonPhase(toEntity(calculated))
        } catch (t: Throwable) {
            Log.w("LunarDateRepository", "MoonPhase DAO write error: ${t.message}")
        }
        calculated
    }

    suspend fun getFengShui(date: String): FengShui = withContext(Dispatchers.IO) {
        try {
            val cached = dao.getFengShui(date)
            if (cached != null) {
                return@withContext toModel(cached)
            }
        } catch (t: Throwable) {
            Log.w("LunarDateRepository", "FengShui DAO read error: ${t.message}")
        }

        val calculated = LunarCalendar.calculateFengShuiDetail(date)
        try {
            dao.insertFengShui(toEntity(calculated))
        } catch (t: Throwable) {
            Log.w("LunarDateRepository", "FengShui DAO write error: ${t.message}")
        }
        calculated
    }

    fun toModel(entity: LunarDateEntity): LunarDate = LunarDate(
        solarDate = entity.solarDate,
        lunarDate = entity.lunarDate,
        day = entity.day,
        month = entity.month,
        year = entity.year,
        canChiYear = entity.canChiYear,
        canChiMonth = entity.canChiMonth,
        canChiDay = entity.canChiDay,
        zodiac = entity.zodiac,
        element = entity.element,
        moonPhase = entity.moonPhase,
        fengShui = entity.fengShui,
        auspiciousHours = entity.auspiciousHours.split(",").map { it.trim() }.filter { it.isNotEmpty() },
        holidays = entity.holidays.split(",").map { it.trim() }.filter { it.isNotEmpty() },
        notes = entity.notes
    )

    fun toEntity(model: LunarDate): LunarDateEntity = LunarDateEntity(
        solarDate = model.solarDate,
        lunarDate = model.lunarDate,
        day = model.day,
        month = model.month,
        year = model.year,
        canChiYear = model.canChiYear,
        canChiMonth = model.canChiMonth,
        canChiDay = model.canChiDay,
        zodiac = model.zodiac,
        element = model.element,
        moonPhase = model.moonPhase,
        fengShui = model.fengShui,
        auspiciousHours = model.auspiciousHours.joinToString(","),
        holidays = model.holidays.joinToString(","),
        notes = model.notes
    )

    fun toModel(entity: MoonPhaseEntity): MoonPhase = MoonPhase(
        date = entity.date,
        phase = entity.phase,
        phaseNameVi = entity.phaseNameVi,
        illumination = entity.illumination,
        ageDays = entity.ageDays,
        constellation = entity.constellation,
        energy = entity.energy,
        color = entity.color,
        keywords = entity.keywords.split(",").map { it.trim() }.filter { it.isNotEmpty() },
        recommended = entity.recommended
    )

    fun toEntity(model: MoonPhase): MoonPhaseEntity = MoonPhaseEntity(
        date = model.date,
        phase = model.phase,
        phaseNameVi = model.phaseNameVi,
        illumination = model.illumination,
        ageDays = model.ageDays,
        constellation = model.constellation,
        energy = model.energy,
        color = model.color,
        keywords = model.keywords.joinToString(","),
        recommended = model.recommended
    )

    fun toModel(entity: FengShuiEntity): FengShui = FengShui(
        date = entity.date,
        element = entity.element,
        branch = entity.branch,
        yinYang = entity.yinYang,
        energy = entity.energy,
        direction = entity.direction,
        luckyColor = entity.luckyColor,
        luckyNumber = entity.luckyNumber,
        livestock = entity.livestock,
        gemstone = entity.gemstone,
        luckyAspects = entity.luckyAspects.split(",").map { it.trim() }.filter { it.isNotEmpty() },
        taboos = entity.taboos.split(",").map { it.trim() }.filter { it.isNotEmpty() },
        fortuneSummary = entity.fortuneSummary
    )

    fun toEntity(model: FengShui): FengShuiEntity = FengShuiEntity(
        date = model.date,
        element = model.element,
        branch = model.branch,
        yinYang = model.yinYang,
        energy = model.energy,
        direction = model.direction,
        luckyColor = model.luckyColor,
        luckyNumber = model.luckyNumber,
        livestock = model.livestock,
        gemstone = model.gemstone,
        luckyAspects = model.luckyAspects.joinToString(","),
        taboos = model.taboos.joinToString(","),
        fortuneSummary = model.fortuneSummary
    )
}
