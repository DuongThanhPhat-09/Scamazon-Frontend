package com.example.scamazon_frontend.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.auth.LoginRequest
import com.example.scamazon_frontend.di.ViewModelFactory

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateToRegister: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val isLoading = loginState is Resource.Loading
    
    LaunchedEffect(loginState) {
        when (loginState) {
            is Resource.Success -> {
                viewModel.resetState()
                onNavigateToHome()
            }
            is Resource.Error -> {
                emailError = loginState?.message
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
            Spacer(modifier = Modifier.height(60.dp))

            // Logo
            ScamazonLogo(
                size = 120.dp,
                showBrandName = true,
                showTagline = true
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Welcome Text
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Welcome Back",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sign in to continue shopping",
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
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            LafyuuPasswordField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                placeholder = "Password",
                isError = passwordError != null,
                errorMessage = passwordError,
                imeAction = ImeAction.Done,
                onImeAction = {
                    if (!isLoading) {
                        viewModel.login(LoginRequest(email = email, password = password))
                    }
                }
            )

            // Forgot Password - aligned right
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                LafyuuTextButton(
                    text = "Forgot Password?",
                    onClick = onNavigateToForgotPassword
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign In Button
            LafyuuPrimaryButton(
                text = if (isLoading) "Signing In..." else "Sign In",
                onClick = {
                    viewModel.login(LoginRequest(email = email, password = password))
                },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Divider with text
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(BorderLight)
                )
                Text(
                    text = "New to Scamazon?",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(BorderLight)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register Button
            LafyuuOutlinedButton(
                text = "Create Account",
                onClick = onNavigateToRegister
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Footer
            Text(
                text = "By signing in, you agree to our Terms of Service\nand Privacy Policy",
                fontFamily = Poppins,
                fontSize = 11.sp,
                color = TextHint,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    ScamazonFrontendTheme {
        LoginScreen()
    }
}
