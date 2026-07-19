package com.example.nutriscan.ui.scanner

import android.Manifest
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.nutriscan.data.model.NutritionInfo
import com.example.nutriscan.data.network.OfflineScanner
import com.example.nutriscan.theme.*
import com.example.nutriscan.ui.dashboard.ManualEntryDialog
import kotlinx.coroutines.launch

enum class ScannerState {
    IDLE, CAPTURING, ANALYZING, RESULT, ERROR
}

@Composable
fun ScannerScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var hasCameraPermission by remember { mutableStateOf(false) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    var scannerState by remember { mutableStateOf(ScannerState.IDLE) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var scanResult by remember { mutableStateOf<com.example.nutriscan.data.network.FoodPredictionResult?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    
    var showManualEntry by remember { mutableStateOf(false) }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val offlineScanner = remember { OfflineScanner(context) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                
                // Ensure it's mutable/software for MLKit
                val softwareBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                capturedBitmap = softwareBitmap
                scannerState = ScannerState.ANALYZING
                
                coroutineScope.launch {
                    val result = offlineScanner.analyzeFoodImage(softwareBitmap)
                    result.onSuccess { info ->
                        scanResult = info
                        scannerState = ScannerState.RESULT
                    }.onFailure { err ->
                        errorMessage = err.message ?: "Analysis failed"
                        scannerState = ScannerState.ERROR
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load image from gallery."
                scannerState = ScannerState.ERROR
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Scan Your Food",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            "Powered by Offline ML Kit",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Camera / Result Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(listOf(TerracottaLight, TerracottaPrimary)),
                    shape = RoundedCornerShape(32.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = scannerState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                },
                label = "ScannerState"
            ) { state ->
                when (state) {
                    ScannerState.IDLE -> {
                        if (hasCameraPermission) {
                            CameraPreview(
                                onImageCaptured = {},
                                imageCaptureRef = { imageCapture = it }
                            )
                        } else {
                            Text("Camera permission required.")
                        }
                    }
                    ScannerState.CAPTURING, ScannerState.ANALYZING -> {
                        capturedBitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Captured",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = TerracottaPrimary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Analyzing your food...",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    ScannerState.RESULT -> {
                        capturedBitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Captured",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                    }
                    ScannerState.ERROR -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Automatic food recognition is currently being improved.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Button(onClick = { showManualEntry = true }) {
                                    Text("Choose manually")
                                }
                                OutlinedButton(onClick = { scannerState = ScannerState.IDLE }) {
                                    Text("Cancel")
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Result Card OR Action Buttons
        AnimatedContent(targetState = scannerState == ScannerState.RESULT, label = "") { isResult ->
            if (isResult && scanResult != null) {
                val db = remember { com.example.nutriscan.data.local.AppDatabase.getDatabase(context) }
                val prediction = scanResult!!
                
                if (prediction.confidence > 0.5f && prediction.nutritionInfo != null) {
                    // High confidence
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Most likely: ${prediction.predictedName} (${(prediction.confidence * 100).toInt()}% confidence)",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        NutritionResultCard(
                            nutritionInfo = prediction.nutritionInfo,
                            onAccept = {
                                coroutineScope.launch {
                                    try {
                                        val mealEntity = com.example.nutriscan.data.local.MealEntity(
                                            id = java.util.UUID.randomUUID().toString(),
                                            timestamp = System.currentTimeMillis(),
                                            dateString = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
                                            mealType = com.example.nutriscan.data.model.MealType.LUNCH.name, // Hardcoded for now
                                            itemsJson = com.google.gson.Gson().toJson(listOf(prediction.nutritionInfo)),
                                            isAiAnalyzed = true
                                        )
                                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                                            db.mealDao().insertMeal(mealEntity)
                                        }
                                        android.widget.Toast.makeText(context, "Meal saved successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        android.widget.Toast.makeText(context, "Failed to save meal.", android.widget.Toast.LENGTH_SHORT).show()
                                    } finally {
                                        scannerState = ScannerState.IDLE
                                        scanResult = null
                                    }
                                }
                            },
                            onDiscard = {
                                scannerState = ScannerState.IDLE
                                scanResult = null
                            }
                        )
                    }
                } else {
                    // Low confidence or missing nutrition info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Not confident enough to identify this food.",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text("Top alternative predictions:", fontWeight = FontWeight.Bold)
                            val allPredictions = listOf(prediction.predictedName to prediction.confidence) + prediction.alternatives
                            allPredictions.take(3).forEach { (altName, altConf) ->
                                Text("- $altName (${(altConf * 100).toInt()}%)")
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                OutlinedButton(onClick = { 
                                    scannerState = ScannerState.IDLE
                                    scanResult = null
                                }) {
                                    Text("Try another image")
                                }
                                Button(onClick = { 
                                    showManualEntry = true
                                    scannerState = ScannerState.IDLE
                                    scanResult = null
                                }) {
                                    Text("Choose manually")
                                }
                            }
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    ScanActionButton(
                        icon = Icons.Rounded.CameraAlt,
                        label = "Scan",
                        color = TerracottaPrimary,
                        onClick = {
                            if (scannerState == ScannerState.IDLE && imageCapture != null) {
                                scannerState = ScannerState.CAPTURING
                                imageCapture?.takePicture(
                                    context,
                                    ContextCompat.getMainExecutor(context),
                                    onImageCaptured = { bitmap ->
                                        capturedBitmap = bitmap
                                        scannerState = ScannerState.ANALYZING
                                        coroutineScope.launch {
                                            val result = offlineScanner.analyzeFoodImage(bitmap)
                                            result.onSuccess { info ->
                                                scanResult = info
                                                scannerState = ScannerState.RESULT
                                            }.onFailure { err ->
                                                errorMessage = err.message ?: "Analysis failed"
                                                scannerState = ScannerState.ERROR
                                            }
                                        }
                                    },
                                    onError = {
                                        errorMessage = "Camera error"
                                        scannerState = ScannerState.ERROR
                                    }
                                )
                            }
                        },
                    )
                    ScanActionButton(
                        icon = Icons.Rounded.Image,
                        label = "Gallery",
                        color = TerracottaLight,
                        onClick = {
                            if (scannerState == ScannerState.IDLE || scannerState == ScannerState.ERROR) {
                                galleryLauncher.launch("image/*")
                            }
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    if (showManualEntry) {
        ManualEntryDialog(onDismiss = {
            showManualEntry = false
            scannerState = ScannerState.IDLE // Reset scanner state after manual entry
        })
    }
}

@Composable
private fun ScanActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = color,
                contentColor = Color.White,
            ),
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun NutritionResultCard(
    nutritionInfo: NutritionInfo,
    onAccept: () -> Unit,
    onDiscard: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                nutritionInfo.foodName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MacroStat("Calories", "${nutritionInfo.calories.toInt()} kcal", Color.DarkGray)
                MacroStat("Protein", "${nutritionInfo.proteinG}g", MacroProtein)
                MacroStat("Carbs", "${nutritionInfo.carbsG}g", MacroCarbs)
                MacroStat("Fat", "${nutritionInfo.fatG}g", MacroFat)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDiscard) {
                    Text("Discard", color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onAccept) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Meal")
                }
            }
        }
    }
}

@Composable
private fun MacroStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
