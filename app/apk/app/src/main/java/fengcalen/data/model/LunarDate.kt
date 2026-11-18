package fengcalen.data.model

data class LunarDate(
    val lunarDate: String,      // dd/MM/yyyy
    val solarDate: String,      // dd/MM/yyyy
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
    val auspiciousHours: List<String> = emptyList(),
    val goodDeeds: List<String> = emptyList(),
    val taboos: List<String> = emptyList(),
    val holidays: List<String> = emptyList(),
    val notes: String = ""
)
