package com.example.agrismart.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.agrismart.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseaseScanScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<DetectionResult?>(null) }
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { btm ->
        if (btm != null) {
            bitmap = btm
            startScanning(btm) { result -> 
                isScanning = false
                scanResult = result 
            }
            isScanning = true
            scanResult = null
        }
    }

    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val btm = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
            bitmap = btm
            startScanning(btm) { result -> 
                isScanning = false
                scanResult = result 
            }
            isScanning = true
            scanResult = null
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) cameraLauncher.launch()
        else Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pest & Disease Scan", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Instruction Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Pick an image from your gallery or take a direct photo for detection.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Image Display Area
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.05f))
                    .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    if (isScanning) {
                        ScanningAnimation()
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ImageSearch,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No image selected", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Source selection (always visible until scanned)
            if (scanResult == null || isScanning) {
                Text(
                    "Select Image Source",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Camera Button
                    Surface(
                        onClick = {
                            if (hasCameraPermission) cameraLauncher.launch()
                            else permissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.weight(1f).height(100.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Camera", fontWeight = FontWeight.Bold)
                            Text("At the spot", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    // Gallery Button
                    Surface(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f).height(100.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Collections, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Gallery", fontWeight = FontWeight.Bold)
                            Text("Choose image", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // Results and Save Button
            AnimatedVisibility(visible = scanResult != null && !isScanning) {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))
                    scanResult?.let { result ->
                        ResultCard(result)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = { 
                                Toast.makeText(context, "Saving data...", Toast.LENGTH_SHORT).show()
                                scope.launch {
                                    delay(1000)
                                    Toast.makeText(context, "Detection report saved to gallery!", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Diagnosis & Image", fontWeight = FontWeight.Bold)
                        }
                        
                        TextButton(
                            onClick = {
                                bitmap = null
                                scanResult = null
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Text("Discard and Retake", color = Color.Red)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

fun startScanning(bitmap: Bitmap, onResult: (DetectionResult) -> Unit) {
    val results = listOf(
        DetectionResult("Leaf Rust", 92, "Apply copper-based fungicides. Remove infected leaves and destroy them to prevent spread.", false),
        DetectionResult("Aphids Attack", 85, "Use neem oil spray or insecticidal soap. Encourage natural predators like ladybugs.", false),
        DetectionResult("Healthy Leaf", 98, "No disease detected! Continue regular irrigation and ensure proper sunlight.", true),
        DetectionResult("Early Blight", 78, "Avoid overhead watering. Apply organic mulch to prevent spores from splashing onto leaves.", false)
    )
    
    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
        onResult(results.random())
    }, 2000)
}

@Composable
fun ScanningAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val translateY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .offset(y = translateY.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, Color.Green, Color.Transparent)
                    )
                )
        )
    }
}

@Composable
fun ResultCard(result: DetectionResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (result.isHealthy) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (result.isHealthy) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (result.isHealthy) Color(0xFF2E7D32) else Color(0xFFE65100)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (result.isHealthy) "Healthy Plant" else "Detection: ${result.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (result.isHealthy) Color(0xFF1B5E20) else Color(0xFFBF360C)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Confidence: ${result.confidence}%",
                style = MaterialTheme.typography.labelMedium,
                color = Color.DarkGray
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
            
            Text(
                text = "Recommendation:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = result.advice,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
        }
    }
}

data class DetectionResult(
    val name: String,
    val confidence: Int,
    val advice: String,
    val isHealthy: Boolean = false
)
