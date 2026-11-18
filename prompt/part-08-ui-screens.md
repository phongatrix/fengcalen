# PART 08 — UI Screens (Home, Calendar, MoonPhase, FengShui, CaDao, Language, Occasions, Settings)

## Mục tiêu

Tạo 8 màn hình chính:
- HomeScreen — trang chủ với dấu thời gian, sự kiện liên quan
- CalendarScreen — lịch âm, lịch dương, lễ tết
- MoonPhaseScreen — giai đoạn mặt trăng và thông tin liên quan
- FengShuiScreen — tín hiệu phong thủy, element, yin-yang
- CaDaoScreen — danh sách ca dao, câu đối, lời chào
- LanguageScreen — ngôn ngữ, cấu hình
- OccasionsScreen — sự kiện văn hóa
- SettingsScreen — cài đặt chung

## Thực hiện

```bash
mkdir -p app/src/main/java/fengcalen/ui/screen/calendar
mkdir -p app/src/main/java/fengcalen/ui/screen/moon
mkdir -p app/src/main/java/fengcalen/ui/screen/feng
mkdir -p app/src/main/java/fengcalen/ui/screen/cadao
mkdir -p app/src/main/java/fengcalen/ui/screen/language
mkdir -p app/src/main/java/fengcalen/ui/screen/occasions
mkdir -p app/src/main/java/fengcalen/ui/screen/settings
```

## Tạo app/src/main/java/fengcalen/ui/screen/HomeScreen.kt

```kotlin
package fengcalen.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fengcalen.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(navController: NavController) {
    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
    }
    val timeFormatter = remember {
        SimpleDateFormat("HH:mm:ss", Locale("vi", "VN"))
    }

    val currentDate by remember { mutableStateOf(dateFormatter.format(Date())) }
    val currentTime by remember { mutableStateOf(timeFormatter.format(Date())) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(R.string.app_name)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Thoi Gian",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = currentTime,
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Ngay Hien Tai",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = currentDate,
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { navController.navigate("calendar") }) {
                Text("Xem Lịch")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { navController.navigate("moon") }) {
                Text("Giai Doan Mat Truong")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { navController.navigate("feng") }) {
                Text("Phong Thu")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { navController.navigate("cadao") }) {
                Text("Ca Dao")
            }
        }
    }
}
```

## Tạo app/src/main/java/fengcalen/ui/screen/CalendarScreen.kt

```kotlin
package fengcalen.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fengcalen.data.model.LunarDate
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(navController: NavController) {
    val calendar = remember { Calendar.getInstance() }
    val currentLunarDate = remember { LunarCalendar.solarToLunar(dateFormatter.format(Date())) }

    val lunarDateText = remember(currentLunarDate) {
        "${currentLunarDate.day}/${currentLunarDate.month}/${currentLunarDate.year}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lich Am - Lunar Calendar") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding
        ) {
            item {
                Card(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Phan Trang",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = lunarDateText,
                            style = MaterialTheme.typography.displayMedium
                        )
                        Text(
                            text = "Zodiac: ${currentLunarDate.zodiac}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Element: ${currentLunarDate.element}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Moon Phase: ${currentLunarDate.moonPhase}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Feng Shui: ${currentLunarDate.fengShui}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                item {
                    Text("Sự kiện sắp tới", style = MaterialTheme.typography.titleMedium)
                }
                items(R.array.occasions) { occasion ->
                    Card(modifier = Modifier.padding(16.dp)) {
                        Text(text = occasion)
                    }
                }
            }
        }
    }
}
```

## Tạo app/src/main/java/fengcalen/ui/screen/MoonPhaseScreen.kt

```kotlin
package fengcalen.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fengcalen.util.MoonPhaseUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MoonPhaseScreen() {
    val today = LocalDate.now()
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    val moonPhase = remember(today) {
        MoonPhaseUtils.getMoonPhase(today)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giai Doan Mat Truong") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ngay: ${today.format(dateFormatter)}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Giai Doan: ${moonPhase.phase}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Do Chiang: ${String.format("%.1f", moonPhase.illumination)}%",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tinh Te: ${moonPhase.energy}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Mau: ${moonPhase.color}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Dinh Danh: ${String.join(", ", moonPhase.keywords)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            val msg = if (moonPhase.recommended) "Ngay nao co tang am hong!" else "Ket luan"
            Text(text = "$msg", style = MaterialTheme.typography.titleMedium)
        }
    }
}
```

## Tạo app/src/main/java/fengcalen/ui/screen/FengShuiScreen.kt

```kotlin
package fengcalen.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fengcalen.util.LunarCalendar
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FengShuiScreen() {
    val calendar = remember { Calendar.getInstance() }
    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
    }
    val currentLunarDate by remember { mutableStateOf(LunarCalendar.solarToLunar(dateFormatter.format(Date()))) }

    val fengShuiText = remember(currentLunarDate) { currentLunarDate.fengShui }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phong Thu") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Phong Thu Ngay",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = fengShuiText,
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Card(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nguon Am", style = MaterialTheme.typography.titleMedium)
                    Text("Vue Thuong (Beneath)", style = MaterialTheme.typography.bodyMedium)
                    Text("Duong Hang", style = MaterialTheme.typography.bodyMedium)
                    Text("Quyn Luong", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Card(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tinh Te", style = MaterialTheme.typography.titleMedium)
                    Text("Kham Pha", style = MaterialTheme.typography.bodyMedium)
                    Text("Moc Dat", style = MaterialTheme.typography.bodyMedium)
                    Text("Kinh Do", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
```

## Tạo app/src/main/java/fengcalen/ui/screen/CaDaoScreen.kt

```kotlin
package fengcalen.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CaDaoScreen() {
    val caDaoList = listOf(
        "Da den nguoi rong, nang hoa nguyen nhu thien.",
        "Kim ngan dua, cao xem hon lao.",
        "Dan co hong, benh do lam mac.",
        "Nam dua nam, dinh a mui gieng."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ca Dao") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding
        ) {
            items(caDaoList) { item ->
                Card(modifier = Modifier.padding(16.dp)) {
                    Text(text = item, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
```

## Tạo app/src/main/java/fengcalen/ui/screen/LanguageScreen.kt

```kotlin
package fengcalen.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LanguageScreen(navController: NavController) {
    var selectedLang by remember { mutableStateOf("vi") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ngôn Ngữ") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Chon Ngon Ngu")
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { selectedLang = "vi"; navController.navigate("home") },
                    colors = ButtonDefaults.buttonColors(containerColor = if (selectedLang == "vi") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface)
                ) {
                    Text("Tieng Viet")
                }
                Button(
                    onClick = { selectedLang = "en"; navController.navigate("home") },
                    colors = ButtonDefaults.buttonColors(containerColor = if (selectedLang == "en") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface)
                ) {
                    Text("English")
                }
            }
        }
    }
}
```

## Tạo app/src/main/java/fengcalen/ui/screen/OccasionsScreen.kt

```kotlin
package fengcalen.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OccasionsScreen() {
    val occasions = listOf(
        "Tết Nguyên Đán",
        "Rằm 1/1",
        "Rằm 1/2",
        "Rằm 1/3",
        "Tết Dương",
        "Tết Âm",
        "Đoan Ngọ",
        "Trung Thu",
        "Giao Thừa",
        "Giỗ Tổ",
        "Mừng 1/1",
        "Mừng 1/2",
        "Mừng 1/3",
        "Mừng 1/4",
        "Mừng 1/5",
        "Mừng 1/6",
        "Mừng 1/7",
        "Mừng 1/8",
        "Mừng 1/9",
        "Mừng 1/10",
        "Mừng 1/11",
        "Mừng 1/12"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sự Kiện") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding
        ) {
            items(occasions) { item ->
                Card(modifier = Modifier.padding(16.dp)) {
                    Text(text = item, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
```

## Tạo app/src/main/java/fengcalen/ui/screen/SettingsScreen.kt

```kotlin
package fengcalen.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {
    var language by remember { mutableStateOf("vi") }
    var theme by remember { mutableStateOf("dark") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cai Dat") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Ngon Ngu", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RadioButton(selected = language == "vi", onClick = { language = "vi" })
                Text("Tieng Viet")
                RadioButton(selected = language == "en", onClick = { language = "en" })
                Text("English")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Chu De", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RadioButton(selected = theme == "dark", onClick = { theme = "dark" })
                Text("Dark")
                RadioButton(selected = theme == "light", onClick = { theme = "light" })
                Text("Light")
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { navController.navigate("home") }) {
                Text("Tro Ve Trang Chủ")
            }
        }
    }
}
```

## Kiểm tra Part 08

Sau khi chạy, kiểm tra:
- [ ] Cấu trúc thư mục `screen/` đầy đủ
- [ ] Tất cả 8 màn hình tồn tại
- [ ] Mỗi màn hình có `Scaffold` + `TopAppBar` hợp lý

## Lệnh kiểm tra

```bash
cd /home/ibm/rea-analyze
echo "=== Kiểm tra Part 08 ==="
find app/src/main/java/fengcalen/ui/screen/ -name "*.kt" | sort
```
