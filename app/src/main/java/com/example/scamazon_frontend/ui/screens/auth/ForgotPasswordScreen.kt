package com.example.scamazon_frontend.ui.screens.auth

import android.util.Patterns
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
fun ForgotPasswordScreen(
    viewModel: AuthViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateBack: () -> Unit = {},
    onNavigateToVerifyOtp: (String) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    val forgotPasswordState by viewModel.forgotPasswordState.collectAsStateWithLifecycle()
    val isLoading = forgotPasswordState is Resource.Loading

    LaunchedEffect(forgotPasswordState) {
        when (forgotPasswordState) {
            is Resource.Success -> {
                viewModel.resetState()
                onNavigateToVerifyOtp(email)
            }
            is Resource.Error -> {
                emailError = forgotPasswordState?.message
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
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logo
            ScamazonLogo(size = 100.dp, showBrandName = true, showTagline = false)

            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Forgot Password",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter your email to receive a 6-digit OTP",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Email Field
            LafyuuEmailField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                placeholder = "Email Address",
                isError = emailError != null,
                errorMessage = emailError,
                imeAction = ImeAction.Done,
                onImeAction = {
                    if (!isLoading) {
                        val err = validateEmail(email)
                        if (err != null) emailError = err
                        else viewModel.sendOtp(email)
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Send OTP Button
            LafyuuPrimaryButton(
                text = if (isLoading) "Sending..." else "Send OTP",
                onClick = {
                    val err = validateEmail(email)
                    if (err != null) {
                        emailError = err
                    } else {
                        viewModel.sendOtp(email)
                    }
                },
                enabled = !isLoading
            )
        }
    }
}

private fun validateEmail(email: String): String? {
    return when {
        email.isBlank() -> "Email là bắt buộc"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email không hợp lệ"
        else -> null
    }
}
