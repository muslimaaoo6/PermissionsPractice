package com.example.permissionspractice

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PermissionsSettingsScreen()
                }
            }
        }
    }
}

/**
 * User perm bloklangan holatda sozlamalarga yo'naltiruvchi funksiya
 */
private fun navigateToAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

@Composable
fun PermissionsSettingsScreen() {
    val context = LocalContext.current

    // Har bir ruxsat uchun qolgan urinishlar soni (maksimum 3 ta)
    var cameraAttempts by remember { mutableIntStateOf(3) }
    var locationAttempts by remember { mutableIntStateOf(3) }
    var mediaAttempts by remember { mutableIntStateOf(3) }

    // Ruxsat berilganlik holatlari
    var isCameraAllowed by remember { mutableStateOf(false) }
    var isLocationAllowed by remember { mutableStateOf(false) }
    var isMediaAllowed by remember { mutableStateOf(false) }

    // 1. Camera permission launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            isCameraAllowed = true
        } else {
            if (cameraAttempts > 0) cameraAttempts--
        }
    }

    // 2. Geolocation permission launcher
    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            isLocationAllowed = true
        } else {
            if (locationAttempts > 0) locationAttempts--
        }
    }

    // 3. Media access (Photos/Videos) launcher
    val mediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            isMediaAllowed = true
        } else {
            if (mediaAttempts > 0) mediaAttempts--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "App Permissions Manager",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Camera Card
        PermissionCard(
            title = "Camera Access",
            subtitle = "Required to capture photos and videos inside the app.",
            isGranted = isCameraAllowed,
            attemptsLeft = cameraAttempts,
            onRequestClick = {
                if (cameraAttempts > 0) {
                    cameraLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            onSettingsClick = { navigateToAppSettings(context) }
        )

        // Geolocation Card
        PermissionCard(
            title = "Geolocation Access",
            subtitle = "Used for location-based features and mapping.",
            isGranted = isLocationAllowed,
            attemptsLeft = locationAttempts,
            onRequestClick = {
                if (locationAttempts > 0) {
                    locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            onSettingsClick = { navigateToAppSettings(context) }
        )

        // Media Access Card
        PermissionCard(
            title = "Photos & Videos",
            subtitle = "Needed to select and upload media files from gallery.",
            isGranted = isMediaAllowed,
            attemptsLeft = mediaAttempts,
            onRequestClick = {
                if (mediaAttempts > 0) {
                    mediaLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            },
            onSettingsClick = { navigateToAppSettings(context) }
        )
    }
}

@Composable
fun PermissionCard(
    title: String,
    subtitle: String,
    isGranted: Boolean,
    attemptsLeft: Int,
    onRequestClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFD0D0D0), RoundedCornerShape(12.dp))
            .background(Color(0xFFF7F9FA), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1C1B1F)
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            when {
                isGranted -> {
                    Text(
                        text = "Status: Permission Granted",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32)
                    )
                }
                attemptsLeft > 0 -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tries left: $attemptsLeft",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFD32F2F)
                        )
                        Button(
                            onClick = onRequestClick,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Request")
                        }
                    }
                }
                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Blocked (0 tries left)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                        Button(
                            onClick = onSettingsClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A4A4A)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Go to Settings")
                        }
                    }
                }
            }
        }
    }
}