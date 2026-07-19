package com.example.nutriscan.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriscan.data.model.*
import com.example.nutriscan.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repo = remember { com.example.nutriscan.data.repository.ProfileRepository(context) }
    
    var name by remember { mutableStateOf(repo.getName()) }
    var age by remember { mutableStateOf(repo.getAge()) }
    var heightCm by remember { mutableStateOf(repo.getHeight()) }
    var weightKg by remember { mutableStateOf(repo.getWeight()) }
    var selectedGender by remember { mutableStateOf(repo.getGender()) }
    var selectedActivity by remember { mutableStateOf(repo.getActivityLevel()) }
    var selectedGoal by remember { mutableStateOf(repo.getHealthGoal()) }
    var selectedDiet by remember { mutableStateOf(repo.getDietaryPreference()) }
    var city by remember { mutableStateOf(repo.getCity()) }
    
    var isEditing by remember { mutableStateOf(name.isBlank()) }
    var showSavedMessage by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            // Profile Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(NutriGreenSurface),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(56.dp),
                        tint = NutriGreen,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (name.isNotBlank()) name else "Set Up Your Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "Health & Nutrition Profile",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // BMI Card (shows when data is available)
        if (heightCm.isNotBlank() && weightKg.isNotBlank()) {
            item {
                val h = heightCm.toFloatOrNull() ?: 0f
                val w = weightKg.toFloatOrNull() ?: 0f
                if (h > 0 && w > 0) {
                    val bmi = w / ((h / 100f) * (h / 100f))
                    BmiCard(bmi)
                }
            }
        }

        // Personal Info Section
        item {
            SectionHeader("Personal Information")
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = isEditing,
                singleLine = true,
            )
        }



        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it.filter { c -> c.isDigit() } },
                    label = { Text("Age") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = isEditing,
                    singleLine = true,
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isEditing,
                    singleLine = true,
                )
            }
        }

        // Gender Selection
        item {
            Text(
                "Gender",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                Gender.entries.forEachIndexed { index, gender ->
                    SegmentedButton(
                        selected = selectedGender == gender,
                        onClick = { if (isEditing) selectedGender = gender },
                        shape = SegmentedButtonDefaults.itemShape(index, Gender.entries.size),
                    ) {
                        Text(gender.displayName, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // Body Measurements
        item {
            SectionHeader("Body Measurements")
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = heightCm,
                    onValueChange = { heightCm = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Height (cm)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    enabled = isEditing,
                    singleLine = true,
                )
                OutlinedTextField(
                    value = weightKg,
                    onValueChange = { weightKg = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    enabled = isEditing,
                    singleLine = true,
                )
            }
        }

        // Activity Level
        item {
            SectionHeader("Activity Level")
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ActivityLevel.entries.forEach { level ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selectedActivity == level,
                            onClick = { if (isEditing) selectedActivity = level },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = NutriGreen,
                            ),
                        )
                        Text(
                            level.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    }
                }
            }
        }

        // Health Goal
        item {
            SectionHeader("Health Goal")
        }

        item {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                HealthGoal.entries.forEachIndexed { index, goal ->
                    SegmentedButton(
                        selected = selectedGoal == goal,
                        onClick = { if (isEditing) selectedGoal = goal },
                        shape = SegmentedButtonDefaults.itemShape(index, HealthGoal.entries.size),
                    ) {
                        Text(goal.displayName, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // Dietary Preference
        item {
            SectionHeader("Dietary Preference")
        }

        item {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                DietaryPreference.entries.forEachIndexed { index, pref ->
                    SegmentedButton(
                        selected = selectedDiet == pref,
                        onClick = { if (isEditing) selectedDiet = pref },
                        shape = SegmentedButtonDefaults.itemShape(index, DietaryPreference.entries.size),
                    ) {
                        Text(pref.displayName, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // --- New Personalization Settings ---
        item {
            SectionHeader("Personalization")
        }
        
        item {
            var isManualCalorie by remember { mutableStateOf(repo.isCalorieTargetManual()) }
            var manualCalorieText by remember { mutableStateOf(repo.getManualCalorieTarget().toInt().toString()) }
            var waterGoalText by remember { mutableStateOf(repo.getDailyWaterGoal().toString()) }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Calorie Target Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Manual Calorie Target",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Override automatically calculated BMR target",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isManualCalorie,
                        onCheckedChange = { 
                            if (isEditing) {
                                isManualCalorie = it
                                repo.setCalorieTargetManual(it)
                            }
                        },
                        enabled = isEditing
                    )
                }

                if (isManualCalorie) {
                    OutlinedTextField(
                        value = manualCalorieText,
                        onValueChange = { 
                            manualCalorieText = it.filter { c -> c.isDigit() }
                            val cals = manualCalorieText.toFloatOrNull()
                            if (cals != null && cals > 0) {
                                repo.setManualCalorieTarget(cals)
                            }
                        },
                        label = { Text("Target Calories (kcal)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = isEditing,
                        singleLine = true,
                    )
                }

                OutlinedTextField(
                    value = waterGoalText,
                    onValueChange = { 
                        waterGoalText = it.filter { c -> c.isDigit() }
                        val goal = waterGoalText.toIntOrNull()
                        if (goal != null && goal > 0) {
                            repo.setDailyWaterGoal(goal)
                        }
                    },
                    label = { Text("Daily Water Goal (Glasses)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = isEditing,
                    singleLine = true,
                )
            }
        }

        // Save Button
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (isEditing) {
                        val ageInt = age.toIntOrNull() ?: 0
                        val heightFloat = heightCm.toFloatOrNull() ?: 0f
                        val weightFloat = weightKg.toFloatOrNull() ?: 0f

                        if (ageInt <= 0 || heightFloat <= 0f || weightFloat <= 0f) {
                            android.widget.Toast.makeText(context, "Please enter valid age, height, and weight (> 0)", android.widget.Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        repo.saveName(name)
                        repo.saveAge(age)
                        repo.saveHeight(heightCm)
                        repo.saveWeight(weightKg)
                        repo.saveGender(selectedGender)
                        repo.saveActivityLevel(selectedActivity)
                        repo.saveHealthGoal(selectedGoal)
                        repo.saveDietaryPreference(selectedDiet)
                        repo.saveCity(city)
                        showSavedMessage = true
                    } else {
                        showSavedMessage = false
                    }
                    isEditing = !isEditing
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEditing) NutriGreen else NutriOrange,
                ),
            ) {
                Icon(
                    if (isEditing) Icons.Rounded.Save else Icons.Rounded.Edit,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (isEditing) "Save Profile" else "Edit Profile",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            if (showSavedMessage) {
                Text(
                    "Profile saved! Your calorie targets will be updated.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SuccessGreen,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }

        // Bottom spacer
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
private fun BmiCard(bmi: Float) {
    val (label, color) = when {
        bmi < 18.5f -> "Underweight" to CarbsAmber
        bmi < 25f -> "Normal" to SuccessGreen
        bmi < 30f -> "Overweight" to NutriOrange
        else -> "Obese" to ErrorRed
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "BMI (Body Mass Index)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "%.1f".format(bmi),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = color,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        label,
                        style = MaterialTheme.typography.titleSmall,
                        color = color,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
            }
        }
    }
}
