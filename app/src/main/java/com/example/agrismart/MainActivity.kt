package com.example.agrismart

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgriSmartTheme {
                val context = LocalContext.current
                val userPreferencesManager = remember { UserPreferencesManager(context) }
                
                val userState by userPreferencesManager.userFlow.collectAsState(initial = null)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val user = userState
                    if (user == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    } else {
                        val localizedContext = remember(user.language) {
                            val locale = Locale(user.language)
                            Locale.setDefault(locale)
                            val config = Configuration(context.resources.configuration)
                            config.setLocale(locale)
                            context.createConfigurationContext(config)
                        }

                        CompositionLocalProvider(LocalContext provides localizedContext) {
                            AgriSmartApp(userPreferencesManager, user)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AgriSmartApp(userPreferencesManager: UserPreferencesManager, initialUser: User) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val notificationHelper = remember { NotificationHelper(context) }
    val scope = rememberCoroutineScope()
    
    val userState by userPreferencesManager.userFlow.collectAsState(initial = initialUser)

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController, isLoggedIn = userState.isLoggedIn)
        }

        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                onSaveUser = { user ->
                    scope.launch { userPreferencesManager.saveUser(user) }
                }
            )
        }

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

        composable(Screen.Advisory.route) {
            AdvisoryScreen(navController = navController, soilType = userState.soilType)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                user = userState,
                onLogout = {
                    scope.launch { userPreferencesManager.clearUser() }
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                navController = navController,
                user = userState,
                onLanguageChange = { lang ->
                    scope.launch { 
                        userPreferencesManager.saveUser(userState.copy(language = lang))
                    }
                }
            )
        }

        composable(Screen.Weather.route) {
            WeatherScreen(navController = navController, user = userState)
        }

        composable(Screen.Market.route) {
            MarketScreen(navController = navController)
        }

        composable(Screen.DiseaseScan.route) {
            DiseaseScanScreen(navController = navController)
        }

        composable(Screen.Feedback.route) {
            FeedbackScreen(navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(navController: NavController, title: String, message: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "🚜", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Go Back")
            }
        }
    }
}
