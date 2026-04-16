package com.example.agrismart.data

/**
 * Enhanced User Model for Precision Farming.
 * Added landSize, currentCrop, and sowingDate for SIH unique features.
 */
data class User(
    val name: String = "",
    val soilType: String = "Loamy",
    val location: String = "",
    val isLoggedIn: Boolean = false,
    val language: String = "en",
    val landSize: Double = 0.0, // in Hectares
    val currentCrop: String = "None",
    val sowingDate: String = "" // Format: YYYY-MM-DD
)
