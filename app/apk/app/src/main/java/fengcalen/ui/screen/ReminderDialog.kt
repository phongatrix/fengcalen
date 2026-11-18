package fengcalen.ui.screen

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import fengcalen.data.entity.EventReminderEntity
import fengcalen.util.NokiaSoundHelper
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateEditReminderDialog(
    initialDate: LocalDate,
    initialIsLunar: Boolean = true,
    initialLunarDay: Int = 1,
    initialLunarMonth: Int = 1,
    reminderToEdit: EventReminderEntity? = null,
    onDismiss: () -> Unit,
    onSave: (EventReminderEntity) -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(reminderToEdit?.title ?: "") }
    var eventType by remember { mutableStateOf(reminderToEdit?.eventType ?: "MEMORIAL") }
    var isLunar by remember {
        mutableStateOf(reminderToEdit?.isLunar ?: initialIsLunar)
    }

    var selectedDay by remember {
        mutableIntStateOf(
            reminderToEdit?.day ?: if (isLunar) initialLunarDay else initialDate.dayOfMonth
        )
    }
    var selectedMonth by remember {
        mutableIntStateOf(
            reminderToEdit?.month ?: if (isLunar) initialLunarMonth else initialDate.monthValue
        )
    }

    // Lặp lại: YEARLY (hàng năm), MONTHLY (hàng tháng), ONCE (chỉ 1 lần)
    var repeatType by remember { mutableStateOf(reminderToEdit?.repeatType ?: "YEARLY") }

    // Thông báo trước: "0"=đúng ngày, "1"=1 ngày, "3"=3 ngày, "7"=1 tuần, "10"=10 ngày
    var selectedDaysBefore by remember {
        val initialSet = reminderToEdit?.remindDaysBefore?.split(",")?.mapNotNull { it.trim().toIntOrNull() }?.toSet()
            ?: setOf(0, 7, 10)
        mutableStateOf(initialSet)
    }

    // Giờ và phút nhận thông báo theo ý muốn
    val initialTimeParts = (reminderToEdit?.remindTime ?: "08:00").split(":")
    var selectedHour by remember { mutableIntStateOf(initialTimeParts.getOrNull(0)?.toIntOrNull() ?: 8) }
    var selectedMinute by remember { mutableIntStateOf(initialTimeParts.getOrNull(1)?.toIntOrNull() ?: 0) }

    val formattedTime = remember(selectedHour, selectedMinute) {
        String.format("%02d:%02d", selectedHour, selectedMinute)
    }

    var selectedRingtone by remember { mutableStateOf(reminderToEdit?.ringtone ?: "nokia_tune") }
    var note by remember { mutableStateOf(reminderToEdit?.note ?: "") }

    var isPlayingPreview by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            NokiaSoundHelper.stopPreview()
        }
    }

    Dialog(
        onDismissRequest = {
            NokiaSoundHelper.stopPreview()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.94f)
                .padding(vertical = 10.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (reminderToEdit == null) "Tạo Nhắc Nhở Sự Kiện" else "Chỉnh Sửa Nhắc Nhở",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = {
                        NokiaSoundHelper.stopPreview()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Title input
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Tên sự kiện / Đối tượng") },
                        placeholder = { Text("Ví dụ: Sinh nhật mẹ, Ngày giỗ Cụ Cố...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Event, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    )

                    // 2. Event Type Chips
                    Text(
                        text = "Loại sự kiện:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val types = listOf(
                            Triple("MEMORIAL", "🕯️ Ngày giỗ", Color(0xFFE11D48)),
                            Triple("BIRTHDAY", "🎂 Sinh nhật", Color(0xFF2563EB)),
                            Triple("ANNIVERSARY", "💍 Kỷ niệm", Color(0xFFD97706)),
                            Triple("OTHER", "📌 Khác", Color(0xFF059669))
                        )
                        types.forEach { (typeId, label, _) ->
                            val isSelected = eventType == typeId
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    eventType = typeId
                                    if (typeId == "MEMORIAL") isLunar = true
                                },
                                label = { Text(label, fontSize = 12.sp) }
                            )
                        }
                    }

                    // 3. Calendar System Toggle (Lịch Âm vs Lịch Dương)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Hệ thống lịch áp dụng:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isLunar) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .clickable { isLunar = true }
                                        .padding(vertical = 10.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.NightsStay,
                                        contentDescription = null,
                                        tint = if (isLunar) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "🌙 Âm Lịch (Giỗ/Lễ)",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLunar) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (!isLunar) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .clickable { isLunar = false }
                                        .padding(vertical = 10.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.WbSunny,
                                        contentDescription = null,
                                        tint = if (!isLunar) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "☀️ Dương Lịch",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!isLunar) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // 4. Date Selectors (Day and Month)
                    Text(
                        text = "Ngày diễn ra (${if (isLunar) "Âm lịch" else "Dương lịch"}):",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Day Selector
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { if (selectedDay > 1) selectedDay-- },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Giảm")
                                }
                                Text(
                                    text = "Ngày $selectedDay",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                IconButton(
                                    onClick = { if (selectedDay < 31) selectedDay++ },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Tăng")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Month Selector
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { if (selectedMonth > 1) selectedMonth-- },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Giảm")
                                }
                                Text(
                                    text = "Tháng $selectedMonth",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                IconButton(
                                    onClick = { if (selectedMonth < 12) selectedMonth++ },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Tăng")
                                }
                            }
                        }
                    }

                    // 5. Chu kỳ lặp lại (MỚI THEO YÊU CẦU: Lặp lại hàng năm rõ ràng)
                    Text(
                        text = "Chu kỳ lặp lại:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val repeatOptions = listOf(
                            Triple("YEARLY", "🔄 Lặp lại hàng năm", "Tự động nhắc lại vào ngày này mỗi năm (Khuyên dùng cho Sinh nhật, Giỗ)"),
                            Triple("MONTHLY", "🔁 Lặp lại hàng tháng", "Tự động nhắc lại vào ngày này mỗi tháng"),
                            Triple("ONCE", "⏹️ Chỉ một lần", "Chỉ thông báo một lần trong năm nay, không lặp lại")
                        )

                        repeatOptions.forEach { (typeId, label, desc) ->
                            val isSelected = repeatType == typeId
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { repeatType = typeId },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { repeatType = typeId }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = desc,
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 6. Notification Timing (Thông báo trước)
                    Text(
                        text = "Thời gian thông báo trước:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val timingOptions = listOf(
                            Pair(10, "Trước 10 ngày"),
                            Pair(7, "Trước 1 tuần (7 ngày)"),
                            Pair(3, "Trước 3 ngày"),
                            Pair(1, "Trước 1 ngày"),
                            Pair(0, "Đúng ngày")
                        )
                        timingOptions.forEach { (days, label) ->
                            val isSelected = days in selectedDaysBefore
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedDaysBefore = if (isSelected) {
                                        if (selectedDaysBefore.size > 1) selectedDaysBefore - days else selectedDaysBefore
                                    } else {
                                        selectedDaysBefore + days
                                    }
                                },
                                label = { Text(label, fontSize = 12.5.sp) },
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            )
                        }
                    }

                    // 7. Giờ nhận thông báo (NÂNG CẤP MẠNH MẼ: Chọn giờ theo ý muốn)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Giờ nhận thông báo:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        // Nút mở TimePickerDialog chuẩn của hệ thống
                        FilledTonalButton(
                            onClick = {
                                TimePickerDialog(
                                    context,
                                    { _, h, m ->
                                        selectedHour = h
                                        selectedMinute = m
                                    },
                                    selectedHour,
                                    selectedMinute,
                                    true // 24h format
                                ).show()
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mở đồng hồ", fontSize = 12.sp)
                        }
                    }

                    // Khung chỉnh giờ và phút trực tiếp
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Hiển thị giờ lớn
                            Text(
                                text = formattedTime,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Bộ tăng giảm Giờ
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Giờ (0 - 23)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        FilledIconButton(
                                            onClick = {
                                                selectedHour = if (selectedHour > 0) selectedHour - 1 else 23
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Giảm giờ")
                                        }
                                        Text(
                                            text = String.format("%02d", selectedHour),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(horizontal = 12.dp)
                                        )
                                        FilledIconButton(
                                            onClick = {
                                                selectedHour = if (selectedHour < 23) selectedHour + 1 else 0
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Tăng giờ")
                                        }
                                    }
                                }

                                VerticalDivider(modifier = Modifier.height(44.dp))

                                // Bộ tăng giảm Phút
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Phút (0 - 59)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        FilledIconButton(
                                            onClick = {
                                                selectedMinute = if (selectedMinute >= 5) selectedMinute - 5 else (60 - (5 - selectedMinute))
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Giảm phút")
                                        }
                                        Text(
                                            text = String.format("%02d", selectedMinute),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(horizontal = 12.dp)
                                        )
                                        FilledIconButton(
                                            onClick = {
                                                selectedMinute = (selectedMinute + 5) % 60
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Tăng phút")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Các mốc chọn nhanh giờ
                    Text("Các mốc gợi ý nhanh:", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val timeOptions = listOf("06:00", "07:00", "08:00", "09:00", "11:30", "18:00", "20:00")
                        timeOptions.forEach { t ->
                            val parts = t.split(":")
                            val h = parts[0].toInt()
                            val m = parts[1].toInt()
                            val isSelected = selectedHour == h && selectedMinute == m

                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedHour = h
                                    selectedMinute = m
                                },
                                label = { Text(t, fontSize = 12.sp) }
                            )
                        }
                    }

                    // 8. Đề xuất nhạc báo Nokia
                    Text(
                        text = "Đề xuất nhạc báo Nokia:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NokiaSoundHelper.TONES.forEach { tone ->
                            val isSelected = selectedRingtone == tone.id
                            val isPlayingThis = isPlayingPreview && NokiaSoundHelper.currentlyPlayingId == tone.id

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedRingtone = tone.id },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedRingtone = tone.id }
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = tone.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = tone.subtitle,
                                                fontSize = 11.5.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    if (tone.resId != 0) {
                                        IconButton(
                                            onClick = {
                                                if (isPlayingThis) {
                                                    NokiaSoundHelper.stopPreview()
                                                    isPlayingPreview = false
                                                } else {
                                                    isPlayingPreview = true
                                                    NokiaSoundHelper.playPreview(context, tone.id) {
                                                        isPlayingPreview = false
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                if (isPlayingThis) Icons.Default.StopCircle else Icons.Default.PlayCircle,
                                                contentDescription = "Nghe thử",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 9. Optional Notes
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Ghi chú thêm (tùy chọn)") },
                        placeholder = { Text("Chuẩn bị mâm cỗ, hoa quả, quà tặng...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 10. Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            NokiaSoundHelper.stopPreview()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Hủy")
                    }

                    Button(
                        onClick = {
                            if (title.isBlank()) return@Button
                            NokiaSoundHelper.stopPreview()
                            val reminder = EventReminderEntity(
                                id = reminderToEdit?.id ?: 0L,
                                title = title.trim(),
                                eventType = eventType,
                                isLunar = isLunar,
                                day = selectedDay,
                                month = selectedMonth,
                                repeatType = repeatType,
                                remindDaysBefore = selectedDaysBefore.sorted().joinToString(","),
                                remindTime = formattedTime,
                                ringtone = selectedRingtone,
                                note = note.trim(),
                                isEnabled = true
                            )
                            onSave(reminder)
                        },
                        enabled = title.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Lưu nhắc nhở")
                    }
                }
            }
        }
    }
}
