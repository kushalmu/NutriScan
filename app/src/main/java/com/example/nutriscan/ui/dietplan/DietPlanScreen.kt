package com.example.nutriscan.ui.dietplan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriscan.theme.*

// Sample Mysore food suggestions
private data class MealSuggestion(
    val mealTime: String,
    val emoji: String,
    val foodName: String,
    val calories: Int,
    val description: String,
)

private val sampleDayPlan = listOf(
    MealSuggestion("Breakfast", "🌅", "Ragi Dosa + Coconut Chutney", 280, "High fiber, low GI — great for sustained energy"),
    MealSuggestion("Mid-Morning", "🍎", "Banana + 5 Almonds", 160, "Quick energy with healthy fats"),
    MealSuggestion("Lunch", "☀️", "Bisi Bele Bath + Kosambari", 420, "Complete protein with lentils and veggies"),
    MealSuggestion("Evening", "🫖", "Masala Chai + Thatte Idli (2)", 200, "Light snack, locally popular in Mysore"),
    MealSuggestion("Dinner", "🌙", "Akki Roti + Majjige Huli", 350, "Light, easy to digest, good for sleep"),
)

@Composable
fun DietPlanScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            // Header
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Your Diet Plan",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = NutriOrange,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Text(
                    "Personalized for Mysore • Based on local foods",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Daily summary card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = NutriGreen,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PlanStatItem("Calories", "1,410", "kcal")
                    PlanStatItem("Protein", "48", "g")
                    PlanStatItem("Meals", "5", "today")
                }
            }
        }

        // Today's Plan label
        item {
            Text(
                "Today's Meal Plan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }

        // Meal suggestions
        items(sampleDayPlan.size) { index ->
            MealSuggestionCard(sampleDayPlan[index])
        }

        // AI Generate button
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { /* Phase 4: Gemini diet plan generation */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NutriGreen,
                ),
            ) {
                Icon(
                    Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Generate AI Diet Plan",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                "✨ Powered by Gemini AI • Coming in Phase 4",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }

        // Bottom spacer
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PlanStatItem(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
        Text(
            "$label ($unit)",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f),
        )
    }
}

@Composable
private fun MealSuggestionCard(meal: MealSuggestion) {
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
            verticalAlignment = Alignment.Top,
        ) {
            // Time + emoji
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(meal.emoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    meal.mealTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Food details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    meal.foodName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    meal.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Calories badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(NutriGreenSurface)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    "${meal.calories} kcal",
                    style = MaterialTheme.typography.labelMedium,
                    color = NutriGreen,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
