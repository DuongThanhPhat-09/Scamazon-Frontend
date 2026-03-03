package com.example.scamazon_frontend.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun ResetPasswordScreen(
    email: String,
    otp: String,
    viewModel: AuthViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateToLogin: () -> Unit = {}
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val resetPasswordState by viewModel.resetPasswordState.collectAsStateWithLifecycle()
    val isLoading = resetPasswordState is Resource.Loading

    LaunchedEffect(resetPasswordState) {
        when (resetPasswordState) {
            is Resource.Success -> {
                viewModel.resetState()
                onNavigateToLogin()
            }
            is Resource.Error -> {
                newPasswordError = resetPasswordState?.message
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Back button
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                IconButton(onClick = onNavigateToLogin) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ScamazonLogo(size = 100.dp, showBrandName = true, showTagline = false)

            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "New Password",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Set a new password for your account",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // New Password Field
            LafyuuPasswordField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    newPasswordError = null
                },
                placeholder = "New Password",
                isError = newPasswordError != null,
                errorMessage = newPasswordError,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            LafyuuPasswordField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = null
                },
                placeholder = "Confirm Password",
                isError = confirmPasswordError != null,
                errorMessage = confirmPasswordError,
                imeAction = ImeAction.Done,
                onImeAction = {
                    if (!isLoading) {
                        submitReset(email, otp, newPassword, confirmPassword,
                            onNewPasswordError = { newPasswordError = it },
                            onConfirmPasswordError = { confirmPasswordError = it },
                            onValid = { viewModel.resetPassword(email, otp, newPassword) }
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Reset Password Button
            LafyuuPrimaryButton(
                text = if (isLoading) "Resetting..." else "Reset Password",
                onClick = {
                    submitReset(email, otp, newPassword, confirmPassword,
                        onNewPasswordError = { newPasswordError = it },
                        onConfirmPasswordError = { confirmPasswordError = it },
                        onValid = { viewModel.resetPassword(email, otp, newPassword) }
                    )
                },
                enabled = !isLoading
            )
        }
    }
}

private fun submitReset(
    email: String,
    otp: String,
    newPassword: String,
    confirmPassword: String,
    onNewPasswordError: (String?) -> Unit,
    onConfirmPasswordError: (String?) -> Unit,
    onValid: () -> Unit
) {
    var hasError = false

    if (newPassword.isBlank()) {
        onNewPasswordError("Mật khẩu mới là bắt buộc")
        hasError = true
    } else if (newPassword.length < 6) {
        onNewPasswordError("Mật khẩu phải có ít nhất 6 ký tự")
        hasError = true
    }

    if (confirmPassword.isBlank()) {
        onConfirmPasswordError("Xác nhận mật khẩu là bắt buộc")
        hasError = true
    } else if (newPassword != confirmPassword) {
        onConfirmPasswordError("Mật khẩu xác nhận không khớp")
        hasError = true
    }

    if (!hasError) onValid()
}
