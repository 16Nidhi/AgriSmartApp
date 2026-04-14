package com.example.agrismart.data

/**
 * Data model for the user profile.
 * Added language support for SIH Multilingual requirement.
 */
data class User(
    val name: String = "",
    val soilType: String = "Loamy",
    val location: String = "",
    val isLoggedIn: Boolean = false,
    val language: String = "en" // "en" for English, "hi" for Hindi
)
