package com.example.agrismart.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.agrismart.R
import com.example.agrismart.data.CropRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AdvisoryScreen(
    navController: NavController,
    soilType: String
) {
    val cropRepository = CropRepository()
    val recommendedCrops = cropRepository.recommendCrops(soilType)
    
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    val tabs = listOf("Crops", "Fertilizers")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu_advisory), fontWeight = FontWeight.Bold) },
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
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(text = title, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> CropAdvisoryPage(soilType, recommendedCrops)
                    1 -> FertilizerAdvisoryPage(soilType)
                }
            }
        }
    }
}

@Composable
fun CropAdvisoryPage(soilType: String, crops: List<com.example.agrismart.data.Crop>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SmartAIAdviceCard(
                title = "Crop Strategy",
                advice = getSmartCropAdvice(soilType)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(crops) { crop ->
            InfoCard(
                title = crop.name,
                description = crop.description,
                icon = crop.icon
            )
        }
    }
}

@Composable
fun FertilizerAdvisoryPage(soilType: String) {
    val fertilizers = getFertilizerData(soilType)
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SmartAIAdviceCard(
                title = "Nutrient Strategy",
                advice = getSmartFertilizerAdvice(soilType)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(fertilizers) { fert ->
            InfoCard(
                title = fert.name,
                description = fert.usage,
                icon = "🧪",
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun SmartAIAdviceCard(title: String, advice: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(text = advice, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// --- Updated Smart AI Logic ---

fun getSmartCropAdvice(soilType: String): String = when (soilType) {
    "Sandy" -> "Sandy soil drains quickly. Focus on drought-resistant crops and mulching to retain moisture."
    "Clayey" -> "Clayey soil holds water. Ensure proper drainage to avoid root rot during rains."
    "Loamy" -> "Perfect balance! Loamy soil supports most crops. Maintain organic matter for best yield."
    "Black" -> "Black soil (Regur) is moisture-retentive and rich in lime. Excellent for Cotton and deep-rooted crops."
    "Silty" -> "Silty soil is fertile but can pack down easily. Avoid over-tilling to maintain soil structure."
    else -> "Analyze your $soilType soil structure before planting deep-rooted crops."
}

fun getSmartFertilizerAdvice(soilType: String): String = when (soilType) {
    "Sandy" -> "High leaching risk. Apply fertilizers in small, frequent doses rather than all at once."
    "Clayey" -> "Low penetration. Use liquid fertilizers or mix thoroughly with the topsoil."
    "Loamy" -> "Nutrient retention is good. Use a balanced N-P-K (20-20-20) for steady growth."
    "Black" -> "Usually rich in Nitrogen. Focus on Phosphatic fertilizers to boost yield."
    "Silty" -> "Naturally fertile. Use organic compost to prevent the soil from becoming too compact."
    else -> "Conduct a soil pH test to determine the best fertilizer mix for $soilType soil."
}

data class Fertilizer(val name: String, val usage: String)

fun getFertilizerData(soilType: String): List<Fertilizer> = when (soilType) {
    "Sandy" -> listOf(
        Fertilizer("Potash", "Helps in water retention and stress tolerance."),
        Fertilizer("Organic Manure", "Essential to build soil structure in sandy areas."),
        Fertilizer("Zinc Sulphate", "Corrects micro-nutrient deficiency common in sandy soils.")
    )
    "Clayey" -> listOf(
        Fertilizer("Gypsum", "Helps break down heavy clay and improves drainage."),
        Fertilizer("Ammonium Sulphate", "Better nitrogen source for water-logged clayey soils."),
        Fertilizer("DAP", "Provides essential phosphorus for deep root penetration.")
    )
    "Black" -> listOf(
        Fertilizer("Super Phosphate", "Crucial for cotton growth in black soil."),
        Fertilizer("Magnesium", "Prevents leaf reddening in cotton."),
        Fertilizer("Urea", "Supplements nitrogen during the flowering stage.")
    )
    "Silty" -> listOf(
        Fertilizer("Balanced NPK", "Ideal for the mixed texture of silty soil."),
        Fertilizer("Calcium", "Maintains soil cell structure and prevents compaction."),
        Fertilizer("Bio-fertilizers", "Boosts microbial activity in fertile silt.")
    )
    else -> listOf(
        Fertilizer("Urea (Nitrogen)", "General growth promoter."),
        Fertilizer("DAP", "Root developer."),
        Fertilizer("Organic Compost", "Best for overall soil health.")
    )
}
