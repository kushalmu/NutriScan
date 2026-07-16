package com.example.nutriscan.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriscan.data.model.DailySummary
import com.example.nutriscan.data.model.MealEntry
import com.example.nutriscan.data.model.MealType
import com.example.nutriscan.data.model.NutritionInfo
import com.example.nutriscan.theme.*

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    // Sample data for demonstration — will be replaced with real data later
    val sampleMeals = listOf(
        MealEntry(
            id = "1",
            mealType = MealType.BREAKFAST,
            items = listOf(
                NutritionInfo("Idli (3 pcs)", "ಇಡ್ಲಿ", 150f, 195f, 5.4f, 39f, 0.6f, 1.8f, 0.3f, 280f),
                NutritionInfo("Sambar", "ಸಾಂಬಾರ್", 150f, 90f, 4.2f, 12f, 2.5f, 3.0f, 2.1f, 450f),
            ),
        ),
        MealEntry(
            id = "2",
            mealType = MealType.LUNCH,
            items = listOf(
                NutritionInfo("Rice", "ಅಕ್ಕಿ", 200f, 260f, 4.8f, 56f, 0.4f, 0.6f, 0.1f, 2f),
                NutritionInfo("Bisi Bele Bath", "ಬಿಸಿ ಬೇಳೆ ಬಾತ್", 250f, 320f, 9.5f, 48f, 8.2f, 5.4f, 3.2f, 520f),
            ),
        ),
    )

    val dailySummary = DailySummary(
        date = "2026-07-16",
        meals = sampleMeals,
        waterGlasses = 5,
        targetCalories = 2000f,
    )

    var waterCount by remember { mutableIntStateOf(dailySummary.waterGlasses) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Greeting Header
        item {
            GreetingHeader()
        }

        // Calorie Progress Ring
        item {
            CalorieProgressCard(dailySummary)
        }

        // Macro Breakdown
        item {
            MacroBreakdownCard(dailySummary)
        }

        // Water Tracker
        item {
            WaterTrackerCard(waterCount) { newCount -> waterCount = newCount }
        }

        // Today's Meals
        item {
            Text(
                "Today's Meals",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        // Meal cards
        items(sampleMeals.size) { index ->
            MealCard(sampleMeals[index])
        }

        // Bottom spacer for navigation bar
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun GreetingHeader() {
    Column {
        Text(
            text = "Good Evening! 👋",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Track your nutrition journey",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CalorieProgressCard(summary: DailySummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Daily Calories",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Animated calorie ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp),
            ) {
                val progress by animateFloatAsState(
                    targetValue = summary.calorieProgress,
                    animationSpec = tween(1000),
                    label = "calorie_progress",
                )

                Canvas(modifier = Modifier.size(200.dp)) {
                    val strokeWidth = 20.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Background ring
                    drawCircle(
                        color = Color.White.copy(alpha = 0.3f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )

                    // Progress arc
                    val sweepAngle = 360f * progress.coerceAtMost(1f)
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(NutriGreenLight, NutriGreen, NutriGreenDark),
                        ),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )
                }

                // Center text
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${summary.totalCalories.toInt()}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text = "/ ${summary.targetCalories.toInt()} kcal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val remaining = (summary.targetCalories - summary.totalCalories).toInt()
            Text(
                text = if (remaining > 0) "$remaining kcal remaining" else "Target reached! 🎉",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun MacroBreakdownCard(summary: DailySummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Macro Nutrients",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                MacroItem("Protein", summary.totalProtein, "g", ProteinBlue, targetG = 60f)
                MacroItem("Carbs", summary.totalCarbs, "g", CarbsAmber, targetG = 250f)
                MacroItem("Fat", summary.totalFat, "g", FatPurple, targetG = 65f)
                MacroItem("Fiber", summary.totalFiber, "g", FiberGreen, targetG = 30f)
            }
        }
    }
}

@Composable
private fun MacroItem(name: String, amount: Float, unit: String, color: Color, targetG: Float) {
    val progress by animateFloatAsState(
        targetValue = (amount / targetG).coerceIn(0f, 1f),
        animationSpec = tween(800),
        label = "macro_$name",
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(56.dp)) {
            Canvas(modifier = Modifier.size(56.dp)) {
                val strokeWidth = 6.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2

                drawCircle(
                    color = color.copy(alpha = 0.2f),
                    radius = radius,
                    style = Stroke(width = strokeWidth),
                )

                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                    size = Size(size.width - strokeWidth, size.height - strokeWidth),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
            }

            Text(
                text = "${amount.toInt()}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "$unit",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun WaterTrackerCard(count: Int, onCountChange: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Water icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(WaterCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Rounded.LocalDrink,
                    contentDescription = "Water",
                    tint = WaterCyan,
                    modifier = Modifier.size(28.dp),
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Water Intake",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "$count / 8 glasses",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Minus button
            FilledIconButton(
                onClick = { if (count > 0) onCountChange(count - 1) },
                modifier = Modifier.size(36.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Icon(Icons.Rounded.Remove, contentDescription = "Remove", modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                "$count",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = WaterCyan,
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Plus button
            FilledIconButton(
                onClick = { onCountChange(count + 1) },
                modifier = Modifier.size(36.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = WaterCyan,
                    contentColor = Color.White,
                ),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun MealCard(meal: MealEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Meal type emoji
            Text(
                text = meal.mealType.emoji,
                fontSize = 32.sp,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.mealType.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = meal.items.joinToString(", ") { it.foodName },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${meal.totalCalories.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NutriGreen,
                )
                Text(
                    text = "kcal",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
