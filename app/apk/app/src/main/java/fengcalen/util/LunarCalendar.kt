package fengcalen.util

import fengcalen.data.entity.EventReminderEntity
import fengcalen.data.model.FengShui
import fengcalen.data.model.LunarDate
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.*

object LunarCalendar {

    private val CAN = arrayOf("Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh", "Tân", "Nhâm", "Quý")
    private val CHI = arrayOf("Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi")
    private val ZODIAC = arrayOf("Chuột", "Trâu", "Hổ", "Mèo", "Rồng", "Rắn", "Ngựa", "Dê", "Khỉ", "Gà", "Chó", "Lợn")
    private val ELEMENTS = arrayOf("Kim", "Thủy", "Hỏa", "Thổ", "Mộc")

    private val TIET_KHI = arrayOf(
        "Xuân Phân", "Thanh Minh", "Cốc Vũ", "Lập Hạ", "Tiểu Mãn", "Mang Chủng",
        "Hạ Chí", "Tiểu Thử", "Đại Thử", "Lập Thu", "Xử Thử", "Bạch Lộ",
        "Thu Phân", "Hàn Lộ", "Sương Giáng", "Lập Đông", "Tiểu Tuyết", "Đại Tuyết",
        "Đông Chí", "Tiểu Hàn", "Đại Hàn", "Lập Xuân", "Vũ Thủy", "Kinh Trập"
    )

    fun jdFromDate(d: Int, m: Int, y: Int): Int {
        val a = (14 - m) / 12
        val y1 = y + 4800 - a
        val m1 = m + 12 * a - 3
        var jd = d + (153 * m1 + 2) / 5 + 365 * y1 + y1 / 4 - y1 / 100 + y1 / 400 - 32045
        if (jd < 2299161) {
            jd = d + (153 * m1 + 2) / 5 + 365 * y1 + y1 / 4 - 32083
        }
        return jd
    }

    private fun newMoon(k: Int): Double {
        val T = k / 1236.85
        val T2 = T * T
        val T3 = T2 * T
        var Jd1 = 2415020.75933 + 29.53058868 * k + 0.0001178 * T2 - 0.000000155 * T3
        val M = 359.2242 + 29.10535608 * k - 0.0000333 * T2 - 0.00000347 * T3
        val Mpr = 306.0253 + 385.81691806 * k + 0.0107306 * T2 + 0.00001236 * T3
        val F = 21.2964 + 390.67050646 * k - 0.0016528 * T2 - 0.00000239 * T3

        val rad = PI / 180.0
        val C1 = (0.1734 - 0.000393 * T) * sin(M * rad) + 0.0021 * sin(2 * M * rad)
        val C2 = -0.4068 * sin(Mpr * rad) + 0.0161 * sin(2 * Mpr * rad)
        val C3 = -0.0004 * sin(3 * Mpr * rad)
        val C4 = 0.0104 * sin(2 * F * rad) - 0.0051 * sin((M + Mpr) * rad)
        val C5 = -0.0074 * sin((M - Mpr) * rad) + 0.0004 * sin((2 * F + M) * rad)

        return Jd1 + C1 + C2 + C3 + C4 + C5
    }

    private fun sunLongitude(jdn: Double): Double {
        val T = (jdn - 2451545.0) / 36525.0
        val L0 = 280.46646 + 36000.76983 * T + 0.0003032 * T * T
        val M = 357.52911 + 35999.05029 * T - 0.0001537 * T * T
        val rad = PI / 180.0
        val C = (1.914602 - 0.004817 * T) * sin(M * rad) + (0.019993 - 0.000101 * T) * sin(2 * M * rad) + 0.000289 * sin(3 * M * rad)
        var L = L0 + C
        L %= 360.0
        if (L < 0) L += 360.0
        return L * rad
    }

    private fun getSunLongitudeMajor(dayNumber: Int, timeZone: Double): Int {
        return (sunLongitude(dayNumber - 0.5 - timeZone / 24.0) / (PI / 6)).toInt()
    }

    private fun getLunarMonth11(year: Int, timeZone: Double): Int {
        val off = jdFromDate(31, 12, year) - 2415021
        val k = (off / 29.530588853).toInt()
        var nm = newMoon(k)
        var sunLong = getSunLongitudeMajor((nm + 0.5 + timeZone / 24.0).toInt(), timeZone)
        if (sunLong >= 9) {
            nm = newMoon(k - 1)
        }
        return (nm + 0.5 + timeZone / 24.0).toInt()
    }

    fun convertSolar2Lunar(dd: Int, mm: Int, yy: Int, timeZone: Double = 7.0): IntArray {
        val dayNumber = jdFromDate(dd, mm, yy)
        val k = ((dayNumber - 2415021.07699) / 29.530588853).toInt()
        var monthStart = (newMoon(k) + 0.5 + timeZone / 24.0).toInt()
        if (monthStart > dayNumber) {
            monthStart = (newMoon(k - 1) + 0.5 + timeZone / 24.0).toInt()
        }

        var a11 = getLunarMonth11(yy, timeZone)
        var b11 = a11
        val lunarYear: Int
        if (a11 >= monthStart) {
            lunarYear = yy
            a11 = getLunarMonth11(yy - 1, timeZone)
        } else {
            lunarYear = yy + 1
            b11 = getLunarMonth11(yy + 1, timeZone)
        }

        val lunarDay = dayNumber - monthStart + 1
        val diff = ((monthStart - a11) / 29.0).toInt()
        var lunarLeap = 0
        var lunarMonth = diff + 11
        if (b11 - a11 > 365) {
            val leapMonthDiff = 3
            if (diff >= leapMonthDiff) {
                lunarMonth = diff + 10
                if (diff == leapMonthDiff) {
                    lunarLeap = 1
                }
            }
        }
        if (lunarMonth > 12) {
            lunarMonth -= 12
        }

        return intArrayOf(lunarDay, lunarMonth, lunarYear, lunarLeap)
    }

    /**
     * Tìm ngày Dương lịch tương ứng với ngày Âm lịch trong năm Âm lịch đó.
     */
    fun findSolarDateForLunar(lunarDay: Int, lunarMonth: Int, lunarYear: Int): LocalDate? {
        val start = LocalDate.of(lunarYear, 1, 1)
        val end = LocalDate.of(lunarYear + 1, 3, 1)
        var cur = start
        while (!cur.isAfter(end)) {
            val l = convertSolar2Lunar(cur.dayOfMonth, cur.monthValue, cur.year, 7.0)
            if (l[0] == lunarDay && l[1] == lunarMonth && l[2] == lunarYear) {
                return cur
            }
            cur = cur.plusDays(1)
        }
        return null
    }

    /**
     * Kiểm tra một ngày Dương lịch có trùng với ngày diễn ra của sự kiện (Âm hoặc Dương) hay không.
     */
    fun isDateMatchingReminder(date: LocalDate, reminder: EventReminderEntity): Boolean {
        return if (reminder.isLunar) {
            val l = convertSolar2Lunar(date.dayOfMonth, date.monthValue, date.year, 7.0)
            l[0] == reminder.day && l[1] == reminder.month
        } else {
            date.dayOfMonth == reminder.day && date.monthValue == reminder.month
        }
    }

    /**
     * Tính toán ngày Dương lịch tiếp theo mà sự kiện sẽ diễn ra.
     */
    fun getNextOccurrence(reminder: EventReminderEntity, fromDate: LocalDate = LocalDate.now()): LocalDate {
        return if (!reminder.isLunar) {
            val maxDayThisYear = java.time.Month.of(reminder.month).length(fromDate.isLeapYear)
            val thisYearDay = reminder.day.coerceAtMost(maxDayThisYear)
            val thisYearDate = LocalDate.of(fromDate.year, reminder.month, thisYearDay)
            if (thisYearDate.isBefore(fromDate)) {
                val nextYear = fromDate.year + 1
                val maxDayNextYear = java.time.Month.of(reminder.month).length(java.time.Year.isLeap(nextYear.toLong()))
                LocalDate.of(nextYear, reminder.month, reminder.day.coerceAtMost(maxDayNextYear))
            } else {
                thisYearDate
            }
        } else {
            val todayLunar = convertSolar2Lunar(fromDate.dayOfMonth, fromDate.monthValue, fromDate.year, 7.0)
            val curLunarYear = todayLunar[2]
            var solar = findSolarDateForLunar(reminder.day, reminder.month, curLunarYear)
            if (solar == null || solar.isBefore(fromDate)) {
                solar = findSolarDateForLunar(reminder.day, reminder.month, curLunarYear + 1)
            }
            solar ?: fromDate
        }
    }

    /**
     * Số ngày còn lại tính từ hôm nay đến ngày diễn ra tiếp theo của sự kiện.
     */
    fun getDaysUntilNextOccurrence(reminder: EventReminderEntity, fromDate: LocalDate = LocalDate.now()): Long {
        val nextOccurrence = getNextOccurrence(reminder, fromDate)
        return ChronoUnit.DAYS.between(fromDate, nextOccurrence)
    }

    fun solarToLunar(solarDate: String): LunarDate {
        val parts = solarDate.split("/").mapNotNull { it.trim().toIntOrNull() }
        val d = if (parts.isNotEmpty()) parts[0] else 1
        val m = if (parts.size > 1) parts[1] else 1
        val y = if (parts.size > 2) parts[2] else 2026

        val lunar = convertSolar2Lunar(d, m, y, 7.0)
        val ld = lunar[0]
        val lm = lunar[1]
        val ly = lunar[2]

        val canYear = CAN[(ly + 6) % 10]
        val chiYear = CHI[(ly + 8) % 12]
        val zodiacAnimal = ZODIAC[(ly + 8) % 12]
        val canChiYear = "$canYear $chiYear"

        val canMonth = CAN[(ly * 2 + lm + 3) % 10]
        val chiMonth = CHI[(lm + 1) % 12]
        val canChiMonth = "$canMonth $chiMonth"

        val jdn = jdFromDate(d, m, y)
        val canDay = CAN[(jdn + 9) % 10]
        val chiDay = CHI[(jdn + 1) % 12]
        val canChiDay = "$canDay $chiDay"

        val element = ELEMENTS[(ly % 10) / 2]
        val moonPhaseStr = when {
            ld == 1 -> "Sóc (Trăng Non)"
            ld in 2..7 -> "Trăng Lưỡi Liềm Đầu Tháng"
            ld == 8 -> "Thượng Huyền (Bán Nguyệt)"
            ld in 9..14 -> "Trăng Tròn Dần"
            ld in 15..16 -> "Vọng (Trăng Tròn)"
            ld in 17..22 -> "Trăng Khuyết Dần"
            ld == 23 -> "Hạ Huyền (Bán Nguyệt)"
            else -> "Trăng Tàn Cuối Tháng"
        }

        val fengShuiSummary = calculateFengShuiSummary(chiDay, element)
        val auspicious = getAuspiciousHours(chiDay)

        return LunarDate(
            solarDate = String.format("%02d/%02d/%04d", d, m, y),
            lunarDate = String.format("%02d/%02d/%04d", ld, lm, ly),
            day = ld.toString(),
            month = lm.toString(),
            year = ly.toString(),
            canChiYear = canChiYear,
            canChiMonth = canChiMonth,
            canChiDay = canChiDay,
            zodiac = zodiacAnimal,
            element = element,
            moonPhase = moonPhaseStr,
            fengShui = fengShuiSummary,
            auspiciousHours = auspicious,
            goodDeeds = listOf("Cầu an", "Họp mặt", "Khai trương", "Xuất hành"),
            taboos = listOf("Tranh chấp", "Kiện tụng"),
            holidays = emptyList()
        )
    }

    private fun calculateFengShuiSummary(chiDay: String, element: String): String {
        return "Ngày $chiDay, Hành $element - Trực Khai, Sao Bích Cát tinh"
    }

    fun calculateFengShuiDetail(dateStr: String): FengShui {
        val parts = dateStr.split("/").mapNotNull { it.trim().toIntOrNull() }
        val d = if (parts.isNotEmpty()) parts[0] else 1
        val m = if (parts.size > 1) parts[1] else 1
        val y = if (parts.size > 2) parts[2] else 2026
        val jdn = jdFromDate(d, m, y)

        val chi = CHI[(jdn + 1) % 12]
        val element = ELEMENTS[(y % 10) / 2]
        val yinYang = if (jdn % 2 == 0) "Dương" else "Âm"

        val directions = arrayOf("Đông Nam (Hỷ Thần)", "Chính Nam (Tài Thần)", "Tây Bắc (Quý Thần)")
        val luckyColors = arrayOf("Đỏ kim", "Vàng hoàng kim", "Trắng bạc", "Xanh lam ngọc")

        return FengShui(
            date = dateStr,
            element = element,
            branch = chi,
            yinYang = yinYang,
            energy = "Vượng khí cát lợi",
            direction = directions[jdn % directions.size],
            luckyColor = luckyColors[jdn % luckyColors.size],
            luckyNumber = ((jdn % 9) + 1).toString(),
            livestock = ZODIAC[(jdn + 1) % 12],
            gemstone = "Ngọc Bích & Thạch Anh Vàng",
            luckyAspects = listOf("Cầu tài", "Khai xuân", "Gặp gỡ quý nhân", "Tu tạo gia đường"),
            taboos = listOf("Động thổ hướng Hung sát", "Tránh đi xa đêm muộn"),
            fortuneSummary = "Khí tiết thuận hòa, âm dương cân bằng, trăm sự hanh thông."
        )
    }

    private fun getAuspiciousHours(chiDay: String): List<String> {
        return listOf(
            "Tý (23:00 - 01:00)",
            "Sửu (01:00 - 03:00)",
            "Mão (05:00 - 07:00)",
            "Ngọ (11:00 - 13:00)",
            "Thân (15:00 - 17:00)",
            "Dậu (17:00 - 19:00)"
        )
    }
}
