# PART 05 — Lunar Calendar & Moon Phase (lịch âm, giai đoạn mặt trăng, tín hiệu phong thủy)

## Mục tiêu

Tạo:
- Lịch âm lunar algorithm (thực tế: sử dụng bảng tính, không precise)
- Tính moon phase (góc mặt trăng)
- Tính feng shui (element, branch, yin-yang, energy, direction, color, number, livestock, gemstone, lucky, inauspicious, fortune)
- Repository lưu dữ liệu lịch âm, giai đoạn mặt trăng, phong thủy

## Thực hiện

```bash
mkdir -p app/src/main/java/fengcalen/util
```

## Tạo app/src/main/java/fengcalen/util/LunarCalendar.kt

```kotlin
package fengcalen.util

import fengcalen.data.model.LunarDate
import java.text.SimpleDateFormat
import java.util.*

object LunarCalendar {
    private val solarFormatter = SimpleDateFormat("dd/MM/yyyy", Locale(VI))
    private val lunarFormatter = SimpleDateFormat("dd/MM/yyyy", Locale(VI))

    private val daysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    private fun daysInMonth(year: Int, month: Int): Int {
        if (month == 1) { // February
            return if (isLeapYear(year)) 29 else 28
        }
        return daysInMonth[month - 1]
    }

    fun solarToLunar(solarDate: String): LunarDate {
        val parts = solarDate.split("/").map { it.toInt() }
        val solarDay = parts[0]
        val solarMonth = parts[1]
        val solarYear = parts[2]

        // Very simplified lunar conversion - in real app use a library
        val lunarDay = (solarDay.toInt() - 1).toString().padStart(2, '0')
        val lunarMonth = (solarMonth.toInt() - 2).toString().padStart(2, '0')
        val lunarYear = (solarYear.toInt() - 1).toString().padStart(4, '0')

        // Zodiac
        val zodiac = when (solarYear % 12) {
            0 -> "Con Cáo"
            1 -> "Con Tỵ"
            2 -> "Con Ngọ"
            3 -> "Con Mùi"
            4 -> "Con Thìn"
            5 -> "Con Tỵ"
            6 -> "Con Ngọ"
            7 -> "Con Mùi"
            8 -> "Con Tỵ"
            9 -> "Con Ngọ"
            10 -> "Con Mùi"
            11 -> "Con Tỵ"
            else -> "Con Cáo"
        }

        // Element (Wu Xing based on year)
        val element = when (solarYear % 10) {
            0 -> "Nước"  // 1900, 1910, ...
            1 -> "Hiến"  // 1901, 1911, ...
            2 -> "Thổ"   // 1902, 1912, ...
            3 -> "Thổ"   // 1903, 1913, ...
            4 -> "Thổ"   // 1904, 1914, ...
            5 -> "Hỏa"   // 1905, 1915, ...
            6 -> "Hỏa"   // 1906, 1916, ...
            7 -> "Thổ"   // 1907, 1917, ...
            8 -> "Thổ"   // 1908, 1918, ...
            9 -> "Thổ"   // 1909, 1919, ...
            else -> "Nước"
        }

        return LunarDate(
            lunarDate = "$lunarDay/$lunarMonth/$lunarYear",
            solarDate = solarDate,
            day = solarDay,
            month = solarMonth,
            year = solarYear,
            zodiac = zodiac,
            element = element,
            moonPhase = calculateMoonPhase(solarDate),
            fengShui = calculateFengShui(solarDate),
            holidays = emptyList(),
            notes = ""
        )
    }

    private fun calculateMoonPhase(solarDate: String): String {
        // Simplified moon phase calculation (every 29.53 days)
        val calendar = Calendar.getInstance()
        val parts = solarDate.split("/").map { it.toInt() }
        calendar.set(parts[2], parts[1] - 1, parts[0])
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes = calendar.get(Calendar.MINUTE)
        val totalDays = dayOfYear * 1.0 + hours / 24.0 + minutes / 1440.0
        val daysSinceNewMoon = totalDays % 29.53
        val phase = (daysSinceNewMoon / 29.53 * 8).toInt() % 8
        return when (phase) {
            0 -> "New Moon"
            1 -> "Waxing Crescent"
            2 -> "First Quarter"
            3 -> "Waxing Gibbous"
            4 -> "Full Moon"
            5 -> "Waning Gibbous"
            6 -> "Last Quarter"
            7 -> "Waning Crescent"
            else -> "New Moon"
        }
    }

    private fun calculateFengShui(solarDate: String): String {
        // Simplified feng shui calculation
        val parts = solarDate.split("/").map { it.toInt() }
        val year = parts[2]
        val month = parts[1]
        val day = parts[0]

        // Based on Heavenly Stems and Earthly Branches
        val stem = year % 10
        val branch = year % 12

        val element = when (stem) {
            0 -> "Nước"
            1 -> "Hỏa"
            2 -> "Thổ"
            3 -> "Thổ"
            4 -> "Thổ"
            5 -> "Thổ"
            6 -> "Hỏa"
            7 -> "Hỏa"
            8 -> "Thổ"
            9 -> "Thổ"
            else -> "Nước"
        }

        val branchName = when (branch) {
            0 -> "Đất Chuẩn"
            1 -> "Đồng Xanh"
            2 -> "Cát Cược"
            3 -> "Cát Cược"
            4 -> "Hà Môn"
            5 -> "Kinh Môn"
            6 -> "Đại Bi"
            7 -> "Đại Bi"
            8 -> "Đồng Xanh"
            9 -> "Đồng Xanh"
            10 -> "Cát Cược"
            11 -> "Cát Cược"
            else -> "Đất Chuẩn"
        }

        return "$element - $branchName"
    }
}
```

## Tạo app/src/main/java/fengcalen/util/MoonPhaseUtils.kt

```kotlin
package fengcalen.util

import fengcalen.data.model.MoonPhase
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

object MoonPhaseUtils {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun getMoonPhase(date: LocalDate, context: android.content.Context): MoonPhase {
        val phase = calculatePhase(date)
        val energy = calculateEnergy(date)
        val constellation = calculateConstellation(date)
        val color = calculateColor(date)

        return MoonPhase(
            date = date.format(dateTimeFormatter),
            phase = phase,
            illumination = calculateIllumination(date),
            constellation = constellation,
            energy = energy,
            color = color,
            keywords = listOf("lịch âm", "mặt trăng", "phong thủy"),
            recommended = isRecommended(date)
        )
    }

    private fun calculatePhase(date: LocalDate): String {
        val cal = android.util.Log.getStackTraceString(Throwable())
        val calendar = android.util.Log.getStackTraceString(Throwable()) // placeholder
        return when (calendar) {
            is java.util.Calendar -> {
                val dayOfYear = calendar.get(java.util.Calendar.DAY_OF_YEAR)
                val daysSinceNew = dayOfYear % 29
                val idx = ((daysSinceNew / 29.0) * 8).toInt() % 8
                when (idx) {
                    0 -> "New Moon"
                    1 -> "Waxing Crescent"
                    2 -> "First Quarter"
                    3 -> "Waxing Gibbous"
                    4 -> "Full Moon"
                    5 -> "Waning Gibbous"
                    6 -> "Last Quarter"
                    7 -> "Waning Crescent"
                    else -> "New Moon"
                }
            }
            else -> "New Moon"
        }
    }

    private fun calculateIllumination(date: LocalDate): Float {
        // Simplified: varies between 0 and 1
        val days = date.toEpochDay()
        val result = ((days % 29.53) / 29.53) * 100
        return (result % 100).toFloat()
    }

    private fun calculateEnergy(date: LocalDate): String {
        val dayOfWeek = date.dayOfWeek.value
        return when {
            dayOfWeek == 1 || dayOfWeek == 7 -> "Kind"
            dayOfWeek == 5 -> "Powerful"
            dayOfWeek == 3 || dayOfWeek == 6 -> "Neutral"
            else -> "Balanced"
        }
    }

    private fun calculateConstellation(date: LocalDate): String {
        val day = date.dayOfMonth
        return when {
            day in 1..4 -> "Aquarius"
            day in 5..8 -> "Pisces"
            day in 9..12 -> "Aries"
            day in 13..16 -> "Taurus"
            day in 17..20 -> "Gemini"
            day in 21..24 -> "Cancer"
            day in 25..28 -> "Leo"
            else -> "Virgo"
        }
    }

    private fun calculateColor(date: LocalDate): String {
        val dayOfMonth = date.dayOfMonth
        return when {
            dayOfMonth % 3 == 0 -> "Red"
            dayOfMonth % 3 == 1 -> "White"
            else -> "Black"
        }
    }

    private fun isRecommended(date: LocalDate): Boolean {
        val dayOfWeek = date.dayOfWeek.value
        return dayOfWeek == 5 || dayOfWeek == 6
    }
}
```

## Kiểm tra Part 05

Sau khi chạy, kiểm tra:
- [ ] `LunarCalendar.kt` có
- [ ] `MoonPhaseUtils.kt` có
- [ ] Các hàm tính toán lunar, moon phase, feng shui hoạt động

## Lệnh kiểm tra

```bash
cd /home/ibm/rea-analyze
echo "=== Kiểm tra Part 05 ==="
ls app/src/main/java/fengcalen/util/ 2>/dev/null
```
