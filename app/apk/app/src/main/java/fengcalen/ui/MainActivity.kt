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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
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
        val targetRoute = intent?.getStringExtra("route")

        setContent {
            val isDarkPref by viewModel.isDarkTheme.collectAsState()
            val useDark = isDarkPref ?: isSystemInDarkTheme()

            FengCalendarTheme(darkTheme = useDark) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                LaunchedEffect(targetRoute) {
                    if (targetRoute == "reminders") {
                        navController.navigate(Screen.Reminders.route)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val bottomNavRoutes = listOf(
                            Screen.Home.route,
                            Screen.Calendar.route,
                            Screen.Reminders.route,
                            Screen.Settings.route
                        )
                        if (currentRoute in bottomNavRoutes) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Home.route,
                                    onClick = {
                                        if (currentRoute != Screen.Home.route) {
                                            navController.navigate(Screen.Home.route) {
                                                popUpTo(Screen.Home.route) { inclusive = true }
                                            }
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Trang chủ") },
                                    label = { Text("Hôm nay") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Calendar.route,
                                    onClick = {
                                        if (currentRoute != Screen.Calendar.route) {
                                            navController.navigate(Screen.Calendar.route)
                                        }
                                    },
                                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Lịch") },
                                    label = { Text("Lịch") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Reminders.route,
                                    onClick = {
                                        if (currentRoute != Screen.Reminders.route) {
                                            navController.navigate(Screen.Reminders.route)
                                        }
                                    },
                                    icon = { Icon(Icons.Default.NotificationsActive, contentDescription = "Nhắc nhở") },
                                    label = { Text("Nhắc Nhở") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Settings.route,
                                    onClick = {
                                        if (currentRoute != Screen.Settings.route) {
                                            navController.navigate(Screen.Settings.route)
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = "Cài đặt") },
                                    label = { Text("Cài Đặt") }
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
                        composable(Screen.Reminders.route) {
                            RemindersScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(Screen.Moon.route) {
                            MoonPhaseScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(Screen.FengShui.route) {
                            FengShuiScreen(navController = navController, viewModel = viewModel)
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
