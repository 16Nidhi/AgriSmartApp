package com.example.agrismart.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.SquareFoot
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmSetupScreen(
    navController: NavController,
    user: User,
    onSaveFarmDetails: (Double, String, String) -> Unit
) {
    var landSize by remember { mutableStateOf(if (user.landSize > 0) user.landSize.toString() else "") }
    var selectedCrop by remember { mutableStateOf(user.currentCrop) }
    var sowingDate by remember { mutableStateOf(user.sowingDate) }
    
    // Modern Date Picker State (Syllabus: Unit III)
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val crops = listOf("Wheat", "Rice", "Tomato", "Cotton", "Maize", "Soybean")

    // Modern Material 3 Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        sowingDate = formatter.format(date)
                    }
                    showDatePicker = false
                }) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Digital Farm", fontWeight = FontWeight.Bold) },
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
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Agriculture, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Configure your Farm", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = "Tell us about your land to get precise calculations.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = landSize,
                onValueChange = { landSize = it },
                label = { Text("Land Size (in Hectares)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.SquareFoot, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Select your current Crop", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            crops.chunked(2).forEach { rowCrops ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowCrops.forEach { crop ->
                        FilterChip(
                            selected = selectedCrop == crop,
                            onClick = { selectedCrop = crop },
                            label = { Text(crop) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Interactive Date Selection Field
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = sowingDate,
                    onValueChange = { },
                    label = { Text("Sowing Date") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    placeholder = { Text("Pick a date") }
                )
                // Clickable overlay to trigger the Modern DatePicker
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            PrimaryButton(
                text = "Calculate & Save",
                onClick = {
                    val size = landSize.toDoubleOrNull() ?: 0.0
                    onSaveFarmDetails(size, selectedCrop, sowingDate)
                    navController.popBackStack()
                },
                enabled = landSize.isNotEmpty() && selectedCrop != "None" && sowingDate.isNotEmpty()
            )
        }
    }
}
