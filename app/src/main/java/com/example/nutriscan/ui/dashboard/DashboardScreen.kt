package com.example.nutriscan.ui.dashboard

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriscan.data.model.DailySummary
import com.example.nutriscan.data.model.MealEntry
import com.example.nutriscan.data.model.MealType
import com.example.nutriscan.data.model.NutritionInfo
import com.example.nutriscan.theme.*

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember { com.example.nutriscan.data.local.AppDatabase.getDatabase(context) }
    val todayDateString = remember { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()) }
    val dbMeals by db.mealDao().getMealsForDate(todayDateString).collectAsState(initial = emptyList())
    
    val converters = remember { com.example.nutriscan.data.local.Converters() }
    val realMeals = dbMeals.map { entity ->
        entity.toMealEntry(converters.toNutritionInfoList(entity.itemsJson))
    }

    val repo = remember { com.example.nutriscan.data.repository.ProfileRepository(context) }
    
    val dailySummary = DailySummary(
        date = todayDateString,
        meals = realMeals,
        waterGlasses = repo.getWaterCountForToday(),
        targetCalories = 2000f,
    )

    var waterCount by remember { mutableIntStateOf(dailySummary.waterGlasses) }

    LaunchedEffect(waterCount) {
        repo.saveWaterCountForToday(waterCount)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(24.dp), // Increased padding for softer look
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Greeting Header
        item {
            GreetingHeader()
        }

        // ==========================================
        // HOMEWORK: DAILY MOTIVATION CARD
        // ==========================================
        item {
            DailyMotivationCard()
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
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        // Meal cards
        items(realMeals.size) { index ->
            MealCard(realMeals[index])
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
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Track your nutrition journey",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ==========================================
// USER HOMEWORK: FILL THIS IN!
// ==========================================
@Composable
fun DailyMotivationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "💡 Daily Motivation",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\"Every healthy choice is a step towards a better you. Keep going!\"",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun CalorieProgressCard(summary: DailySummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(), // Smooth expansion
        shape = RoundedCornerShape(32.dp), // Softer, pill-like corners
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Flat design
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Daily Calories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Animated calorie ring with Spring physics
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(220.dp),
            ) {
                val progress by animateFloatAsState(
                    targetValue = summary.calorieProgress,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "calorie_progress",
                )

                val primaryColor = MaterialTheme.colorScheme.primary
                
                Canvas(modifier = Modifier.size(220.dp)) {
                    val strokeWidth = 24.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Background ring (Soft)
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.1f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )

                    // Progress arc (Solid Terracotta, no gradients)
                    val sweepAngle = 360f * progress.coerceAtMost(1f)
                    drawArc(
                        color = primaryColor,
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
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "/ ${summary.targetCalories.toInt()} kcal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val remaining = (summary.targetCalories - summary.totalCalories).toInt()
            Text(
                text = if (remaining > 0) "$remaining kcal remaining" else "Target reached! 🎉",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun MacroBreakdownCard(summary: DailySummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Macro Nutrients",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                MacroItem("Protein", summary.totalProtein, "g", MacroProtein, targetG = 60f)
                MacroItem("Carbs", summary.totalCarbs, "g", MacroCarbs, targetG = 250f)
                MacroItem("Fat", summary.totalFat, "g", MacroFat, targetG = 65f)
                MacroItem("Fiber", summary.totalFiber, "g", MacroFiber, targetG = 30f)
            }
        }
    }
}

@Composable
private fun MacroItem(name: String, amount: Float, unit: String, color: Color, targetG: Float) {
    val progress by animateFloatAsState(
        targetValue = (amount / targetG).coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "macro_$name",
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
            Canvas(modifier = Modifier.size(64.dp)) {
                val strokeWidth = 8.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2

                drawCircle(
                    color = color.copy(alpha = 0.15f),
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
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun WaterTrackerCard(count: Int, onCountChange: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
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
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(WaterCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Rounded.LocalDrink,
                    contentDescription = "Water",
                    tint = WaterCyan,
                    modifier = Modifier.size(32.dp),
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Water Intake",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    "$count / 8 glasses",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Minus button
            FilledIconButton(
                onClick = { if (count > 0) onCountChange(count - 1) },
                modifier = Modifier.size(40.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Icon(Icons.Rounded.Remove, contentDescription = "Remove", modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                "$count",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = WaterCyan,
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Plus button
            FilledIconButton(
                onClick = { onCountChange(count + 1) },
                modifier = Modifier.size(40.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = WaterCyan,
                    contentColor = Color.White,
                ),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun MealCard(meal: MealEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // slightly smaller radius for lists
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Meal type emoji container
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = meal.mealType.emoji,
                    fontSize = 28.sp,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.mealType.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = meal.items.joinToString(", ") { it.foodName },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${meal.totalCalories.toInt()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "kcal",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
