package com.example.agrismart.data

/**
 * Data model for a crop recommendation.
 */
data class Crop(
    val name: String,
    val description: String,
    val season: String,
    val soilType: String,
    val icon: String // Placeholder for an icon name or emoji
)
