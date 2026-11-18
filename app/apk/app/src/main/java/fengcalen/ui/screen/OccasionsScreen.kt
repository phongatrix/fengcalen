package fengcalen.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import fengcalen.data.model.Occasion
import fengcalen.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OccasionsScreen(navController: NavController, viewModel: MainViewModel) {
    val occasions by viewModel.occasions.collectAsState()
    val activeVanKhan by viewModel.activeVanKhan.collectAsState()
    var selectedOccasion by remember { mutableStateOf<Occasion?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("48 Lễ Tiết & Sự Kiện Cổ Truyền") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Trở về")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(occasions) { occasion ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedOccasion = occasion },
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
                            Text(
                                text = occasion.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                                Text(text = occasion.date)
                            }
                        }
                        if (occasion.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = occasion.description,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 3
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { viewModel.openVanKhan(occasion.id) }) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Xem Văn Khấn")
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog for Reading Van Khan
    activeVanKhan?.let { vanKhanText ->
        Dialog(onDismissRequest = { viewModel.closeVanKhan() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Văn Khấn Cổ Truyền",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = vanKhanText,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.closeVanKhan() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Đóng")
                    }
                }
            }
        }
    }
}
