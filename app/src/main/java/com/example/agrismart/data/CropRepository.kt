package com.example.agrismart.data

/**
 * Simple rule-based repository for crop recommendations.
 */
class CropRepository {

    private val allCrops = listOf(
        Crop("Rice", "High water requirement, ideal for monsoon.", "Monsoon", "Clayey", "🍚"),
        Crop("Wheat", "Grown in cool climate, requires moderate water.", "Winter", "Loamy", "🌾"),
        Crop("Cotton", "Thrives in black soil with moderate rainfall.", "Summer", "Black", "☁️"),
        Crop("Maize", "Versatile crop, suitable for various soils.", "Summer", "Loamy", "🌽"),
        Crop("Sorghum", "Drought resistant, good for dry areas.", "Summer", "Sandy", "🌾"),
        Crop("Gram", "Pulse crop, nitrogen fixing.", "Winter", "Sandy Loamy", "🫘"),
        Crop("Tomato", "High yield in well-drained loamy soil.", "Summer", "Loamy", "🍅"),
        Crop("Mustard", "Oilseed crop for winter season.", "Winter", "Clayey", "🌱")
    )

    /**
     * Logic to recommend crops based on soil type.
     */
    fun recommendCrops(soilType: String): List<Crop> {
        // Simple logic: Filter crops that match the soil type
        // For simplicity, we also return some "general" crops
        return allCrops.filter { it.soilType.contains(soilType, ignoreCase = true) || it.soilType == "Loamy" }
    }

    fun getAllCrops(): List<Crop> = allCrops
}
