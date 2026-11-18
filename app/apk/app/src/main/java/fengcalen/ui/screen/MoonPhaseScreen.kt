package fengcalen.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoonPhaseScreen(navController: NavController, viewModel: MainViewModel) {
    val moonPhase by viewModel.moonPhase.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giai Đoạn Mặt Trăng") },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Moon Visual Representation
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.secondary.copy(
                                    alpha = ((moonPhase?.illumination ?: 50f) / 100f).coerceIn(0.2f, 1f)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${moonPhase?.illumination?.toInt() ?: 0}%",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = moonPhase?.phaseNameVi ?: "Đang tính toán...",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = moonPhase?.phase ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Metrics details
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricRow(label = "Tuổi trăng:", value = "${moonPhase?.ageDays ?: 0} ngày")
                    Divider()
                    MetricRow(label = "Chòm sao chiếu:", value = moonPhase?.constellation ?: "--")
                    Divider()
                    MetricRow(label = "Năng lượng vũ trụ:", value = moonPhase?.energy ?: "--")
                    Divider()
                    MetricRow(
                        label = "Thời điểm cát lợi:",
                        value = if (moonPhase?.recommended == true) "Rất thích hợp cầu an, khởi sự" else "Nên tịnh dưỡng, tĩnh tâm"
                    )
                }
            }

            // Keyword tags
            moonPhase?.keywords?.takeIf { it.isNotEmpty() }?.let { tags ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { tag ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(tag) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}
