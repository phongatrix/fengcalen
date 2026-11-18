package fengcalen.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fengcalen.data.entity.EventReminderEntity
import fengcalen.ui.viewmodel.MainViewModel
import fengcalen.util.LunarCalendar
import fengcalen.util.NokiaSoundHelper
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(navController: NavController, viewModel: MainViewModel) {
    val allReminders by viewModel.allReminders.collectAsState()
    val upcomingReminders by viewModel.upcomingReminders.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, MEMORIAL, BIRTHDAY, ANNIVERSARY
    var showCreateDialog by remember { mutableStateOf(false) }
    var reminderToEdit by remember { mutableStateOf<EventReminderEntity?>(null) }

    val filteredReminders = remember(allReminders, selectedFilter) {
        if (selectedFilter == "ALL") allReminders
        else allReminders.filter { it.eventType == selectedFilter }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản Lý Nhắc Nhở", fontWeight = FontWeight.Bold) },
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
                text = { Text("Tạo sự kiện mới") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Upcoming highlights (Sắp tới trong 30 ngày)
                if (upcomingReminders.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AccessAlarm,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sự kiện sắp diễn ra",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                upcomingReminders.take(3).forEach { (reminder, daysUntil) ->
                                    val icon = when (reminder.eventType) {
                                        "BIRTHDAY" -> "🎂"
                                        "MEMORIAL" -> "🕯️"
                                        "ANNIVERSARY" -> "💍"
                                        else -> "📌"
                                    }
                                    val countdownText = when (daysUntil) {
                                        0L -> "HÔM NAY!"
                                        1L -> "Ngày mai"
                                        7L -> "1 tuần nữa"
                                        10L -> "10 ngày nữa"
                                        else -> "Còn $daysUntil ngày"
                                    }

                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = icon, fontSize = 20.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = reminder.title,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp
                                                    )
                                                    Text(
                                                        text = "Ngày ${reminder.day}/${reminder.month} (${if (reminder.isLunar) "Âm lịch" else "Dương lịch"})",
                                                        fontSize = 11.5.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                            Surface(
                                                color = if (daysUntil <= 3) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = countdownText,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (daysUntil <= 3) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Filter Chips
                Text(
                    text = "Bộ lọc phân loại:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf(
                        Pair("ALL", "Tất cả"),
                        Pair("MEMORIAL", "🕯️ Ngày giỗ"),
                        Pair("BIRTHDAY", "🎂 Sinh nhật"),
                        Pair("ANNIVERSARY", "💍 Kỷ niệm")
                    )
                    filters.forEach { (key, label) ->
                        FilterChip(
                            selected = selectedFilter == key,
                            onClick = { selectedFilter = key },
                            label = { Text(label, fontSize = 12.sp) }
                        )
                    }
                }
            }

            if (filteredReminders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.EventBusy,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Chưa có sự kiện nhắc nhở nào",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Bấm 'Tạo sự kiện mới' để thêm ngày sinh nhật, ngày giỗ...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredReminders, key = { it.id }) { reminder ->
                    ReminderFullCard(
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

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Padding for FAB
            }
        }
    }

    if (showCreateDialog) {
        val today = LocalDate.now()
        val lunar = remember(today) {
            LunarCalendar.convertSolar2Lunar(today.dayOfMonth, today.monthValue, today.year)
        }
        CreateEditReminderDialog(
            initialDate = today,
            initialIsLunar = true,
            initialLunarDay = lunar[0],
            initialLunarMonth = lunar[1],
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
fun ReminderFullCard(
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
    val daysUntil = remember(reminder) {
        LunarCalendar.getDaysUntilNextOccurrence(reminder, LocalDate.now())
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isEnabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (reminder.isEnabled) 2.dp else 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = icon, fontSize = 28.sp)
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
                    }
                }

                Switch(
                    checked = reminder.isEnabled,
                    onCheckedChange = { onToggle() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🔔 Báo trước: ${formatRemindDays(reminder.remindDaysBefore)} • ${reminder.remindTime}",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "🎵 Nhạc báo: $toneName",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (reminder.note.isNotEmpty()) {
                        Text(
                            text = "📝 Ghi chú: ${reminder.note}",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa", modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Xóa",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
