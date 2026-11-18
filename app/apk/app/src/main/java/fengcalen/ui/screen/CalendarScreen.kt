package fengcalen.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fengcalen.data.entity.EventReminderEntity
import fengcalen.ui.viewmodel.MainViewModel
import fengcalen.util.LunarCalendar
import fengcalen.util.NokiaSoundHelper
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController, viewModel: MainViewModel) {
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    val selectedDate by viewModel.selectedDate.collectAsState()
    val todayLunar by viewModel.todayLunar.collectAsState()
    val allReminders by viewModel.allReminders.collectAsState()
    val selectedDateReminders by viewModel.selectedDateReminders.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var reminderToEdit by remember { mutableStateOf<EventReminderEntity?>(null) }

    val daysInMonth = remember(currentYearMonth) {
        val firstDay = currentYearMonth.atDay(1)
        val dayOfWeekOffset = (firstDay.dayOfWeek.value + 6) % 7 // Monday = 0
        val totalDays = currentYearMonth.lengthOfMonth()
        val list = mutableListOf<LocalDate?>()
        for (i in 0 until dayOfWeekOffset) {
            list.add(null)
        }
        for (day in 1..totalDays) {
            list.add(currentYearMonth.atDay(day))
        }
        list
    }

    // Selected lunar date calculation
    val currentSelectedLunar = remember(selectedDate) {
        LunarCalendar.convertSolar2Lunar(selectedDate.dayOfMonth, selectedDate.monthValue, selectedDate.year)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch Tháng Vạn Niên", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Trở về")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        reminderToEdit = null
                        showCreateDialog = true
                    }) {
                        Icon(Icons.Default.AddAlert, contentDescription = "Thêm nhắc nhở")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    reminderToEdit = null
                    showCreateDialog = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Thêm nhắc nhở") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Month Header Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentYearMonth = currentYearMonth.minusMonths(1) }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Tháng trước")
                }
                Text(
                    text = "Tháng ${currentYearMonth.monthValue} năm ${currentYearMonth.year}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { currentYearMonth = currentYearMonth.plusMonths(1) }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Tháng sau")
                }
            }

            // Days of week header
            val dayHeaders = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
            Row(modifier = Modifier.fillMaxWidth()) {
                dayHeaders.forEachIndexed { index, header ->
                    Text(
                        text = header,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = if (index == 6) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Calendar Grid
            val totalWeeks = (daysInMonth.size + 6) / 7
            val gridHeight = (totalWeeks * 54).dp + 16.dp

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(gridHeight)
                        .padding(8.dp),
                    userScrollEnabled = false
                ) {
                    items(daysInMonth) { date ->
                        if (date == null) {
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .fillMaxWidth()
                                    .height(50.dp)
                            )
                        } else {
                            val isSelected = date == selectedDate
                            val isToday = date == LocalDate.now()
                            val lunarData = remember(date) {
                                LunarCalendar.convertSolar2Lunar(date.dayOfMonth, date.monthValue, date.year)
                            }
                            val lunarDay = lunarData[0]
                            val lunarMonth = lunarData[1]
                            val isSpecialLunar = lunarDay == 1 || lunarDay == 15

                            // Check if this date has any user-created event reminders
                            val dayEvents = remember(allReminders, date) {
                                allReminders.filter { LunarCalendar.isDateMatchingReminder(date, it) }
                            }
                            val hasEvent = dayEvents.isNotEmpty()

                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            isToday -> MaterialTheme.colorScheme.primaryContainer
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable { viewModel.selectDate(date) }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                // Ngày tây (dương lịch) - canh giữa phía trên
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = when {
                                        isSelected -> MaterialTheme.colorScheme.onPrimary
                                        date.dayOfWeek.value == 7 -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.onSurface
                                    },
                                    modifier = Modifier.align(Alignment.TopCenter)
                                )

                                // Ngày âm - hiển thị ở góc phải, ngay bên dưới ngày tây
                                Text(
                                    text = if (lunarDay == 1) "$lunarDay/$lunarMonth" else lunarDay.toString(),
                                    fontSize = 9.5.sp,
                                    fontWeight = if (isSpecialLunar || isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        isSelected -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                                        isSpecialLunar -> Color(0xFFEF4444)
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                                    },
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                )

                                // Dấu chấm đánh dấu sự kiện nhắc nhở (Sinh nhật, Giỗ, v.v...)
                                if (hasEvent) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) Color.White else Color(0xFFE11D48)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Selected Date Detail Card
            todayLunar?.let { lunar ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Chi tiết ngày: ${lunar.solarDate}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            FilledTonalButton(
                                onClick = {
                                    reminderToEdit = null
                                    showCreateDialog = true
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tạo nhắc nhở", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Âm lịch: Ngày ${lunar.day} tháng ${lunar.month} năm ${lunar.year}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Ngày: ${lunar.canChiDay} • Tháng: ${lunar.canChiMonth} • Năm: ${lunar.canChiYear}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Giờ hoàng đạo:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = lunar.auspiciousHours.joinToString(", "),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Reminders for Selected Date
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sự kiện & Nhắc nhở ngày này",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${selectedDateReminders.size} sự kiện",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (selectedDateReminders.isEmpty()) {
                        Text(
                            text = "Chưa có sự kiện nhắc nhở nào cho ngày này. Chạm '+ Tạo nhắc nhở' để thêm ngày sinh nhật, ngày giỗ...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            selectedDateReminders.forEach { reminder ->
                                ReminderItemCard(
                                    reminder = reminder,
                                    onEdit = {
                                        reminderToEdit = reminder
                                        showCreateDialog = true
                                    },
                                    onDelete = {
                                        viewModel.deleteReminder(reminder.id)
                                    },
                                    onToggle = {
                                        viewModel.toggleReminder(reminder)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp)) // Space for FAB
        }
    }

    // Dialog Create / Edit Reminder
    if (showCreateDialog) {
        CreateEditReminderDialog(
            initialDate = selectedDate,
            initialIsLunar = true,
            initialLunarDay = currentSelectedLunar[0],
            initialLunarMonth = currentSelectedLunar[1],
            reminderToEdit = reminderToEdit,
            onDismiss = { showCreateDialog = false },
            onSave = { reminder ->
                viewModel.saveReminder(reminder)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun ReminderItemCard(
    reminder: EventReminderEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggle: () -> Unit
) {
    val icon = when (reminder.eventType) {
        "BIRTHDAY" -> "🎂"
        "MEMORIAL" -> "🕯️"
        "ANNIVERSARY" -> "💍"
        else -> "📌"
    }

    val typeLabel = when (reminder.eventType) {
        "BIRTHDAY" -> "Sinh nhật"
        "MEMORIAL" -> "Ngày giỗ"
        "ANNIVERSARY" -> "Kỷ niệm"
        else -> "Sự kiện"
    }

    val toneName = NokiaSoundHelper.TONES.find { it.id == reminder.ringtone }?.name ?: "Mặc định"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = icon, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = reminder.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$typeLabel • ${if (reminder.isLunar) "Âm lịch" else "Dương lịch"} (${reminder.day}/${reminder.month})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "🎵 $toneName • Báo trước: ${formatRemindDays(reminder.remindDaysBefore)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Sửa", modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Xóa",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

fun formatRemindDays(daysStr: String): String {
    val days = daysStr.split(",").mapNotNull { it.trim().toIntOrNull() }
    if (days.isEmpty()) return "Đúng ngày"
    return days.sorted().joinToString(", ") { d ->
        when (d) {
            0 -> "Đúng ngày"
            1 -> "1 ngày"
            3 -> "3 ngày"
            7 -> "1 tuần"
            10 -> "10 ngày"
            else -> "$d ngày"
        }
    }
}
