package fengcalen.data.model

data class MoonPhase(
    val date: String,
    val phase: String,
    val phaseNameVi: String,
    val illumination: Float,
    val ageDays: Float,
    val constellation: String,
    val energy: String,
    val color: String,
    val keywords: List<String> = emptyList(),
    val recommended: Boolean = false
)

enum class PhaseType {
    NEW_MOON, WAXING_CRESCENT, FIRST_QUARTER, WAXING_GIBBOUS,
    FULL_MOON, WANING_GIBBOUS, LAST_QUARTER, WANING_CRESCENT
}
