# PART 03 — Data Layer (Room, DataStore, Repository, Models)

## Mục tiêu

Tạo:
- Room entities và DAO
- DataStore preferences
- Repository pattern
- Models: LunarDate, MoonPhase, FengShui, ContentItem
- Hilt dependency injection

## Thực hiện

```bash
mkdir -p app/src/main/java/fengcalen/data/entity
mkdir -p app/src/main/java/fengcalen/data/dao
mkdir -p app/src/main/java/fengcalen/data/repository
mkdir -p app/src/main/java/fengcalen/data/model
```

## Tạo app/src/main/java/fengcalen/data/model/LunarDate.kt

```kotlin
package fengcalen.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lunar_dates")
data class LunarDate(
    @PrimaryKey val lunarDate: String,
    val solarDate: String,
    val day: String,
    val month: String,
    val year: String,
    val zodiac: String,
    val element: String,
    val moonPhase: String,
    val fengShui: String,
    val holidays: List<String> = emptyList(),
    val notes: String = ""
)
```

## Tạo app/src/main/java/fengcalen/data/model/MoonPhase.kt

```kotlin
package fengcalen.data.model

data class MoonPhase(
    val date: String,
    val phase: String,
    val illumination: Float,
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
```

## Tạo app/src/main/java/fengcalen/data/model/FengShui.kt

```kotlin
package fengcalen.data.model

data class FengShui(
    val date: String,
    val element: String,
    val branch: String,
    val yinYang: String,
    val energy: String,
    val direction: String,
    val color: String,
    val number: String,
    val livestock: String,
    val gemstone: String,
    val lucky: String,
    val inauspicious: String,
    val fortune: String
)
```

## Tạo app/src/main/java/fengcalen/data/model/ContentItem.kt

```kotlin
package fengcalen.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "content_items")
data class ContentItem(
    @PrimaryKey val id: String,
    val type: String, // ca-dao, cau-doi, greeting, occasion, van-khan
    val title: String,
    val content: String,
    val theme: String,
    val tags: List<String> = emptyList(),
    val source: String = ""
)
```

## Tạo app/src/main/java/fengcalen/data/entity/LunarDateEntity.kt

```kotlin
package fengcalen.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lunar_dates")
data class LunarDateEntity(
    @PrimaryKey val lunarDate: String,
    val solarDate: String,
    val day: String,
    val month: String,
    val year: String,
    val zodiac: String,
    val element: String,
    val moonPhase: String,
    val fengShui: String,
    val holidays: String,
    val notes: String
)

@Entity(tableName = "moon_phases")
data class MoonPhaseEntity(
    val date: String,
    val phase: String,
    val illumination: Float,
    val constellation: String,
    val energy: String,
    val color: String,
    val keywords: String,
    val recommended: Boolean
)

@Entity(tableName = "feng_shui")
data class FengShuiEntity(
    val date: String,
    val element: String,
    val branch: String,
    val yinYang: String,
    val energy: String,
    val direction: String,
    val color: String,
    val number: String,
    val livestock: String,
    val gemstone: String,
    val lucky: String,
    val inauspicious: String,
    val fortune: String
)

@Entity(tableName = "content_items")
data class ContentEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val content: String,
    val theme: String,
    val tags: String,
    val source: String
)
```

## Tạo app/src/main/java/fengcalen/data/dao/LunarDateDao.kt

```kotlin
package fengcalen.data.dao

import androidx.room.*
import fengcalen.data.entity.LunarDateEntity
import fengcalen.data.entity.MoonPhaseEntity
import fengcalen.data.entity.ContentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LunarDateDao {
    @Query("SELECT * FROM lunar_dates ORDER BY lunarDate DESC")
    fun getAllLunarDates(): Flow<List<LunarDateEntity>>

    @Query("SELECT * FROM lunar_dates WHERE lunarDate = :date")
    suspend fun getLunarDate(date: String): LunarDateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLunarDate(entity: LunarDateEntity)

    @Delete
    suspend fun deleteLunarDate(entity: LunarDateEntity)

    @Query("SELECT * FROM moon_phases ORDER BY date DESC")
    fun getAllMoonPhases(): Flow<List<MoonPhaseEntity>>

    @Query("SELECT * FROM moon_phases WHERE date = :date")
    suspend fun getMoonPhase(date: String): MoonPhaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoonPhase(entity: MoonPhaseEntity)

    @Query("SELECT * FROM content_items WHERE type = :type ORDER BY title")
    fun getContentByType(type: String): Flow<List<ContentEntity>>

    @Query("SELECT * FROM content_items WHERE type = :type AND id = :id")
    suspend fun getContentById(type: String, id: String): ContentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(entity: ContentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllContent(entities: List<ContentEntity>)

    @Query("DELETE FROM content_items WHERE type = :type")
    suspend fun deleteByType(type: String)
}
```

## Tạo app/src/main/java/fengcalen/data/repository/LunarDateRepository.kt

```kotlin
package fengcalen.data.repository

import fengcalen.data.dao.LunarDateDao
import fengcalen.data.entity.LunarDateEntity
import fengcalen.data.entity.MoonPhaseEntity
import fengcalen.data.entity.ContentEntity
import fengcalen.data.model.ContentItem
import fengcalen.data.model.FengShui
import fengcalen.data.model.LunarDate
import fengcalen.data.model.MoonPhase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LunarDateRepository @Inject constructor(
    private val dao: LunarDateDao
) {
    val allLunarDates: Flow<List<LunarDateEntity>> = dao.getAllLunarDates()
    val allMoonPhases: Flow<List<MoonPhaseEntity>> = dao.getAllMoonPhases()

    fun getLunarDatesFlow(): Flow<List<LunarDateEntity>> = dao.getAllLunarDates()
    fun getMoonPhasesFlow(): Flow<List<MoonPhaseEntity>> = dao.getAllMoonPhases()

    suspend fun getLunarDate(date: String): LunarDateEntity? = dao.getLunarDate(date)

    suspend fun insertLunarDate(entity: LunarDateEntity) {
        dao.insertLunarDate(entity)
    }

    suspend fun deleteLunarDate(entity: LunarDateEntity) {
        dao.deleteLunarDate(entity)
    }

    suspend fun saveMoonPhase(entity: MoonPhaseEntity) {
        dao.insertMoonPhase(entity)
    }

    fun getContentByType(type: String): Flow<List<ContentEntity>> = dao.getContentByType(type)

    suspend fun getContentById(type: String, id: String): ContentEntity? =
        dao.getContentById(type, id)

    suspend fun insertContent(entity: ContentEntity) {
        dao.insertContent(entity)
    }

    suspend fun insertAllContent(entities: List<ContentEntity>) {
        dao.insertAllContent(entities)
    }

    suspend fun deleteContentType(type: String) {
        dao.deleteByType(type)
    }

    fun toModel(entity: LunarDateEntity): LunarDate = LunarDate(
        lunarDate = entity.lunarDate,
        solarDate = entity.solarDate,
        day = entity.day,
        month = entity.month,
        year = entity.year,
        zodiac = entity.zodiac,
        element = entity.element,
        moonPhase = entity.moonPhase,
        fengShui = entity.fengShui,
        holidays = entity.holidays.split(",").filter { it.isNotEmpty() },
        notes = entity.notes
    )

    fun toModel(entity: MoonPhaseEntity): MoonPhase = MoonPhase(
        date = entity.date,
        phase = entity.phase,
        illumination = entity.illumination,
        constellation = entity.constellation,
        energy = entity.energy,
        color = entity.color,
        keywords = entity.keywords.split(",").filter { it.isNotEmpty() },
        recommended = entity.recommended
    )

    fun toModel(entity: ContentEntity): ContentItem = ContentItem(
        id = entity.id,
        type = entity.type,
        title = entity.title,
        content = entity.content,
        theme = entity.theme,
        tags = entity.tags.split(",").filter { it.isNotEmpty() },
        source = entity.source
    )

    fun toEntity(model: LunarDate): LunarDateEntity = LunarDateEntity(
        lunarDate = model.lunarDate,
        solarDate = model.solarDate,
        day = model.day,
        month = model.month,
        year = model.year,
        zodiac = model.zodiac,
        element = model.element,
        moonPhase = model.moonPhase,
        fengShui = model.fengShui,
        holidays = model.holidays.joinToString(","),
        notes = model.notes
    )

    fun toEntity(model: MoonPhase): MoonPhaseEntity = MoonPhaseEntity(
        date = model.date,
        phase = model.phase,
        illumination = model.illumination,
        constellation = model.constellation,
        energy = model.energy,
        color = model.color,
        keywords = model.keywords.joinToString(","),
        recommended = model.recommended
    )

    fun toEntity(model: ContentItem): ContentEntity = ContentEntity(
        id = model.id,
        type = model.type,
        title = model.title,
        content = model.content,
        theme = model.theme,
        tags = model.tags.joinToString(","),
        source = model.source
    )
}
```

## Tạo app/src/main/java/fengcalen/data/repository/ContentRepository.kt

```kotlin
package fengcalen.data.repository

import android.content.Context
import fengcalen.data.model.ContentItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepository @Inject constructor(
    private val dao: LunarDateDao,
    private val context: Context
) {
    fun loadContentFromAssets(type: String): Flow<List<ContentItem>> = flow {
        withContext(Dispatchers.IO) {
            val assetManager = context.assets
            val assetPath = when (type) {
                "ca-dao" -> "alc/ca-dao.json"
                "cau-doi" -> "alc/cau-doi.json"
                "greeting" -> "alc/greetings.json"
                "lexicon" -> "alc/lexicon.json"
                "provinces" -> "regions/provinces.json"
                else -> {
                    emit(emptyList())
                    return@withContext
                }
            }
            try {
                val json = assetManager.open(assetPath).bufferedReader().use { it.readText() }
                val items = com.google.gson.Gson().fromJson<List<*>>(json, Array<Any>::class.java)
                    .map { it as Map<String, Any> }
                val itemsWithId = items.mapNotNull { map ->
                    val id = map["id"]?.toString()?.ifEmpty { null } ?: map["_id"]?.toString()?.ifEmpty { null }
                    if (id == null) null else map
                }
                val models = itemsWithId.map { map ->
                    ContentItem(
                        id = map["id"]?.toString() ?: UUID.randomUUID().toString(),
                        type = type,
                        title = map["title"]?.toString() ?: map["name"]?.toString() ?: "",
                        content = map["content"]?.toString() ?: map["lyrics"]?.toString() ?: "",
                        theme = map["theme"]?.toString() ?: "",
                        tags = (map["tags"] as? List<*>)?.map { it as? String } ?: emptyList(),
                        source = "asset://$assetPath"
                    )
                }
                emit(models)
            } catch (e: Exception) {
                emit(emptyList())
            }
        }
    }.flowOn(Dispatchers.IO)
}
```

## Kiểm tra Part 03

Sau khi chạy, kiểm tra:
- [ ] Cấu trúc thư mục `data/` đầy đủ
- [ ] `LunarDate.kt`, `MoonPhase.kt`, `FengShui.kt`, `ContentItem.kt` có
- [ ] `LunarDateEntity.kt`, `MoonPhaseEntity.kt`, `ContentEntity.kt` có
- [ ] `LunarDateDao.kt` có
- [ ] `LunarDateRepository.kt` có
- [ ] `ContentRepository.kt` có

## Lệnh kiểm tra

```bash
cd /home/ibm/rea-analyze
echo "=== Kiểm tra Part 03 ==="
find app/src/main/java/fengcalen/data -name "*.kt" | sort
```
