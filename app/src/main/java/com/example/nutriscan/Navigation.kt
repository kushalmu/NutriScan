package com.example.nutriscan

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nutriscan.theme.NutriGreen
import com.example.nutriscan.ui.dashboard.DashboardScreen
import com.example.nutriscan.ui.dietplan.DietPlanScreen
import com.example.nutriscan.ui.profile.ProfileScreen
import com.example.nutriscan.ui.scanner.ScannerScreen

// Bottom navigation item data
private data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Rounded.Home, Icons.Outlined.Home),
    BottomNavItem("Scan", Icons.Rounded.CameraAlt, Icons.Outlined.CameraAlt),
    BottomNavItem("Plan", Icons.Rounded.Restaurant, Icons.Outlined.Restaurant),
    BottomNavItem("Profile", Icons.Rounded.Person, Icons.Outlined.Person),
)

@Composable
fun MainNavigation() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                            )
                        },
                        label = {
                            Text(
                                item.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NutriGreen,
                            selectedTextColor = NutriGreen,
                            indicatorColor = NutriGreen.copy(alpha = 0.12f),
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        // Show the selected screen
        when (selectedTab) {
            0 -> DashboardScreen(modifier = Modifier.padding(innerPadding).safeDrawingPadding())
            1 -> ScannerScreen(modifier = Modifier.padding(innerPadding).safeDrawingPadding())
            2 -> DietPlanScreen(modifier = Modifier.padding(innerPadding).safeDrawingPadding())
            3 -> ProfileScreen(modifier = Modifier.padding(innerPadding).safeDrawingPadding())
        }
    }
}
