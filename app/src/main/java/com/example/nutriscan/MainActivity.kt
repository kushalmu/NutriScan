package com.example.nutriscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.nutriscan.theme.NutriScanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            val repo = androidx.compose.runtime.remember { com.example.nutriscan.data.repository.ProfileRepository(context) }
            var showOnboarding by androidx.compose.runtime.remember { 
                androidx.compose.runtime.mutableStateOf(!repo.hasCompletedOnboarding()) 
            }

            NutriScanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    if (showOnboarding) {
                        com.example.nutriscan.ui.onboarding.OnboardingScreen(
                            onFinish = { showOnboarding = false }
                        )
                    } else {
                        MainNavigation()
                    }
                }
            }
        }
    }
}
