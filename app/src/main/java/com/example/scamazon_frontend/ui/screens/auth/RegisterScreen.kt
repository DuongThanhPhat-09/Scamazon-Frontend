package com.example.scamazon_frontend.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.scamazon_frontend.data.models.auth.RegisterRequest
import com.example.scamazon_frontend.di.ViewModelFactory

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateToLogin: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val registerState by viewModel.registerState.collectAsStateWithLifecycle()
    val isLoading = registerState is Resource.Loading
    
    LaunchedEffect(registerState) {
        when (registerState) {
            is Resource.Success -> {
                viewModel.resetState()
                onNavigateToHome()
            }
            is Resource.Error -> {
                emailError = registerState?.message // Basic error mapping
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
            Spacer(modifier = Modifier.height(48.dp))

            // Logo (smaller for register screen)
            ScamazonLogo(
                size = 100.dp,
                showBrandName = true,
                showTagline = false
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Welcome Text
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Create Account",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Join us and start shopping today",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Full Name Field
            LafyuuTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    fullNameError = null
                },
                placeholder = "Full Name",
                leadingIcon = Icons.Default.Person,
                isError = fullNameError != null,
                errorMessage = fullNameError,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(14.dp))

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

            Spacer(modifier = Modifier.height(14.dp))

            // Phone Field
            LafyuuTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    phoneError = null
                },
                placeholder = "Phone Number (Optional)",
                leadingIcon = Icons.Default.Phone,
                isError = phoneError != null,
                errorMessage = phoneError,
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(14.dp))

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
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(14.dp))

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
                        performRegistrationValidation(
                            fullName = fullName,
                            email = email,
                            phone = phone,
                            password = password,
                            confirmPassword = confirmPassword,
                            onFullNameError = { fullNameError = it },
                            onEmailError = { emailError = it },
                            onPhoneError = { phoneError = it },
                            onPasswordError = { passwordError = it },
                            onConfirmPasswordError = { confirmPasswordError = it },
                            onValid = {
                                viewModel.register(
                                    RegisterRequest(
                                        username = email.substringBefore("@").replace(".", "_"), // basic strategy for username
                                        email = email,
                                        password = password,
                                        fullName = fullName,
                                        phone = phone.ifBlank { null }
                                    )
                                )
                            }
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Button
            LafyuuPrimaryButton(
                text = if (isLoading) "Creating Account..." else "Create Account",
                onClick = {
                    performRegistrationValidation(
                        fullName = fullName,
                        email = email,
                        phone = phone,
                        password = password,
                        confirmPassword = confirmPassword,
                        onFullNameError = { fullNameError = it },
                        onEmailError = { emailError = it },
                        onPhoneError = { phoneError = it },
                        onPasswordError = { passwordError = it },
                        onConfirmPasswordError = { confirmPasswordError = it },
                        onValid = {
                            viewModel.register(
                                RegisterRequest(
                                    username = email.substringBefore("@").replace(".", "_"),
                                    email = email,
                                    password = password,
                                    fullName = fullName,
                                    phone = phone.ifBlank { null }
                                )
                            )
                        }
                    )
                },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                    text = "Already have an account?",
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

            Spacer(modifier = Modifier.height(20.dp))

            // Login Button
            LafyuuOutlinedButton(
                text = "Sign In",
                onClick = onNavigateToLogin
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Text(
                text = "By creating an account, you agree to our\nTerms of Service and Privacy Policy",
                fontFamily = Poppins,
                fontSize = 11.sp,
                color = TextHint,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun performRegistrationValidation(
    fullName: String,
    email: String,
    phone: String,
    password: String,
    confirmPassword: String,
    onFullNameError: (String?) -> Unit,
    onEmailError: (String?) -> Unit,
    onPhoneError: (String?) -> Unit,
    onPasswordError: (String?) -> Unit,
    onConfirmPasswordError: (String?) -> Unit,
    onValid: () -> Unit
) {
    var isValid = true

    // Validate full name
    if (fullName.isBlank()) {
        onFullNameError("Full name is required")
        isValid = false
    } else if (fullName.length < 2) {
        onFullNameError("Full name must be at least 2 characters")
        isValid = false
    } else {
        onFullNameError(null)
    }

    // Validate email
    if (email.isBlank()) {
        onEmailError("Email is required")
        isValid = false
    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        onEmailError("Please enter a valid email")
        isValid = false
    } else {
        onEmailError(null)
    }

    // Validate phone (optional but if provided, must be valid)
    if (phone.isNotBlank() && phone.length < 10) {
        onPhoneError("Please enter a valid phone number")
        isValid = false
    } else {
        onPhoneError(null)
    }

    // Validate password
    if (password.isBlank()) {
        onPasswordError("Password is required")
        isValid = false
    } else if (password.length < 6) {
        onPasswordError("Password must be at least 6 characters")
        isValid = false
    } else {
        onPasswordError(null)
    }

    // Validate confirm password
    if (confirmPassword.isBlank()) {
        onConfirmPasswordError("Please confirm your password")
        isValid = false
    } else if (confirmPassword != password) {
        onConfirmPasswordError("Passwords do not match")
        isValid = false
    } else {
        onConfirmPasswordError(null)
    }

    if (isValid) {
        onValid()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    ScamazonFrontendTheme {
        RegisterScreen()
    }
}
