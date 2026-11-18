package fengcalen.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fengcalen.data.model.ContentItem
import fengcalen.data.model.FengShui
import fengcalen.data.model.LunarDate
import fengcalen.data.model.MoonPhase
import fengcalen.data.model.Occasion
import fengcalen.data.repository.ContentRepository
import fengcalen.data.repository.LunarDateRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val lunarRepository: LunarDateRepository,
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("MainViewModel", "Unhandled coroutine error caught safely: ${throwable.message}", throwable)
    }

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _todayLunar = MutableStateFlow<LunarDate?>(null)
    val todayLunar: StateFlow<LunarDate?> = _todayLunar.asStateFlow()

    private val _moonPhase = MutableStateFlow<MoonPhase?>(null)
    val moonPhase: StateFlow<MoonPhase?> = _moonPhase.asStateFlow()

    private val _fengShui = MutableStateFlow<FengShui?>(null)
    val fengShui: StateFlow<FengShui?> = _fengShui.asStateFlow()

    private val _caDaoList = MutableStateFlow<List<ContentItem>>(emptyList())
    val caDaoList: StateFlow<List<ContentItem>> = _caDaoList.asStateFlow()

    private val _cauDoiList = MutableStateFlow<List<ContentItem>>(emptyList())
    val cauDoiList: StateFlow<List<ContentItem>> = _cauDoiList.asStateFlow()

    private val _greetingsList = MutableStateFlow<List<ContentItem>>(emptyList())
    val greetingsList: StateFlow<List<ContentItem>> = _greetingsList.asStateFlow()

    private val _occasions = MutableStateFlow<List<Occasion>>(emptyList())
    val occasions: StateFlow<List<Occasion>> = _occasions.asStateFlow()

    private val _activeVanKhan = MutableStateFlow<String?>(null)
    val activeVanKhan: StateFlow<String?> = _activeVanKhan.asStateFlow()

    private val _isDarkTheme = MutableStateFlow<Boolean?>(null) // null = system
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()

    private val _currentLanguage = MutableStateFlow("vi")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    init {
        loadDateData(LocalDate.now())
        loadContent()
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadDateData(date)
    }

    private fun loadDateData(date: LocalDate) {
        viewModelScope.launch(exceptionHandler) {
            try {
                val dateStr = date.format(dateFormatter)
                _todayLunar.value = lunarRepository.getLunarDate(dateStr)
                _moonPhase.value = lunarRepository.getMoonPhase(dateStr)
                _fengShui.value = lunarRepository.getFengShui(dateStr)
            } catch (t: Throwable) {
                Log.e("MainViewModel", "Error loading date data: ${t.message}", t)
            }
        }
    }

    private fun loadContent() {
        viewModelScope.launch(exceptionHandler) {
            try {
                contentRepository.getContent("ca-dao").collect { items ->
                    _caDaoList.value = items
                }
            } catch (t: Throwable) {
                Log.e("MainViewModel", "Error loading ca-dao: ${t.message}", t)
            }
        }
        viewModelScope.launch(exceptionHandler) {
            try {
                contentRepository.getContent("cau-doi").collect { items ->
                    _cauDoiList.value = items
                }
            } catch (t: Throwable) {
                Log.e("MainViewModel", "Error loading cau-doi: ${t.message}", t)
            }
        }
        viewModelScope.launch(exceptionHandler) {
            try {
                contentRepository.getContent("greeting").collect { items ->
                    _greetingsList.value = items
                }
            } catch (t: Throwable) {
                Log.e("MainViewModel", "Error loading greetings: ${t.message}", t)
            }
        }
        viewModelScope.launch(exceptionHandler) {
            try {
                contentRepository.loadAllOccasions().collect { list ->
                    _occasions.value = list
                }
            } catch (t: Throwable) {
                Log.e("MainViewModel", "Error loading occasions: ${t.message}", t)
            }
        }
    }

    fun openVanKhan(id: String) {
        viewModelScope.launch(exceptionHandler) {
            try {
                contentRepository.loadVanKhan(id).collect { text ->
                    _activeVanKhan.value = text
                }
            } catch (t: Throwable) {
                Log.e("MainViewModel", "Error loading van-khan: ${t.message}", t)
            }
        }
    }

    fun closeVanKhan() {
        _activeVanKhan.value = null
    }

    fun setDarkMode(dark: Boolean?) {
        _isDarkTheme.value = dark
    }

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }
}
