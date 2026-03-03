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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun VerifyOtpScreen(
    email: String,
    viewModel: AuthViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateBack: () -> Unit = {},
    onNavigateToResetPassword: (String, String) -> Unit = { _, _ -> }
) {
    var otp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }

    val verifyOtpState by viewModel.verifyOtpState.collectAsStateWithLifecycle()
    val forgotPasswordState by viewModel.forgotPasswordState.collectAsStateWithLifecycle()
    val isLoading = verifyOtpState is Resource.Loading
    val isResending = forgotPasswordState is Resource.Loading

    LaunchedEffect(verifyOtpState) {
        when (verifyOtpState) {
            is Resource.Success -> {
                viewModel.resetState()
                onNavigateToResetPassword(email, otp)
            }
            is Resource.Error -> {
                otpError = verifyOtpState?.message
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

            ScamazonLogo(size = 100.dp, showBrandName = true, showTagline = false)

            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Verify OTP",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    buildAnnotatedString {
                        append("OTP has been sent to ")
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = PrimaryBlue)) {
                            append(email)
                        }
                    },
                    fontFamily = Poppins,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // OTP Field
            LafyuuTextField(
                value = otp,
                onValueChange = {
                    if (it.length <= 6) {
                        otp = it.filter { c -> c.isDigit() }
                        otpError = null
                    }
                },
                placeholder = "Enter 6-digit OTP",
                keyboardType = KeyboardType.NumberPassword,
                isError = otpError != null,
                errorMessage = otpError,
                imeAction = ImeAction.Done,
                onImeAction = {
                    if (!isLoading) {
                        val err = validateOtp(otp)
                        if (err != null) otpError = err
                        else viewModel.verifyOtp(email, otp)
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Verify Button
            LafyuuPrimaryButton(
                text = if (isLoading) "Verifying..." else "Verify OTP",
                onClick = {
                    val err = validateOtp(otp)
                    if (err != null) {
                        otpError = err
                    } else {
                        viewModel.verifyOtp(email, otp)
                    }
                },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Resend OTP
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Didn't receive the code?",
                    fontFamily = Poppins,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                LafyuuTextButton(
                    text = if (isResending) "Sending..." else "Resend",
                    onClick = {
                        if (!isResending) {
                            viewModel.sendOtp(email)
                        }
                    }
                )
            }
        }
    }
}

private fun validateOtp(otp: String): String? {
    return when {
        otp.isBlank() -> "Mã OTP là bắt buộc"
        otp.length != 6 -> "Mã OTP phải đúng 6 chữ số"
        else -> null
    }
}
