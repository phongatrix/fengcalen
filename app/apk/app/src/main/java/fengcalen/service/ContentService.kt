package fengcalen.service

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import fengcalen.data.model.ContentItem
import fengcalen.data.model.Occasion
import fengcalen.data.repository.ContentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentService @Inject constructor(
    private val contentRepository: ContentRepository,
    @ApplicationContext private val context: Context
) {
    fun loadCaDao(): Flow<List<ContentItem>> = contentRepository.getContent("ca-dao")
    fun loadCauDoi(): Flow<List<ContentItem>> = contentRepository.getContent("cau-doi")
    fun loadGreetings(): Flow<List<ContentItem>> = contentRepository.getContent("greeting")
    fun loadLexicon(): Flow<List<ContentItem>> = contentRepository.getContent("lexicon")
    fun loadProvinces(): Flow<List<ContentItem>> = contentRepository.getContent("provinces")
    fun loadOccasions(): Flow<List<Occasion>> = contentRepository.loadAllOccasions()
    fun loadVanKhan(id: String): Flow<String> = contentRepository.loadVanKhan(id)
    fun listVanKhans(): List<String> = contentRepository.listVanKhanFiles()
}
