package com.example.agrismart.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.agrismart.data.User
import com.example.agrismart.navigation.Screen

/**
 * Modern Login Screen for user onboarding.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    onSaveUser: (User) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedSoilType by remember { mutableStateOf("Loamy") }

    val soilTypes = listOf("Loamy", "Clayey", "Sandy", "Black", "Silty")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.background)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🌱",
                fontSize = 80.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Personalize Your Experience",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Tell us about your farm to get better advice",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Name Input
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Farmer's Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Location Input
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location (e.g. Punjab)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Soil Type Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedSoilType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Primary Soil Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            soilTypes.forEach { soilType ->
                                DropdownMenuItem(
                                    text = { Text(text = soilType) },
                                    onClick = {
                                        selectedSoilType = soilType
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Save and Continue Button
            PrimaryButton(
                text = "Generate My Advisory",
                onClick = {
                    if (name.isNotBlank() && location.isNotBlank()) {
                        val user = User(
                            name = name,
                            soilType = selectedSoilType,
                            location = location,
                            isLoggedIn = true
                        )
                        onSaveUser(user)
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                enabled = name.isNotBlank() && location.isNotBlank()
            )
            
            Text(
                text = "We value your privacy. Data stays on device.",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
