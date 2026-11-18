package fengcalen.data.model

data class ContentItem(
    val id: String,
    val type: String, // "ca-dao", "cau-doi", "greeting", "lexicon", "van-khan"
    val title: String,
    val content: String,
    val theme: String,
    val tags: List<String> = emptyList(),
    val source: String = ""
)
