package com.example.agrismart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agrismart.data.UserPreferencesManager
import com.example.agrismart.data.User
import com.example.agrismart.navigation.Screen
import com.example.agrismart.ui.*
import com.example.agrismart.ui.theme.AgriSmartTheme
import com.example.agrismart.utils.NotificationHelper
import kotlinx.coroutines.launch

/**
 * Main Activity of the application.
 * Handles the Navigation Host and State Management for user data.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            AgriSmartTheme {
                AgriSmartApp()
            }
        }
    }
}

@Composable
fun AgriSmartApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val userPreferencesManager = remember { UserPreferencesManager(context) }
    val notificationHelper = remember { NotificationHelper(context) }
    val scope = rememberCoroutineScope()

    // Observe the user flow from DataStore
    val userState by userPreferencesManager.userFlow.collectAsState(initial = User())

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                navController = navController,
                isLoggedIn = userState.isLoggedIn
            )
        }

        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                onSaveUser = { user ->
                    scope.launch { userPreferencesManager.saveUser(user) }
                }
            )
        }

        // Dashboard Screen
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                navController = navController,
                user = userState,
                onShowNotification = {
                    notificationHelper.showBasicNotification(
                        "Crop Advisory Reminder",
                        "Time to check your irrigation for ${userState.soilType} soil."
                    )
                }
            )
        }

        // Advisory Screen
        composable(Screen.Advisory.route) {
            AdvisoryScreen(
                navController = navController,
                soilType = userState.soilType
            )
        }

        // Profile Screen
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                user = userState,
                onLogout = {
                    scope.launch { userPreferencesManager.clearUser() }
                }
            )
        }
    }
}
