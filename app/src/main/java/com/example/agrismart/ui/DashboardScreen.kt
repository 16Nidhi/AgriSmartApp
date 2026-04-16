package com.example.agrismart.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.agrismart.R
import com.example.agrismart.data.User
import com.example.agrismart.data.CropRepository
import com.example.agrismart.navigation.Screen
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    user: User,
    onShowNotification: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val cropRepository = CropRepository()
    val recommendedCrops = cropRepository.recommendCrops(user.soilType)

    val menuItems = listOf(
        Screen.Dashboard to stringResource(R.string.menu_home),
        Screen.FarmSetup to "My Digital Farm",
        Screen.Advisory to stringResource(R.string.menu_advisory),
        Screen.Market to stringResource(R.string.menu_market),
        Screen.Weather to stringResource(R.string.menu_weather),
        Screen.DiseaseScan to "Pest Detection",
        Screen.Profile to stringResource(R.string.menu_profile),
        Screen.Settings to stringResource(R.string.menu_settings),
        Screen.Feedback to "Feedback"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            ) {
                Spacer(Modifier.height(24.dp))
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = user.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                menuItems.forEach { (screen, title) ->
                    NavigationDrawerItem(
                        label = { Text(text = title, fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (screen.route != Screen.Dashboard.route) {
                                navController.navigate(screen.route)
                            }
                        },
                        icon = { screen.icon?.let { Icon(it, contentDescription = null) } },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.app_name),
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { navController.navigate(Screen.Profile.route)}
                        ) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = "Profile"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding)
                    .background(Color(0xFFFBFDFA))
            ) {

                item {
                    val hasSetup = user.landSize > 0.0
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(20.dp)
                            .shadow(12.dp, RoundedCornerShape(32.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = if (hasSetup) 
                                        listOf(Color(0xFF1B5E20), Color(0xFF4CAF50)) 
                                        else listOf(Color(0xFF37474F), Color(0xFF78909C))
                                )
                            )
                            .clickable { if (!hasSetup) navController.navigate(Screen.FarmSetup.route) }
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (hasSetup) "${user.currentCrop} Insights" else "Configure Your Farm",
                                        color = Color.White,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Icon(
                                    if (hasSetup) Icons.Default.Agriculture else Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Text(
                                text = if (hasSetup) calculateSmartInsight(user) else "Connect your land data to get precision AI-powered advisory.",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = 24.sp
                            )
                            
                            if (hasSetup) {
                                Spacer(modifier = Modifier.height(24.dp))

                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.2f)))
                                    Box(
                                        modifier = Modifier.fillMaxWidth(0.35f).height(8.dp)
                                            .clip(CircleShape).background(Color.White))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Vegetative Stage",
                                        color = Color.White.copy(alpha = 0.8f),
                                        style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        text = "35%", color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            text = "Smart Crop Discovery",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                        
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(recommendedCrops) { crop ->
                                CropDiscoveryCard(crop.name, crop.description)
                            }
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Precision Farm Tools",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            color = Color.White,
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ToolIcon(Icons.Default.ShoppingCart, "Market") { navController.navigate(Screen.Market.route) }
                                ToolIcon(Icons.Default.CameraAlt, "Scan") { navController.navigate(Screen.DiseaseScan.route) }
                                ToolIcon(Icons.Default.WbCloudy, "Weather") { navController.navigate(Screen.Weather.route) }
                                ToolIcon(Icons.Default.Feedback, "Help") { navController.navigate(Screen.Feedback.route) }
                            }
                        }
                    }
                }

                // 4. Activity Progress (Timeline)
                if (user.landSize > 0.0) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                            Text(
                                text = "Live Farm Feed",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            GrowthTimeline(user.currentCrop)
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

@Composable
fun CropDiscoveryCard(name: String, description: String) {
    Surface(
        modifier = Modifier.width(240.dp).height(150.dp),
        shape = RoundedCornerShape(32.dp),
        color = Color.White,
        shadowElevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF1F8E9)),
                contentAlignment = Alignment.Center
            ) {
                Text("🌿", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = name, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 2)
        }
    }
}

@Composable
fun ToolIcon(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold, color = Color.DarkGray)
    }
}

fun calculateSmartInsight(user: User): String {
    val ureaPerHectare = when (user.currentCrop) {
        "Wheat" -> 2.5
        "Rice" -> 3.0
        "Tomato" -> 1.8
        "Cotton" -> 2.2
        else -> 2.0
    }
    val totalUrea = String.format(Locale.getDefault(), "%.1f", ureaPerHectare * user.landSize)
    return "Based on your ${user.landSize} hectares, AI suggests applying $totalUrea bags of Nitrogen fertilizer this week."
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GrowthTimeline(currentCrop: String) {
    val stages = listOf(
        "Sowing" to "Completed",
        "Vegetative" to "Active Now",
        "Flowering" to "Coming Soon",
        "Harvest" to "April 2025"
    )
    val pagerState = rememberPagerState(pageCount = { stages.size })

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(end = 80.dp)
    ) { page ->
        val stage = stages[page]
        Card(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = if (page == 1) MaterialTheme.colorScheme.primaryContainer else Color.White),
            border = if (page == 1) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (page == 1) MaterialTheme.colorScheme.primary else Color.Gray))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = stage.first, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleSmall)
                    Text(text = stage.second, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}
