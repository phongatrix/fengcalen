package fengcalen.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fengcalen.data.dao.EventReminderDao
import fengcalen.data.entity.EventReminderEntity
import fengcalen.data.model.ContentItem
import fengcalen.data.model.FengShui
import fengcalen.data.model.LunarDate
import fengcalen.data.model.MoonPhase
import fengcalen.data.model.Occasion
import fengcalen.data.repository.ContentRepository
import fengcalen.data.repository.LunarDateRepository
import fengcalen.util.LunarCalendar
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val lunarRepository: LunarDateRepository,
    private val contentRepository: ContentRepository,
    private val reminderDao: EventReminderDao
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

    // Danh sách nhắc nhở sự kiện (Sinh nhật, Giỗ, Kỷ niệm...)
    val allReminders: StateFlow<List<EventReminderEntity>> = reminderDao.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Danh sách sự kiện của ngày đang được chọn trên lịch
    val selectedDateReminders: StateFlow<List<EventReminderEntity>> = combine(
        allReminders,
        selectedDate
    ) { reminders, date ->
        reminders.filter { LunarCalendar.isDateMatchingReminder(date, it) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Danh sách sự kiện sắp tới trong vòng 30 ngày (đặc biệt là 10 ngày, 1 tuần)
    val upcomingReminders: StateFlow<List<Pair<EventReminderEntity, Long>>> = allReminders
        .combine(_selectedDate) { reminders, _ ->
            val today = LocalDate.now()
            reminders
                .filter { it.isEnabled }
                .map { reminder ->
                    val daysUntil = LunarCalendar.getDaysUntilNextOccurrence(reminder, today)
                    Pair(reminder, daysUntil)
                }
                .filter { it.second in 0..30 }
                .sortedBy { it.second }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun saveReminder(reminder: EventReminderEntity) {
        viewModelScope.launch(exceptionHandler) {
            try {
                if (reminder.id == 0L) {
                    reminderDao.insertReminder(reminder)
                } else {
                    reminderDao.updateReminder(reminder)
                }
            } catch (t: Throwable) {
                Log.e("MainViewModel", "Error saving reminder: ${t.message}", t)
            }
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch(exceptionHandler) {
            try {
                reminderDao.deleteReminderById(id)
            } catch (t: Throwable) {
                Log.e("MainViewModel", "Error deleting reminder: ${t.message}", t)
            }
        }
    }

    fun toggleReminder(reminder: EventReminderEntity) {
        viewModelScope.launch(exceptionHandler) {
            try {
                reminderDao.updateReminder(reminder.copy(isEnabled = !reminder.isEnabled))
            } catch (t: Throwable) {
                Log.e("MainViewModel", "Error toggling reminder: ${t.message}", t)
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
