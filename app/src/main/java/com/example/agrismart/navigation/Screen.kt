package com.example.agrismart.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Defines the navigation routes for the application.
 */
sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object Splash : Screen("splash_screen")
    object Login : Screen("login_screen")
    object Dashboard : Screen("dashboard_screen", "Home", Icons.Default.Home)
    object Advisory : Screen("advisory_screen", "Advisory", Icons.Default.Info)
    object Market : Screen("market_screen", "Market Prices", Icons.Default.ShoppingCart)
    object Weather : Screen("weather_screen", "Weather Update", Icons.Default.LocationOn)
    object DiseaseScan : Screen("disease_scan_screen", "Pest Detection", Icons.Default.CameraAlt)
    object Profile : Screen("profile_screen", "My Profile", Icons.Default.Person)
    object Settings : Screen("settings_screen", "Settings", Icons.Default.Settings)
    object Feedback : Screen("feedback_screen", "Feedback", Icons.Default.Feedback)
}
