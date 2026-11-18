package fengcalen.util

import fengcalen.data.model.MoonPhase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.roundToInt

object MoonPhaseUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private const val SYNODIC_MONTH = 29.530588853

    fun getMoonPhase(date: LocalDate): MoonPhase {
        val referenceNewMoon = LocalDate.of(2000, 1, 6)
        val days = java.time.temporal.ChronoUnit.DAYS.between(referenceNewMoon, date)
        val cyclePosition = (days % SYNODIC_MONTH + SYNODIC_MONTH) % SYNODIC_MONTH
        val illuminationPercent = ((1.0 - cos(cyclePosition * 2.0 * Math.PI / SYNODIC_MONTH)) / 2.0 * 100.0).toFloat()

        val (phaseNameEn, phaseNameVi) = when {
            cyclePosition < 1.0 || cyclePosition >= 28.5 -> Pair("New Moon", "Trăng Non (Sóc)")
            cyclePosition < 6.5 -> Pair("Waxing Crescent", "Trăng Lưỡi Liềm Đầu Tháng")
            cyclePosition < 8.5 -> Pair("First Quarter", "Thượng Huyền (Bán Nguyệt)")
            cyclePosition < 13.5 -> Pair("Waxing Gibbous", "Trăng Tròn Dần")
            cyclePosition < 16.0 -> Pair("Full Moon", "Trăng Tròn (Vọng)")
            cyclePosition < 21.0 -> Pair("Waning Gibbous", "Trăng Khuyết Dần")
            cyclePosition < 23.5 -> Pair("Last Quarter", "Hạ Huyền (Bán Nguyệt)")
            else -> Pair("Waning Crescent", "Trăng Tàn Cuối Tháng")
        }

        val constellation = getConstellation(date)
        val isRecommended = cyclePosition in 7.0..16.0

        return MoonPhase(
            date = date.format(dateFormatter),
            phase = phaseNameEn,
            phaseNameVi = phaseNameVi,
            illumination = (illuminationPercent * 10).roundToInt() / 10f,
            ageDays = (cyclePosition * 10).roundToInt() / 10f,
            constellation = constellation,
            energy = if (illuminationPercent > 70) "Dương khí thịnh vượng" else "Âm nhu thanh tịnh",
            color = if (illuminationPercent > 70) "#D4AF37" else "#93C5FD",
            keywords = listOf("Nguyệt tướng", "Triều cường", "Thiền định", "Phong thủy"),
            recommended = isRecommended
        )
    }

    private fun getConstellation(date: LocalDate): String {
        val dayOfYear = date.dayOfYear
        val signs = arrayOf(
            "Bạch Dương", "Kim Ngưu", "Song Tử", "Cự Giải",
            "Sư Tử", "Xử Nữ", "Thiên Bình", "Thiên Yết",
            "Nhân Mã", "Ma Kết", "Bảo Bình", "Song Ngư"
        )
        val index = ((dayOfYear / 30.5).toInt()) % 12
        return signs[index]
    }
}
