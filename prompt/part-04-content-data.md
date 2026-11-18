# PART 04 — Content Data (assets, JSON parsing, ContentProvider)

## Mục tiêu

Tạo:
- ContentProvider lấy dữ liệu từ `assets/`
- Service đọc và parse JSON từ assets
- Hilt module để inject ContentService
- Models từ JSON (ca-dao, cau-doi, greetings, lexicon, occasions, van-khan, provinces)

## Thực hiện

```bash
mkdir -p app/src/main/java/fengcalen/service
mkdir -p app/src/main/java/fengcalen/provider
```

## Tạo app/src/main/java/fengcalen/provider/ContentProvider.kt

```kotlin
package fengcalen.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import dagger.hilt.android.AndroidEntryPoint
import fengcalen.data.model.ContentItem
import fengcalen.data.repository.ContentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContentProvider : ContentProvider() {
    companion object {
        val CONTENT_AUTHORITY = "fengcalen.provider"
        val CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY/content")

        private var contentRepository: ContentRepository? = null

        fun getContentRepository(context: android.content.Context): ContentRepository {
            if (contentRepository == null) {
                contentRepository = ContentRepository(
                    context.assets
                )
            }
            return contentRepository!!
        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    @Suppress("UNCHECKED_CAST")
    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val type = ContentResolver.getType(uri)
        // For assets, use a simple CSV-like cursor
        // In production, use a real ContentProvider with database
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null
}
```

## Tạo app/src/main/java/fengcalen/service/ContentService.kt

```kotlin
package fengcalen.service

import android.content.Context
import com.google.gson.Gson
import fengcalen.data.model.ContentItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentService @Inject constructor() {
    fun loadCaDao(context: Context): Flow<List<ContentItem>> = loadFromAsset(context, "alc/ca-dao.json", "ca-dao")
    fun loadCauDoi(context: Context): Flow<List<ContentItem>> = loadFromAsset(context, "alc/cau-doi.json", "cau-doi")
    fun loadGreetings(context: Context): Flow<List<ContentItem>> = loadFromAsset(context, "alc/greetings.json", "greeting")
    fun loadLexicon(context: Context): Flow<List<ContentItem>> = loadFromAsset(context, "alc/lexicon.json", "lexicon")
    fun loadProvinces(context: Context): Flow<List<ContentItem>> = loadFromAsset(context, "regions/provinces.json", "provinces")

    @Suppress("UNCHECKED_CAST")
    private fun <T> loadFromAsset(context: Context, path: String, type: String): Flow<List<ContentItem>> = flow {
        try {
            val assetManager = context.assets
            val json = assetManager.open(path).bufferedReader().use { it.readText() }
            val items = Gson().fromJson<List<*>>(json, Array<Any>::class.java)
                .map { it as Map<String, Any> }
            val models = items.mapNotNull { map ->
                val id = map["id"]?.toString()?.ifEmpty { null } ?: map["_id"]?.toString()?.ifEmpty { null }
                if (id == null) null else map
            }
            val results = models.map { map ->
                ContentItem(
                    id = map["id"]?.toString() ?: UUID.randomUUID().toString(),
                    type = type,
                    title = map["title"]?.toString() ?: map["name"]?.toString() ?: "",
                    content = map["content"]?.toString() ?: map["lyrics"]?.toString() ?: map["left"]?.toString() ?: "",
                    theme = map["theme"]?.toString() ?: "",
                    tags = (map["tags"] as? List<*>)?.map { it as? String } ?: emptyList(),
                    source = "asset://$path"
                )
            }
            emit(results)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }
}
```

## Tạo app/src/main/java/fengcalen/provider/ContentProvider.kt (Giảm thiểu)

```kotlin
package fengcalen.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

class ContentProvider : ContentProvider() {
    override fun onCreate(): Boolean = true
    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun getType(uri: Uri): String? = null
}
```

## Kiểm tra Part 04

Sau khi chạy, kiểm tra:
- [ ] Cấu trúc thư mục `service/`, `provider/`
- [ ] `ContentService.kt` tồn tại
- [ ] `ContentProvider.kt` tồn tại
- [ ] Dữ liệu JSON có thể đọc được từ assets

## Lệnh kiểm tra

```bash
cd /home/ibm/rea-analyze
echo "=== Kiểm tra Part 04 ==="
ls app/src/main/java/fengcalen/service/ 2>/dev/null
ls app/src/main/java/fengcalen/provider/ 2>/dev/null
echo "=== Kiểm tra assets ==="
ls -la app/src/main/assets/alc/ 2>/dev/null
ls -la app/src/main/assets/occasions/ 2>/dev/null
ls -la app/src/main/assets/van-khan/ 2>/dev/null
ls -la app/src/main/assets/regions/ 2>/dev/null
```
