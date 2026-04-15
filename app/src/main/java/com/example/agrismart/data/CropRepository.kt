package com.example.agrismart.data

/**
 * Simple rule-based repository for crop recommendations.
 */
class CropRepository {

    private val allCrops = listOf(
        // Clayey Soil
        Crop("Rice", "High water requirement, ideal for monsoon.", "Monsoon", "Clayey", "🍚"),
        Crop("Mustard", "Oilseed crop for winter season.", "Winter", "Clayey", "🌱"),
        Crop("Soybean", "Grows well in clayey and loamy soils.", "Monsoon", "Clayey", "🫛"),
        Crop("Sugarcane", "Requires heavy clayey soil and high water.", "All Season", "Clayey", "🎋"),
        
        // Loamy Soil
        Crop("Wheat", "Grown in cool climate, requires moderate water.", "Winter", "Loamy", "🌾"),
        Crop("Maize", "Versatile crop, suitable for various soils.", "Summer", "Loamy", "🌽"),
        Crop("Tomato", "High yield in well-drained loamy soil.", "Summer", "Loamy", "🍅"),
        Crop("Cucumber", "Grows fast in warm, loamy soil.", "Summer", "Loamy", "🥒"),
        
        // Sandy Soil
        Crop("Sorghum", "Drought resistant, good for dry areas.", "Summer", "Sandy", "🌾"),
        Crop("Gram", "Pulse crop, nitrogen fixing.", "Winter", "Sandy", "🫘"),
        Crop("Groundnut", "Suitable for well-drained sandy soil.", "Summer", "Sandy", "🥜"),
        Crop("Watermelon", "Thrives in warm, sandy soil.", "Summer", "Sandy", "🍉"),
        
        // Black Soil (Regur)
        Crop("Cotton", "Thrives in black soil with moderate rainfall.", "Summer", "Black", "☁️"),
        Crop("Linseed", "Fiber and oilseed crop for black soil.", "Winter", "Black", "🌿"),
        Crop("Sunflower", "Oilseed that grows well in deep black soil.", "Summer", "Black", "🌻"),
        Crop("Chilli", "Grown widely in black soil areas.", "Summer", "Black", "🌶️"),

        // Silty Soil
        Crop("Tobacco", "Prefers the fine texture of silty soil.", "Winter", "Silty", "🍂"),
        Crop("Cabbage", "Requires moisture-retentive silty soil.", "Winter", "Silty", "🥬"),
        Crop("Carrot", "Grows straight in soft silty/sandy soil.", "Winter", "Silty", "🥕"),
        Crop("Barley", "Adaptable, but performs well in silty soil.", "Winter", "Silty", "🌾")
    )

    /**
     * Logic to recommend crops based on soil type.
     */
    fun recommendCrops(soilType: String): List<Crop> {
        // Filter crops strictly by soil type to show distinct results
        val filtered = allCrops.filter { it.soilType.contains(soilType, ignoreCase = true) }
        
        // If no specific crops found for the type, return loamy as it is the most versatile
        return if (filtered.isNotEmpty()) filtered else allCrops.filter { it.soilType == "Loamy" }
    }

    fun getAllCrops(): List<Crop> = allCrops
}
