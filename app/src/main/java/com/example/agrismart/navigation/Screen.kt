package com.example.agrismart.navigation

/**
 * Defines the navigation routes for the application.
 * Using a sealed class ensures type safety for our routes.
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Login : Screen("login_screen")
    object Dashboard : Screen("dashboard_screen")
    object Advisory : Screen("advisory_screen")
    object Profile : Screen("profile_screen")
}
