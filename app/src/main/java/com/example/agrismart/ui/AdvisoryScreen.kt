package com.example.agrismart.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.agrismart.data.CropRepository

/**
 * Advisory Screen for the app.
 * Lists all crop suggestions based on the user's soil type.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvisoryScreen(
    navController: NavController,
    soilType: String
) {
    val cropRepository = CropRepository()
    val recommendedCrops = cropRepository.recommendCrops(soilType)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crop Advisory") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding).padding(16.dp)
        ) {
            Text(
                text = "Based on your $soilType soil",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(recommendedCrops) { crop ->
                    InfoCard(
                        title = crop.name,
                        description = crop.description,
                        icon = crop.icon
                    )
                }
            }
        }
    }
}
