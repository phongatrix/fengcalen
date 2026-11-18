package fengcalen.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.Add
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import fengcalen.widget.LunarWidgetProvider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fengcalen.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: MainViewModel) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    var notificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài Đặt Ứng Dụng") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Trở về")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
            // Theme settings
            Text(text = "Giao diện", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ThemeOptionRow(title = "Theo hệ thống", selected = isDarkTheme == null) {
                        viewModel.setDarkMode(null)
                    }
                    Divider()
                    ThemeOptionRow(title = "Chế độ sáng (Light)", selected = isDarkTheme == false) {
                        viewModel.setDarkMode(false)
                    }
                    Divider()
                    ThemeOptionRow(title = "Chế độ tối (Dark)", selected = isDarkTheme == true) {
                        viewModel.setDarkMode(true)
                    }
                }
            }

            // Notification Settings
            Text(text = "Thông báo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Nhắc nhở ngày Rằm & Mùng 1", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Text(text = "Tự động nhắc nhở trước ngày sóc, vọng", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }
            }

            // Desktop Widget Section
            val context = LocalContext.current
            Text(
                text = "Tiện ích Desktop (Home Widget)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Widget Lịch Âm Góc Nhỏ",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Hiển thị ngày âm, ngày dương, can chi & giờ hoàng đạo ngay ngoài màn hình chính điện thoại.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = {
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
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ghim Widget ra Màn hình chính")
                    }
                }
            }

            // About App
            Text(text = "Thông tin", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Lịch Âm - Feng Calen", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "Gói ứng dụng: fengcalen", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Phiên bản: 1.0.0 (Release)", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Nhà phát triển: Hong", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "Ứng dụng tích hợp lịch âm, giai đoạn mặt trăng, phong thủy học đường, 48 lễ tiết và kho tàng văn học dân gian Việt Nam.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeOptionRow(title: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        RadioButton(selected = selected, onClick = onClick)
    }
}
