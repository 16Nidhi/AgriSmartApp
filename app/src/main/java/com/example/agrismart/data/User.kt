package com.example.agrismart.data

/**
 * Data model for the user profile.
 * Stored using Preferences DataStore.
 */
data class User(
    val name: String = "",
    val soilType: String = "Loamy",
    val location: String = "",
    val isLoggedIn: Boolean = false
)
