package com.example.scamazon_frontend.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.profile.UpdateProfileRequest
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val updateState by viewModel.updateState.collectAsStateWithLifecycle()
    val uploadAvatarState by viewModel.uploadAvatarState.collectAsStateWithLifecycle()

    // Form fields
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var ward by remember { mutableStateOf("") }
    var initialized by remember { mutableStateOf(false) }

    // Image picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.uploadAvatar(context, uri)
        }
    }

    // Populate form when profile loads
    LaunchedEffect(profileState) {
        if (profileState is Resource.Success && !initialized) {
            val profile = profileState.data!!
            fullName = profile.fullName ?: ""
            email = profile.email ?: ""
            phone = profile.phone ?: ""
            address = profile.address ?: ""
            city = profile.city ?: ""
            district = profile.district ?: ""
            ward = profile.ward ?: ""
            initialized = true
        }
    }

    // Handle update result
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(updateState) {
        when (updateState) {
            is Resource.Success -> {
                snackbarHostState.showSnackbar("Profile updated successfully!")
                viewModel.resetUpdateState()
            }
            is Resource.Error -> {
                snackbarHostState.showSnackbar(updateState?.message ?: "Update failed")
                viewModel.resetUpdateState()
            }
            else -> {}
        }
    }

    // Handle avatar upload result
    LaunchedEffect(uploadAvatarState) {
        when (uploadAvatarState) {
            is Resource.Success -> {
                snackbarHostState.showSnackbar("Avatar updated successfully!")
                viewModel.resetUploadAvatarState()
            }
            is Resource.Error -> {
                snackbarHostState.showSnackbar(uploadAvatarState?.message ?: "Avatar upload failed")
                viewModel.resetUploadAvatarState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundWhite)
        ) {
            LafyuuTopAppBar(title = "Edit Profile", onBackClick = onNavigateBack)

            when (profileState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
                is Resource.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profileState.message ?: "Error",
                            style = Typography.bodyLarge,
                            color = StatusError
                        )
                    }
                }
                is Resource.Success -> {
                    val profile = profileState.data
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(Dimens.ScreenPadding),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Avatar Section
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue.copy(alpha = 0.1f))
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                val isUploading = uploadAvatarState is Resource.Loading
                                val avatarUrl = profile?.avatarUrl

                                if (isUploading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(36.dp),
                                        color = PrimaryBlue,
                                        strokeWidth = 3.dp
                                    )
                                } else if (!avatarUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(avatarUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Avatar",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    // Fallback: first letter of username
                                    Text(
                                        text = (profile?.username?.firstOrNull()?.uppercase() ?: "U"),
                                        fontSize = 36.sp,
                                        color = PrimaryBlue,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            // Camera icon overlay
                            if (uploadAvatarState !is Resource.Loading) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .align(Alignment.Center)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Change avatar",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.BottomEnd)
                                            .background(PrimaryBlue, CircleShape)
                                            .padding(4.dp),
                                        tint = White
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Tap avatar to change",
                            style = Typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        HorizontalDivider(color = BorderLight)

                        // Full Name
                        Text(text = "Full Name", style = Typography.titleSmall, color = TextPrimary)
                        LafyuuTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            placeholder = "Enter your full name",
                            leadingIcon = Icons.Default.Person
                        )

                        // Email
                        Text(text = "Email", style = Typography.titleSmall, color = TextPrimary)
                        LafyuuTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "Enter your email",
                            leadingIcon = Icons.Default.Email,
                            keyboardType = KeyboardType.Email
                        )

                        // Phone
                        Text(text = "Phone", style = Typography.titleSmall, color = TextPrimary)
                        LafyuuTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            placeholder = "Enter your phone number",
                            leadingIcon = Icons.Default.Phone,
                            keyboardType = KeyboardType.Phone
                        )

                        // Address
                        Text(text = "Address", style = Typography.titleSmall, color = TextPrimary)
                        LafyuuTextField(
                            value = address,
                            onValueChange = { address = it },
                            placeholder = "Street address",
                            leadingIcon = Icons.Default.LocationOn
                        )

                        // City, District, Ward
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LafyuuTextField(
                                value = city,
                                onValueChange = { city = it },
                                placeholder = "City",
                                modifier = Modifier.weight(1f)
                            )
                            LafyuuTextField(
                                value = district,
                                onValueChange = { district = it },
                                placeholder = "District",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        LafyuuTextField(
                            value = ward,
                            onValueChange = { ward = it },
                            placeholder = "Ward",
                            imeAction = ImeAction.Done
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Save Button
                    val isUpdating = updateState is Resource.Loading
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 8.dp,
                        color = White
                    ) {
                        LafyuuPrimaryButton(
                            text = if (isUpdating) "Saving..." else "Save Changes",
                            onClick = {
                                viewModel.updateProfile(
                                    UpdateProfileRequest(
                                        fullName = fullName.ifBlank { null },
                                        email = email.ifBlank { null },
                                        phone = phone.ifBlank { null },
                                        address = address.ifBlank { null },
                                        city = city.ifBlank { null },
                                        district = district.ifBlank { null },
                                        ward = ward.ifBlank { null }
                                    )
                                )
                            },
                            enabled = !isUpdating,
                            modifier = Modifier.padding(Dimens.ScreenPadding)
                        )
                    }
                }
            }
        }
    }
}
