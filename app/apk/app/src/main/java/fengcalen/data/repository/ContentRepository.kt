package fengcalen.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import fengcalen.data.dao.LunarDateDao
import fengcalen.data.model.ContentItem
import fengcalen.data.model.Occasion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepository @Inject constructor(
    private val dao: LunarDateDao,
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()

    fun getContent(type: String): Flow<List<ContentItem>> = flow {
        val assetPath = when (type) {
            "ca-dao" -> "alc/ca-dao.json"
            "cau-doi" -> "alc/cau-doi.json"
            "greeting" -> "alc/greetings.json"
            "lexicon" -> "alc/lexicon.json"
            "provinces" -> "regions/provinces.json"
            else -> null
        }

        if (assetPath != null) {
            try {
                val json = context.assets.open(assetPath).bufferedReader().use { it.readText() }
                val parsed = parseJsonToContent(type, json, assetPath)
                if (parsed.isNotEmpty()) {
                    emit(parsed)
                    return@flow
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        emit(emptyList())
    }.flowOn(Dispatchers.IO)

    fun loadAllOccasions(): Flow<List<Occasion>> = flow {
        val occasions = mutableListOf<Occasion>()
        try {
            val occasionFiles = context.assets.list("occasions") ?: emptyArray()
            for (fileName in occasionFiles) {
                if (fileName.endsWith(".json")) {
                    try {
                        val json = context.assets.open("occasions/$fileName").bufferedReader().use { it.readText() }
                        val map = gson.fromJson<Map<String, Any>>(json, object : TypeToken<Map<String, Any>>() {}.type)
                        val id = fileName.removeSuffix(".json")
                        val occasion = Occasion(
                            id = id,
                            title = map["name"]?.toString() ?: map["title"]?.toString() ?: id,
                            date = map["date"]?.toString() ?: "",
                            isLunar = (map["calendar"]?.toString() ?: "lunar") == "lunar",
                            description = map["summary"]?.toString() ?: map["description"]?.toString() ?: map["body"]?.toString() ?: "",
                            fengshui = map["fengshui"]?.toString() ?: "",
                            activities = (map["activities"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList(),
                            todos = (map["todos"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList(),
                            artImage = "occasion-art/${id}-600.webp"
                        )
                        occasions.add(occasion)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        emit(occasions)
    }.flowOn(Dispatchers.IO)

    fun loadVanKhan(id: String): Flow<String> = flow {
        val fileName = if (id.endsWith(".md")) id else "$id.md"
        val text = try {
            context.assets.open("van-khan/$fileName").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            "Nội dung văn khấn đang được cập nhật."
        }
        emit(text)
    }.flowOn(Dispatchers.IO)

    fun listVanKhanFiles(): List<String> {
        return try {
            context.assets.list("van-khan")?.toList()?.filter { it.endsWith(".md") } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseJsonToContent(type: String, json: String, path: String): List<ContentItem> {
        return try {
            val root = gson.fromJson<Map<String, Any>>(json, object : TypeToken<Map<String, Any>>() {}.type)
            val list = (root["items"] as? List<*>)
                ?: (root["provinces"] as? List<*>)
                ?: (root["animals"] as? List<*>)
                ?: emptyList<Any>()

            list.mapNotNull { item ->
                if (item !is Map<*, *>) return@mapNotNull null
                val id = item["id"]?.toString() ?: UUID.randomUUID().toString()
                val title = item["title"]?.toString()
                    ?: item["name"]?.toString()
                    ?: item["nameVi"]?.toString()
                    ?: item["left"]?.toString()
                    ?: ""
                val content = item["content"]?.toString()
                    ?: item["lyrics"]?.toString()
                    ?: item["greeting"]?.toString()
                    ?: if (item.containsKey("left") && item.containsKey("right")) {
                        "${item["left"]} \n ${item["right"]}"
                    } else item["summary"]?.toString() ?: ""
                val theme = item["theme"]?.toString()
                    ?: item["occasion"]?.toString()
                    ?: item["region"]?.toString()
                    ?: ""
                val tags = (item["tags"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()

                ContentItem(
                    id = id,
                    type = type,
                    title = title,
                    content = content,
                    theme = theme,
                    tags = tags,
                    source = "assets://$path"
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
