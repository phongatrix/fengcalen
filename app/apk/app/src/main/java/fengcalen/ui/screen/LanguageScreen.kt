package fengcalen.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
fun LanguageScreen(navController: NavController, viewModel: MainViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ngôn Ngữ & Vùng Miền") },
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
            // Language Selection
            Text(
                text = "Ngôn ngữ giao diện",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    LanguageOption(
                        title = "Tiếng Việt",
                        selected = currentLang == "vi",
                        onClick = { viewModel.setLanguage("vi") }
                    )
                    Divider()
                    LanguageOption(
                        title = "English (International)",
                        selected = currentLang == "en",
                        onClick = { viewModel.setLanguage("en") }
                    )
                }
            }

            // Dialects of Vietnam (Bắc, Trung, Nam)
            Text(
                text = "Từ Điển Phương Ngữ Ba Miền",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "• Miền Bắc: Hà Nội, Hải Phòng, Quảng Ninh, Phú Thọ...", fontWeight = FontWeight.SemiBold)
                    Text(text = "• Miền Trung: Huế, Đà Nẵng, Quảng Nam, Nghệ An, Hà Tĩnh...", fontWeight = FontWeight.SemiBold)
                    Text(text = "• Miền Nam: Sài Gòn, Cần Thơ, An Giang, Đồng Tháp...", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tự động điều chỉnh thuật ngữ âm lịch, phong tục và lời chúc tương thích với 63 tỉnh thành Việt Nam.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageOption(title: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        if (selected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}
