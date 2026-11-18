package fengcalen.data.model

data class Occasion(
    val id: String,
    val title: String,
    val date: String,
    val isLunar: Boolean = true,
    val description: String = "",
    val fengshui: String = "",
    val activities: List<String> = emptyList(),
    val todos: List<String> = emptyList(),
    val artImage: String? = null
)
