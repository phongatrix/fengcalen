package fengcalen.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import fengcalen.ui.navigation.Screen
import fengcalen.ui.screen.*
import fengcalen.ui.theme.FengCalendarTheme
import fengcalen.ui.viewmodel.MainViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        try {
            fengcalen.widget.LunarWidgetProvider.updateAllWidgets(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkPref by viewModel.isDarkTheme.collectAsState()
            val useDark = isDarkPref ?: isSystemInDarkTheme()

            FengCalendarTheme(darkTheme = useDark) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val bottomNavRoutes = listOf(
                            Screen.Home.route,
                            Screen.Calendar.route,
                            Screen.CaDao.route,
                            Screen.Occasions.route
                        )
                        if (currentRoute in bottomNavRoutes) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Home.route,
                                    onClick = { navController.navigate(Screen.Home.route) },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Trang chủ") },
                                    label = { Text("Hôm nay") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Calendar.route,
                                    onClick = { navController.navigate(Screen.Calendar.route) },
                                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Lịch") },
                                    label = { Text("Lịch") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.CaDao.route,
                                    onClick = { navController.navigate(Screen.CaDao.route) },
                                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Ca Dao") },
                                    label = { Text("Văn Học") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Occasions.route,
                                    onClick = { navController.navigate(Screen.Occasions.route) },
                                    icon = { Icon(Icons.Default.Celebration, contentDescription = "Sự Kiện") },
                                    label = { Text("Sự Kiện") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(Screen.Calendar.route) {
                            CalendarScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(Screen.Moon.route) {
                            MoonPhaseScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(Screen.FengShui.route) {
                            FengShuiScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(Screen.CaDao.route) {
                            CaDaoScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(Screen.Occasions.route) {
                            OccasionsScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(Screen.Language.route) {
                            LanguageScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(navController = navController, viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
