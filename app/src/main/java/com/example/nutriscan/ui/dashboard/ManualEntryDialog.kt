package com.example.nutriscan.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.nutriscan.data.local.AppDatabase
import com.example.nutriscan.data.local.FoodItemEntity
import com.example.nutriscan.data.local.MealEntity
import com.example.nutriscan.data.model.MealType
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntryDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FoodItemEntity>>(emptyList()) }
    var selectedFood by remember { mutableStateOf<FoodItemEntity?>(null) }
    var quantity by remember { mutableIntStateOf(1) }
    
    // Pre-populate data if missing (e.g. missed during migration)
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val allFoods = db.mealDao().searchFoodItemsList("")
            if (allFoods.isEmpty()) {
                db.mealDao().insertFoodItems(com.example.nutriscan.data.local.FoodData.foods)
            }
        }
    }

    // Automatically search when query changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            val results = withContext(Dispatchers.IO) {
                db.mealDao().searchFoodItemsList(searchQuery)
            }
            searchResults = results
        } else {
            searchResults = emptyList()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                TopAppBar(
                    title = { Text("Add Food Manually", fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Rounded.Close, contentDescription = "Close")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                if (selectedFood == null) {
                    // Search Mode
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search food (e.g. Dosa)") },
                            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        if (searchQuery.isNotBlank() && searchResults.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No foods found.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(searchResults.size) { index ->
                                    val food = searchResults[index]
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { 
                                                selectedFood = food 
                                                keyboardController?.hide()
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
                } else {
                    // Detail & Add Mode
                    val food = selectedFood!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            food.foodName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            "1 serving (100g)",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Macros preview
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    MacroStat("Calories", "${(food.calories * quantity).toInt()} kcal")
                                    MacroStat("Protein", "${(food.proteinG * quantity).toInt()} g")
                                    MacroStat("Carbs", "${(food.carbsG * quantity).toInt()} g")
                                    MacroStat("Fat", "${(food.fatG * quantity).toInt()} g")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        // Quantity Selector
                        Text("Quantity", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledIconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Icon(Icons.Rounded.Remove, contentDescription = "Decrease")
                            }
                            Text(
                                "$quantity",
                                modifier = Modifier.padding(horizontal = 32.dp),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            FilledIconButton(
                                onClick = { quantity++ },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = "Increase")
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    try {
                                        val nutritionInfo = food.toNutritionInfo().copy(
                                            portionGrams = 100f * quantity,
                                            calories = food.calories * quantity,
                                            proteinG = food.proteinG * quantity,
                                            carbsG = food.carbsG * quantity,
                                            fatG = food.fatG * quantity,
                                            fiberG = food.fiberG * quantity
                                        )
                                        val mealEntity = MealEntity(
                                            id = UUID.randomUUID().toString(),
                                            timestamp = System.currentTimeMillis(),
                                            dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                            mealType = MealType.LUNCH.name, // Hardcoded default for now
                                            itemsJson = Gson().toJson(listOf(nutritionInfo)),
                                            isAiAnalyzed = false
                                        )
                                        withContext(Dispatchers.IO) {
                                            db.mealDao().insertMeal(mealEntity)
                                        }
                                        android.widget.Toast.makeText(context, "Added successfully", android.widget.Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    } catch (e: Exception) {
                                        android.widget.Toast.makeText(context, "Error saving", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Add to Today's Meals", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        TextButton(onClick = { selectedFood = null }) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
