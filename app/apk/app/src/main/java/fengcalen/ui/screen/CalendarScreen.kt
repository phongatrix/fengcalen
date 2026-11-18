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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
import fengcalen.ui.viewmodel.MainViewModel
import fengcalen.util.LunarCalendar
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController, viewModel: MainViewModel) {
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    val selectedDate by viewModel.selectedDate.collectAsState()
    val todayLunar by viewModel.todayLunar.collectAsState()

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch Tháng Vạn Niên") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Trở về")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
            val gridHeight = (totalWeeks * 50).dp + 16.dp

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
                                    .height(46.dp)
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

                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .fillMaxWidth()
                                    .height(46.dp)
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
                        Text(
                            text = "Chi tiết ngày đã chọn: ${lunar.solarDate}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
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
        }
    }
}
