package fengcalen.data.model

data class FengShui(
    val date: String,
    val element: String,
    val branch: String,
    val yinYang: String,
    val energy: String,
    val direction: String,
    val luckyColor: String,
    val luckyNumber: String,
    val livestock: String,
    val gemstone: String,
    val luckyAspects: List<String> = emptyList(),
    val taboos: List<String> = emptyList(),
    val fortuneSummary: String = ""
)
