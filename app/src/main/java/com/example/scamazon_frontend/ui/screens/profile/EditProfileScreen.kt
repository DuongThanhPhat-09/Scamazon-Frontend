package com.example.scamazon_frontend.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("John Doe") }
    var email by remember { mutableStateOf("johndoe@email.com") }
    var phone by remember { mutableStateOf("+1 555 234 5678") }
    var username by remember { mutableStateOf("johndoe") }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(showSuccessSnackbar) {
        if (showSuccessSnackbar) {
            snackbarHostState.showSnackbar("Profile updated successfully!")
            showSuccessSnackbar = false
            onSaveSuccess()
        }
    }

    fun validate(): Boolean {
        var valid = true
        fullNameError = if (fullName.isBlank()) {
            valid = false
            "Full name is required"
        } else null

        emailError = if (email.isBlank()) {
            valid = false
            "Email is required"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            valid = false
            "Enter a valid email"
        } else null

        phoneError = if (phone.isNotBlank() && phone.length < 8) {
            valid = false
            "Enter a valid phone number"
        } else null

        return valid
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundWhite)
        ) {
            LafyuuTopAppBar(
                title = "Edit Profile",
                onBackClick = onNavigateBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(Dimens.ScreenPadding),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Avatar Section
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlueSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = fullName.take(1).uppercase(),
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Bold,
                                fontSize = 36.sp,
                                color = PrimaryBlue
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue)
                                .align(Alignment.BottomEnd)
                                .clickable { /* Pick image */ },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change photo",
                                tint = White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Tap avatar to change photo",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Form Fields
                FormFieldLabel(text = "Full Name")
                LafyuuTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        fullNameError = null
                    },
                    placeholder = "Enter your full name",
                    leadingIcon = Icons.Default.Person,
                    isError = fullNameError != null,
                    errorMessage = fullNameError,
                    imeAction = ImeAction.Next
                )

                FormFieldLabel(text = "Username")
                LafyuuTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "Enter username",
                    leadingIcon = Icons.Default.Person,
                    imeAction = ImeAction.Next
                )

                FormFieldLabel(text = "Email Address")
                LafyuuEmailField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    placeholder = "Enter your email",
                    isError = emailError != null,
                    errorMessage = emailError,
                    imeAction = ImeAction.Next
                )

                FormFieldLabel(text = "Phone Number")
                LafyuuTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        phoneError = null
                    },
                    placeholder = "Enter your phone number",
                    leadingIcon = Icons.Default.Phone,
                    isError = phoneError != null,
                    errorMessage = phoneError,
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done,
                    onImeAction = {
                        if (validate()) {
                            isLoading = true
                            showSuccessSnackbar = true
                            isLoading = false
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Save Button
                LafyuuPrimaryButton(
                    text = if (isLoading) "Saving..." else "Save Changes",
                    onClick = {
                        if (validate()) {
                            isLoading = true
                            showSuccessSnackbar = true
                            isLoading = false
                        }
                    },
                    enabled = !isLoading
                )

                // Cancel Button
                LafyuuOutlinedButton(
                    text = "Cancel",
                    onClick = onNavigateBack
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FormFieldLabel(text: String) {
    Text(
        text = text,
        fontFamily = Poppins,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProfileScreenPreview() {
    ScamazonFrontendTheme {
        EditProfileScreen()
    }
}
