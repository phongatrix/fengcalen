# PART 02 — Base UI (Theme, MainActivity, Navigation, Skeleton Screens)

## Mục tiêu

Tạo:
- Theme Material3, colors, typography
- `MainActivity` đăng ký app component
- Navigation graph với các tab chính
- Skeleton screens: Home, Calendar, MoonPhase, FengShui, CaDao, Language, Occasions, Settings

## Thực hiện

```bash
cd /home/ibm/rea-analyze
mkdir -p app/src/main/res/values
mkdir -p app/src/main/res/xml
mkdir -p app/src/main/res/mipmap-hdpi
mkdir -p app/src/main/res/mipmap-mdpi
mkdir -p app/src/main/res/mipmap-xhdpi
mkdir -p app/src/main/res/mipmap-xxhdpi
mkdir -p app/src/main/res/mipmap-xxxhdpi
```

## Tạo res/values/colors.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="primary">#1A1A1A</color>
    <color name="primary_variant">#2D2D2D</color>
    <color name="secondary">#C8102E</color>
    <color name="background">#F5F5F5</color>
    <color name="surface">#FFFFFF</color>
    <color name="error">#C8102E</color>
    <color name="text_primary">#222222</color>
    <color name="text_secondary">#666666</color>
    <color name="accent_lunar">#2A6F8F</color>
    <color name="accent_feng">#C8102E</color>
    <color name="accent_info">#2A9D8F</color>
</resources>
```

## Tạo res/values/themes.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.FengCalendar" parent="Theme.Material3.Light.NoActionBar">
        <item name="colorPrimary">@color/primary</item>
        <item name="colorPrimaryVariant">@color/primary_variant</item>
        <item name="colorSecondary">@color/secondary</item>
        <item name="android:statusBarColor">@color/primary</item>
        <item name="android:navigationBarColor">@color/background</item>
        <item name="android:windowBackground">@color/background</item>
    </style>
</resources>
```

## Tạo res/values/strings.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Lịch Âm - Feng Calen</string>
    <string name="menu_home">Trang chủ</string>
    <string name="menu_calendar">Lịch</string>
    <string name="menu_moon">Mặt Trăng</string>
    <string name="menu_feng">Phong Thủy</string>
    <string name="menu_cadao">Ca Dao</string>
    <string name="menu_language">Ngôn Ngữ</string>
    <string name="menu_occasions">Sự Kiện</string>
    <string name="menu_settings">Cài Đặt</string>
    <string name="lunar">Âm lịch</string>
    <string name="solar">Dương lịch</string>
    <string name="feng_shui">Phong Thủy</string>
    <string name="moon_phase">Giai đoạn Mặt Trăng</string>
    <string name="tz">Thời Gian</string>
    <string name="settings_language">Ngôn Ngữ</string>
    <string name="settings_theme">Chủ đề</string>
    <string name="settings_language_vi">Tiếng Việt</string>
    <string name="settings_language_en">English</string>
</resources>
```

## Tạo res/values/attrs.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <attr name="lunarDateColor" format="color" />
    <attr name="fengShuiColor" format="color" />
</resources>
```

## Tạo res/xml/file_paths.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <files-path
        name="files"
        path="." />
</paths>
```

## Tạo res/mipmap-hdpi/ic_launcher.png

(Hợp nhất: 1024x1024, nền bạc, đường tròn, các ký tự "FC" màu đỏ máu)

## Tạo app/src/main/java/fengcalen/FengCalendarApp.kt

```kotlin
package fengcalen

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FengCalendarApp : Application()
```

## Tạo app/src/main/java/fengcalen/ui/theme/Theme.kt

```kotlin
package fengcalen.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val LightColorScheme = lightColorScheme(
    primary = primary,
    onPrimary = contentPrimary,
    primaryContainer = primaryVariant,
    onPrimaryContainer = contentPrimary,
    secondary = secondary,
    onSecondary = contentSecondary,
    secondaryContainer = secondaryVariant,
    onSecondaryContainer = contentSecondary,
    background = background,
    onBackground = textPrimary,
    surface = surface,
    onSurface = textPrimary,
    error = error,
    onError = contentPrimary,
    errorContainer = errorVariant,
    onErrorContainer = contentPrimary,
    outline = outline,
)

private val DarkColorScheme = darkColorScheme(
    primary = primary,
    onPrimary = contentPrimary,
    primaryContainer = primaryVariant,
    onPrimaryContainer = contentPrimary,
    secondary = secondary,
    onSecondary = contentSecondary,
    secondaryContainer = secondaryVariant,
    onSecondaryContainer = contentSecondary,
    background = background,
    onBackground = textPrimary,
    surface = surface,
    onSurface = textPrimary,
    error = error,
    onError = contentPrimary,
    errorContainer = errorVariant,
    onErrorContainer = contentPrimary,
    outline = outline,
)

@Composable
fun FengCalendarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

## Tạo app/src/main/java/fengcalen/ui/theme/Type.kt

```kotlin
package fengcalen.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
        color = textPrimary
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
        color = textPrimary
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        color = textPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = textSecondary
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        color = textPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = textSecondary
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = textPrimary
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp,
        color = textSecondary
    ),
)
```

## Tạo app/src/main/java/fengcalen/ui/MainActivity.kt

```kotlin
package fengcalen.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fengcalen.ui.screen.CaDaoScreen
import fengcalen.ui.screen.CalendarScreen
import fengcalen.ui.screen.FengShuiScreen
import fengcalen.ui.screen.HomeScreen
import fengcalen.ui.screen.LanguageScreen
import fengcalen.ui.screen.MoonPhaseScreen
import fengcalen.ui.screen.OccasionsScreen
import fengcalen.ui.screen.SettingsScreen
import fengcalen.ui.theme.FengCalendarTheme
import fengcalen.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FengCalendarTheme {
                val navController = rememberNavController()
                val viewModel: MainViewModel = hiltViewModel()
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("home") { HomeScreen(navController = navController) }
                        composable("calendar") { CalendarScreen(navController = navController) }
                        composable("moon") { MoonPhaseScreen() }
                        composable("feng") { FengShuiScreen() }
                        composable("cadao") { CaDaoScreen() }
                        composable("language") { LanguageScreen() }
                        composable("occasions") { OccasionsScreen() }
                        composable("settings") { SettingsScreen() }
                    }
                }
            }
        }
    }
}
```

## Kiểm tra Part 02

Sau khi chạy, kiểm tra:
- [ ] Có `Theme.kt` và `Type.kt`
- [ ] `MainActivity.kt` tồn tại
- [ ] `FengCalendarApp.kt` tồn tại
- [ ] `strings.xml`, `colors.xml`, `themes.xml` có nội dung
- [ ] `file_paths.xml` có
- [ ] Thư mục `res/values` không rỗng

## Lệnh kiểm tra

```bash
cd /home/ibm/rea-analyze
echo "=== Kiểm tra Part 02 ==="
ls app/src/main/java/fengcalen/ui/ 2>/dev/null
ls app/src/main/java/fengcalen/ui/theme/ 2>/dev/null
ls app/src/main/java/fengcalen/ui/screen/ 2>/dev/null
ls app/src/main/res/values/ 2>/dev/null
```
