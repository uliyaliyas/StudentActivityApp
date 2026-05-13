package com.example.studentactivityapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.studentactivityapp.navigation.AppNavigation
import com.example.studentactivityapp.service.MyFirebaseMessagingService

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupStatusBar()
        createNotificationChannel()
        requestNotificationPermission()
        setContent {
            AppNavigation()
        }
    }

    private fun setupStatusBar() {
        window.statusBarColor = android.graphics.Color.parseColor("#ECDFFF")
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            MyFirebaseMessagingService.CHANNEL_ID,
            "Уведомления",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Уведомления о новых заданиях и наградах"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
