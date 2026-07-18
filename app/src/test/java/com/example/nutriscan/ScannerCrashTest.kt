package com.example.nutriscan

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.nutriscan.data.network.OfflineScanner
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ScannerCrashTest {
    @Test
    fun testOfflineScannerInit() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        println("Initializing OfflineScanner...")
        val scanner = OfflineScanner(context)
        println("OfflineScanner initialized successfully.")
    }
}
