package fengcalen.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Hôm nay")
    object Calendar : Screen("calendar", "Lịch tháng")
    object Moon : Screen("moon", "Mặt trăng")
    object FengShui : Screen("feng", "Phong thủy")
    object CaDao : Screen("cadao", "Ca dao")
    object Occasions : Screen("occasions", "Sự kiện")
    object Language : Screen("language", "Ngôn ngữ")
    object Settings : Screen("settings", "Cài đặt")
}
