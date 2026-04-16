package com.example.agrismart.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.agrismart.data.User
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmInsightsScreen(navController: NavController, user: User) {
    val ureaPerHectare = when (user.currentCrop) {
        "Wheat" -> 2.5
        "Rice" -> 3.0
        "Tomato" -> 1.8
        "Cotton" -> 2.2
        else -> 2.0
    }
    
    val waterHoursPerHectare = when (user.soilType) {
        "Sandy" -> 4.0
        "Clayey" -> 2.0
        "Loamy" -> 3.0
        else -> 3.5
    }

    val totalUrea = String.format(Locale.getDefault(), "%.1f", ureaPerHectare * user.landSize)
    val totalWater = String.format(Locale.getDefault(), "%.1f", waterHoursPerHectare * user.landSize)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Analysis Report", fontWeight = FontWeight.Bold) },
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
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Precision Farming Report",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${user.landSize} Hectares of ${user.currentCrop}",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Resource Requirements",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Fertilizer Insight
            InsightDetailCard(
                title = "Nitrogen Fertilizer (Urea)",
                value = "$totalUrea Bags",
                description = "Optimal requirement for the current growth stage in ${user.soilType} soil.",
                icon = Icons.Default.Science,
                color = Color(0xFFE8F5E9)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Water Insight
            InsightDetailCard(
                title = "Irrigation Needed",
                value = "$totalWater Hours",
                description = "Based on current evaporation rates and soil moisture retention.",
                icon = Icons.Default.Opacity,
                color = Color(0xFFE3F2FD)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Additional Advice
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Check weather alerts before applying fertilizer to avoid nutrient leaching.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            PrimaryButton(
                text = "Acknowledge Report",
                onClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun InsightDetailCard(
    title: String,
    value: String,
    description: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.DarkGray)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
        }
    }
}
