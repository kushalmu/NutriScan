package com.example.nutriscan.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nutriscan.data.model.ActivityLevel
import com.example.nutriscan.data.model.DietaryPreference
import com.example.nutriscan.data.model.Gender
import com.example.nutriscan.data.model.HealthGoal
import com.example.nutriscan.theme.NutriGreen
import com.example.nutriscan.theme.NutriGreenSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repo = remember { com.example.nutriscan.data.repository.ProfileRepository(context) }
    
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var heightCm by remember { mutableStateOf("") }
    var weightKg by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf(Gender.MALE) }
    var selectedGoal by remember { mutableStateOf(HealthGoal.MAINTAIN) }
    var selectedDiet by remember { mutableStateOf(DietaryPreference.NON_VEG) }
    var isManualCalorie by remember { mutableStateOf(false) }
    var manualCalorieText by remember { mutableStateOf("2000") }
    var waterGoalText by remember { mutableStateOf("8") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Welcome to NutriScan",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Let's set up your profile to personalize your nutrition plan.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it.filter { c -> c.isDigit() } },
                        label = { Text("Age") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = heightCm,
                        onValueChange = { heightCm = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Height (cm)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = weightKg,
                        onValueChange = { weightKg = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                    )
                }
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Gender", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        Gender.entries.forEachIndexed { index, gender ->
                            SegmentedButton(
                                selected = selectedGender == gender,
                                onClick = { selectedGender = gender },
                                shape = SegmentedButtonDefaults.itemShape(index, Gender.entries.size),
                            ) {
                                Text(gender.displayName, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Health Goal", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        HealthGoal.entries.forEachIndexed { index, goal ->
                            SegmentedButton(
                                selected = selectedGoal == goal,
                                onClick = { selectedGoal = goal },
                                shape = SegmentedButtonDefaults.itemShape(index, HealthGoal.entries.size),
                            ) {
                                Text(goal.displayName, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Dietary Preference", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        DietaryPreference.entries.forEachIndexed { index, pref ->
                            SegmentedButton(
                                selected = selectedDiet == pref,
                                onClick = { selectedDiet = pref },
                                shape = SegmentedButtonDefaults.itemShape(index, DietaryPreference.entries.size),
                            ) {
                                Text(pref.displayName, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NutriGreenSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Set Calorie Goal Manually", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text("Otherwise, we will calculate it based on your BMR.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = isManualCalorie, onCheckedChange = { isManualCalorie = it })
                        }
                        if (isManualCalorie) {
                            OutlinedTextField(
                                value = manualCalorieText,
                                onValueChange = { manualCalorieText = it.filter { c -> c.isDigit() } },
                                label = { Text("Target Calories (kcal)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        }

                        OutlinedTextField(
                            value = waterGoalText,
                            onValueChange = { waterGoalText = it.filter { c -> c.isDigit() } },
                            label = { Text("Daily Water Goal (Glasses)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        val ageInt = age.toIntOrNull() ?: 0
                        val heightFloat = heightCm.toFloatOrNull() ?: 0f
                        val weightFloat = weightKg.toFloatOrNull() ?: 0f
                        val manualCals = manualCalorieText.toFloatOrNull() ?: 2000f
                        val waterGoal = waterGoalText.toIntOrNull() ?: 8

                        if (name.isBlank() || ageInt <= 0 || heightFloat <= 0f || weightFloat <= 0f) {
                            android.widget.Toast.makeText(context, "Please fill in all details correctly.", android.widget.Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        repo.saveName(name)
                        repo.saveAge(age)
                        repo.saveHeight(heightCm)
                        repo.saveWeight(weightKg)
                        repo.saveGender(selectedGender)
                        repo.saveHealthGoal(selectedGoal)
                        repo.saveDietaryPreference(selectedDiet)
                        repo.setCalorieTargetManual(isManualCalorie)
                        repo.setManualCalorieTarget(manualCals)
                        repo.setDailyWaterGoal(waterGoal)
                        
                        repo.setHasCompletedOnboarding(true)
                        onFinish()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NutriGreen)
                ) {
                    Text("Complete Setup", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
