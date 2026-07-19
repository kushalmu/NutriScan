package com.example.nutriscan.ui.dietplan

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.FreeBreakfast
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriscan.data.model.ActivityLevel
import com.example.nutriscan.data.model.DietaryPreference
import com.example.nutriscan.data.model.Gender
import com.example.nutriscan.data.model.HealthGoal
import com.example.nutriscan.data.repository.ProfileRepository
import com.example.nutriscan.theme.*

private data class MealSuggestion(
    val time: String,
    val title: String,
    val icon: ImageVector,
    val description: String,
    val macros: String,
    val recommendedTime: String,
    val calories: Int = macros.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
)

private fun getDietPlan(diet: DietaryPreference, goal: HealthGoal): List<MealSuggestion> {
    val isVeg = diet == DietaryPreference.VEG || diet == DietaryPreference.VEGAN
    
    return when (goal) {
        HealthGoal.LOSE -> listOf(
            MealSuggestion("Breakfast", "Oats Idli (2) + Sambar", Icons.Rounded.WbTwilight, "Low calorie, high fiber breakfast to keep you full.", "200 kcal", "08:00 AM"),
            MealSuggestion("Mid-Morning", "Papaya Bowl", Icons.Rounded.Coffee, "Aids digestion and very light on the stomach.", "80 kcal", "11:00 AM"),
            MealSuggestion("Lunch", if (isVeg) "Ragi Mudde + Bassaru" else "Ragi Mudde + Chicken Curry", Icons.Rounded.WbSunny, "Complex carbs for sustained energy without the crash.", if(isVeg) "350 kcal" else "420 kcal", "01:30 PM"),
            MealSuggestion("Evening", "Green Tea + Roasted Makhana", Icons.Rounded.FreeBreakfast, "Low calorie snack to tide you over.", "100 kcal", "05:00 PM"),
            MealSuggestion("Dinner", "Jolada Roti (1) + Palak Dal", Icons.Rounded.Nightlight, "Light and iron-rich for the night.", "250 kcal", "08:00 PM"),
        )
        HealthGoal.GAIN -> listOf(
            MealSuggestion("Breakfast", "Masala Dosa (2) + Coconut Chutney", Icons.Rounded.WbTwilight, "High energy start to the day with good carbs.", "550 kcal", "08:30 AM"),
            MealSuggestion("Mid-Morning", "Banana Milkshake + Almonds", Icons.Rounded.Coffee, "Dense calories and healthy fats for growth.", "320 kcal", "11:30 AM"),
            MealSuggestion("Lunch", if (isVeg) "Bisi Bele Bath + Ghee" else "Chicken Biryani", Icons.Rounded.WbSunny, "Calorie surplus with good macros and protein.", if(isVeg) "550 kcal" else "600 kcal", "01:30 PM"),
            MealSuggestion("Evening", "Filter Coffee + Mangalore Buns", Icons.Rounded.FreeBreakfast, "Classic high-energy snack.", "280 kcal", "05:30 PM"),
            MealSuggestion("Dinner", if (isVeg) "Paneer Butter Masala + Chapati" else "Fish Curry + Rice", Icons.Rounded.Nightlight, "Protein-heavy for overnight muscle repair.", "500 kcal", "08:30 PM"),
        )
        HealthGoal.MAINTAIN -> listOf(
            MealSuggestion("Breakfast", "Ragi Dosa + Coconut Chutney", Icons.Rounded.WbTwilight, "High fiber, low GI — great for sustained energy.", "280 kcal", "08:00 AM"),
            MealSuggestion("Mid-Morning", "Banana + 5 Almonds", Icons.Rounded.Coffee, "Quick energy with healthy fats.", "160 kcal", "11:00 AM"),
            MealSuggestion("Lunch", if (isVeg) "Bisi Bele Bath + Kosambari" else "Rice + Chicken Curry", Icons.Rounded.WbSunny, "Complete protein and balanced carbs.", "420 kcal", "01:30 PM"),
            MealSuggestion("Evening", "Masala Chai + Thatte Idli (1)", Icons.Rounded.FreeBreakfast, "Light snack, locally popular in Mysore.", "180 kcal", "05:00 PM"),
            MealSuggestion("Dinner", "Akki Roti (2) + Majjige Huli", Icons.Rounded.Nightlight, "Light, easy to digest, good for sleep.", "350 kcal", "08:00 PM"),
        )
    }
}

@Composable
fun DietPlanScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val repo = remember { ProfileRepository(context) }
    val db = remember { com.example.nutriscan.data.local.AppDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()
    
    val goal = repo.getHealthGoal()
    val diet = repo.getDietaryPreference()
    val todayDateString = remember { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()) }
    
    val initialPlan = remember(goal, diet) { 
        val basePlan = getDietPlan(diet, goal)
        // Apply saved custom replacements
        basePlan.mapIndexed { index, meal ->
            val replacement = repo.getDietPlanReplacement(todayDateString, index)
            if (replacement != null) {
                meal.copy(
                    title = replacement.first,
                    macros = "${replacement.second} kcal",
                    calories = replacement.second,
                    description = replacement.third
                )
            } else {
                meal
            }
        }
    }
    
    var plan by remember(initialPlan) { mutableStateOf(initialPlan) }
    
    val tdee = remember { repo.getTargetCalories().toInt() }
    val currentPlanCalories = plan.sumOf { it.calories }
    val targetProtein = remember(tdee) { (tdee * 0.25f / 4f).toInt() }
    
    var indexToReplace by remember { mutableStateOf<Int?>(null) }
    
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
                    "Personalized for ${repo.getCity()} • Based on your profile",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Daily summary card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
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
                    PlanStatItem("Target (kcal)", "$tdee", "")
                    PlanStatItem("Plan (kcal)", "$currentPlanCalories", "")
                    PlanStatItem("Meals", "${plan.size}", "")
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
        items(plan.size) { index ->
            MealSuggestionCard(
                meal = plan[index],
                onReplaceClick = { indexToReplace = index }
            )
        }

        // Bottom spacer
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (indexToReplace != null) {
        ReplaceFoodDialog(
            db = db,
            onDismiss = { indexToReplace = null },
            onFoodSelected = { foodItem ->
                val idx = indexToReplace!!
                val oldMeal = plan[idx]
                val replacementDescription = "Custom substitution: ${foodItem.regionalName}"
                val newMeal = oldMeal.copy(
                    title = foodItem.foodName,
                    macros = "${foodItem.calories.toInt()} kcal",
                    calories = foodItem.calories.toInt(),
                    description = replacementDescription
                )
                
                // Persist the replacement
                repo.saveDietPlanReplacement(todayDateString, idx, foodItem.foodName, foodItem.calories.toInt(), replacementDescription)
                
                val newList = plan.toMutableList()
                newList[idx] = newMeal
                plan = newList
                indexToReplace = null
            }
        )
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
private fun MealSuggestionCard(meal: MealSuggestion, onReplaceClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        meal.icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Food details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        meal.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        meal.time,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (expanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            meal.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // Calories badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(NutriGreenSurface)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        meal.macros,
                        style = MaterialTheme.typography.labelMedium,
                        color = NutriGreen,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onReplaceClick) {
                        Text("Replace Item")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReplaceFoodDialog(
    db: com.example.nutriscan.data.local.AppDatabase,
    onDismiss: () -> Unit,
    onFoodSelected: (com.example.nutriscan.data.local.FoodItemEntity) -> Unit
) {
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<com.example.nutriscan.data.local.FoodItemEntity>>(emptyList()) }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            kotlinx.coroutines.delay(300)
            val results = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                db.mealDao().searchFoodItemsList(searchQuery)
            }
            searchResults = results
        } else {
            searchResults = emptyList()
        }
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text("Replace Food", fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Rounded.Close, contentDescription = "Close")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search food (e.g. Dosa)") },
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSearch = { keyboardController?.hide() })
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    if (searchQuery.isNotBlank() && searchResults.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No foods found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(searchResults.size) { index ->
                                val food = searchResults[index]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { 
                                            keyboardController?.hide()
                                            onFoodSelected(food)
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(food.foodName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                            Text(food.regionalName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text("${food.calories.toInt()} kcal", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
