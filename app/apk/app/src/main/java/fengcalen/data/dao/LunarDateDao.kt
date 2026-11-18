package fengcalen.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fengcalen.data.entity.ContentEntity
import fengcalen.data.entity.FengShuiEntity
import fengcalen.data.entity.LunarDateEntity
import fengcalen.data.entity.MoonPhaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LunarDateDao {
    @Query("SELECT * FROM lunar_dates ORDER BY solarDate DESC")
    fun getAllLunarDates(): Flow<List<LunarDateEntity>>

    @Query("SELECT * FROM lunar_dates WHERE solarDate = :date LIMIT 1")
    suspend fun getLunarDateBySolar(date: String): LunarDateEntity?

    @Query("SELECT * FROM lunar_dates WHERE lunarDate = :date LIMIT 1")
    suspend fun getLunarDateByLunar(date: String): LunarDateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLunarDate(entity: LunarDateEntity)

    @Delete
    suspend fun deleteLunarDate(entity: LunarDateEntity)

    @Query("SELECT * FROM moon_phases WHERE date = :date LIMIT 1")
    suspend fun getMoonPhase(date: String): MoonPhaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoonPhase(entity: MoonPhaseEntity)

    @Query("SELECT * FROM feng_shui WHERE date = :date LIMIT 1")
    suspend fun getFengShui(date: String): FengShuiEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFengShui(entity: FengShuiEntity)

    @Query("SELECT * FROM content_items WHERE type = :type ORDER BY title ASC")
    fun getContentByType(type: String): Flow<List<ContentEntity>>

    @Query("SELECT * FROM content_items WHERE type = :type AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%')")
    fun searchContent(type: String, query: String): Flow<List<ContentEntity>>

    @Query("SELECT * FROM content_items WHERE type = :type AND id = :id LIMIT 1")
    suspend fun getContentById(type: String, id: String): ContentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(entity: ContentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllContent(entities: List<ContentEntity>)

    @Query("DELETE FROM content_items WHERE type = :type")
    suspend fun deleteByType(type: String)
}
