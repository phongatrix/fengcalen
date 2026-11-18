package fengcalen.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fengcalen.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FengShuiScreen(navController: NavController, viewModel: MainViewModel) {
    val fengShui by viewModel.fengShui.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tín Hiệu Phong Thủy") },
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
            // Main Element & Direction Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "NGŨ HÀNH & NĂNG LƯỢNG",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bản Mệnh: Hành ${fengShui?.element ?: "--"}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Khí vận: ${fengShui?.yinYang ?: ""} • ${fengShui?.energy ?: ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Hướng xuất hành cát lợi: ${fengShui?.direction ?: "--"}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Màu may mắn: ${fengShui?.luckyColor ?: "--"} • Con số: ${fengShui?.luckyNumber ?: "--"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Good deeds & Taboos
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Việc Nên Làm", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    fengShui?.luckyAspects?.forEach { deed ->
                        Text(text = "• $deed", style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Việc Cần Kiêng Kỵ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    fengShui?.taboos?.forEach { taboo ->
                        Text(text = "• $taboo", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Lời khuyên phong thủy:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = fengShui?.fortuneSummary ?: "Bình hòa an khang.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
