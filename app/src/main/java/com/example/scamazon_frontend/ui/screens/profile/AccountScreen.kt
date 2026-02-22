package com.example.scamazon_frontend.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.core.utils.TokenManager
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun AccountScreen(
    viewModel: ProfileViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateToProfile: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onNavigateToWishlist: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // Top App Bar
        LafyuuTopAppBar(title = "Account")

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.ScreenPadding)
        ) {
            // Profile Header
            when (profileState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
                is Resource.Error -> {
                    ProfileHeader(
                        name = "Guest",
                        email = profileState.message ?: "Error loading profile",
                        avatarUrl = null,
                        onEditClick = onNavigateToProfile
                    )
                }
                is Resource.Success -> {
                    val profile = profileState.data!!
                    ProfileHeader(
                        name = profile.fullName ?: profile.username,
                        email = profile.email ?: "",
                        avatarUrl = profile.avatarUrl,
                        onEditClick = onNavigateToProfile
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Menu Items
            AccountMenuItem(
                icon = Icons.Outlined.Person,
                title = "Profile",
                subtitle = "Edit your profile information",
                onClick = onNavigateToProfile
            )

            AccountMenuItem(
                icon = Icons.Outlined.ShoppingBag,
                title = "My Orders",
                subtitle = "View your order history",
                onClick = onNavigateToOrders
            )

            AccountMenuItem(
                icon = Icons.Outlined.FavoriteBorder,
                title = "Wishlist",
                subtitle = "Your saved items",
                onClick = onNavigateToWishlist
            )

            AccountMenuItem(
                icon = Icons.Outlined.LocationOn,
                title = "Address",
                subtitle = "Manage delivery addresses",
                onClick = { /* Navigate to address */ }
            )

            AccountMenuItem(
                icon = Icons.Outlined.CreditCard,
                title = "Payment Methods",
                subtitle = "Manage payment options",
                onClick = { /* Navigate to payment */ }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = BorderLight
            )

            AccountMenuItem(
                icon = Icons.Outlined.ChatBubbleOutline,
                title = "Customer Support",
                subtitle = "Chat with us online",
                onClick = onNavigateToChat
            )

            AccountMenuItem(
                icon = Icons.Outlined.Map,
                title = "Store Location",
                subtitle = "Visit our flagship store",
                onClick = onNavigateToMap
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = BorderLight
            )

            AccountMenuItem(
                icon = Icons.Outlined.Notifications,
                title = "Notifications",
                subtitle = "Manage notifications",
                onClick = { /* Navigate to notifications */ }
            )

            AccountMenuItem(
                icon = Icons.Outlined.Settings,
                title = "Settings",
                subtitle = "App settings and preferences",
                onClick = onNavigateToSettings
            )

            AccountMenuItem(
                icon = Icons.Outlined.Help,
                title = "Help Center",
                subtitle = "Get help and support",
                onClick = { /* Navigate to help */ }
            )

            AccountMenuItem(
                icon = Icons.Outlined.Info,
                title = "About",
                subtitle = "App version and info",
                onClick = { /* Navigate to about */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            LafyuuOutlinedButton(
                text = "Sign Out",
                onClick = {
                    TokenManager(context).clearAll()
                    onNavigateToLogin()
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    email: String,
    avatarUrl: String?,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        if (!avatarUrl.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(Dimens.AvatarSizeLarge)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(Dimens.AvatarSizeLarge)
                    .clip(CircleShape)
                    .background(PrimaryBlueSoft),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1).uppercase(),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = PrimaryBlue
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // User Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = Typography.titleLarge,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = email,
                style = Typography.bodyMedium,
                color = TextSecondary
            )
        }

        // Edit Button
        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Profile",
                tint = PrimaryBlue
            )
        }
    }
}

@Composable
private fun AccountMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(PrimaryBlueSoft, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Text
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Typography.titleMedium,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = Typography.bodySmall,
                color = TextSecondary
            )
        }

        // Arrow
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextHint
        )
    }
}
