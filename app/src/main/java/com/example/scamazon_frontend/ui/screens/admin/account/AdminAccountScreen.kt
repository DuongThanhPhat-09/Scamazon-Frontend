package com.example.scamazon_frontend.ui.screens.admin.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.core.utils.TokenManager
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.screens.profile.ProfileViewModel
import com.example.scamazon_frontend.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAccountScreen(
    viewModel: ProfileViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Admin Account",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlueSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.AdminPanelSettings,
                    contentDescription = "Admin",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Admin Info
            when (profileState) {
                is Resource.Success -> {
                    val profile = (profileState as Resource.Success).data!!
                    Text(
                        text = profile.fullName ?: profile.username,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = profile.email ?: "",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PrimaryBlueSoft
                    ) {
                        Text(
                            text = "Administrator",
                            fontFamily = Poppins,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
                is Resource.Loading -> {
                    CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(24.dp))
                }
                else -> {
                    Text(
                        text = "Admin User",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Menu Items
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    AdminMenuItem(
                        icon = Icons.Filled.Person,
                        title = "Profile",
                        subtitle = "View your profile information"
                    )
                    HorizontalDivider(color = BorderLight)
                    AdminMenuItem(
                        icon = Icons.Filled.Settings,
                        title = "Settings",
                        subtitle = "App settings"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                AdminMenuItem(
                    icon = Icons.Filled.Logout,
                    title = "Log Out",
                    subtitle = "Sign out of your account",
                    iconTint = StatusError,
                    titleColor = StatusError,
                    onClick = { showLogoutDialog = true }
                )
            }
        }
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out", fontFamily = Poppins, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out?", fontFamily = Poppins) },
            confirmButton = {
                TextButton(
                    onClick = {
                        tokenManager.clearAll()
                        showLogoutDialog = false
                        onNavigateToLogin()
                    }
                ) {
                    Text("Log Out", color = StatusError, fontFamily = Poppins)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = TextSecondary, fontFamily = Poppins)
                }
            }
        )
    }
}

@Composable
private fun AdminMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconTint: androidx.compose.ui.graphics.Color = PrimaryBlue,
    titleColor: androidx.compose.ui.graphics.Color = TextPrimary,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = titleColor
            )
            Text(
                text = subtitle,
                fontFamily = Poppins,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
        IconButton(onClick = onClick) {
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Go",
                tint = TextSecondary
            )
        }
    }
}
