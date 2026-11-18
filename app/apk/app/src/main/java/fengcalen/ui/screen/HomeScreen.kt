package fengcalen.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fengcalen.ui.navigation.Screen
import fengcalen.ui.viewmodel.MainViewModel
import java.time.format.DateTimeFormatter
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import fengcalen.widget.LunarWidgetProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel) {
    val lunarDate by viewModel.todayLunar.collectAsState()
    val moonPhase by viewModel.moonPhase.collectAsState()
    val caDaoList by viewModel.caDaoList.collectAsState()
    val randomCaDao = remember(caDaoList) { caDaoList.shuffled().firstOrNull() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lịch Âm - Feng Calen",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    val context = LocalContext.current
                    IconButton(onClick = {
                        val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                        val provider = ComponentName(context, LunarWidgetProvider::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                            appWidgetManager != null &&
                            appWidgetManager.isRequestPinAppWidgetSupported
                        ) {
                            appWidgetManager.requestPinAppWidget(provider, null, null)
                        } else {
                            Toast.makeText(
                                context,
                                "Chạm giữ màn hình chính điện thoại -> Chọn 'Tiện ích / Widgets' -> Chọn 'Lịch Âm - Feng Calen'",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }) {
                        Icon(Icons.Default.Widgets, contentDescription = "Thêm Widget Desktop")
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Cài đặt")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
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
            // Main Lunar & Solar Date Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "DƯƠNG LỊCH: ${lunarDate?.solarDate ?: "--"}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = lunarDate?.day ?: "--",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Tháng ${lunarDate?.month ?: "--"} Âm Lịch",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        InfoColumn(label = "Năm", value = lunarDate?.canChiYear ?: "--")
                        InfoColumn(label = "Tháng", value = lunarDate?.canChiMonth ?: "--")
                        InfoColumn(label = "Ngày", value = lunarDate?.canChiDay ?: "--")
                    }
                }
            }

            // Zodiac & Element Badge
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Con Giáp: ${lunarDate?.zodiac ?: "--"}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Hành ${lunarDate?.element ?: "--"} • ${moonPhase?.phaseNameVi ?: ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${moonPhase?.illumination?.toInt() ?: 0}%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Auspicious Hours
            lunarDate?.auspiciousHours?.takeIf { it.isNotEmpty() }?.let { hours ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Giờ Hoàng Đạo trong ngày",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = hours.joinToString(" • "),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Daily Folk Verse (Ca Dao)
            randomCaDao?.let { caDao ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Screen.CaDao.route) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "VĂN HÓA DÂN GIAN",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "\"${caDao.content}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                        if (caDao.title.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "— ${caDao.title}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Quick Access Grid
            Text(
                text = "Khám phá chuyên mục",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Lịch Tháng",
                    icon = Icons.Default.CalendarMonth,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Calendar.route) }
                )
                QuickActionCard(
                    title = "Trăng Sao",
                    icon = Icons.Default.NightsStay,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Moon.route) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Phong Thủy",
                    icon = Icons.Default.Explore,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.FengShui.route) }
                )
                QuickActionCard(
                    title = "Sự Kiện & Lễ",
                    icon = Icons.Default.Celebration,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Occasions.route) }
                )
            }
        }
    }
}

@Composable
fun InfoColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(96.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
