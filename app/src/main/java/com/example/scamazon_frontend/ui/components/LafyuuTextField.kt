package com.example.scamazon_frontend.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.example.scamazon_frontend.ui.theme.*

/**
 * Standard Text Field - Blue Theme Style
 */
@Composable
fun LafyuuTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    singleLine: Boolean = true,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = Poppins,
                    color = TextHint
                )
            },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (isError) SecondaryRed else TextSecondary
                    )
                }
            },
            trailingIcon = trailingIcon,
            isError = isError,
            singleLine = singleLine,
            enabled = enabled,
            shape = LafyuuShapes.InputFieldShape,
            colors = OutlinedTextFieldDefaults.colors(
                // Focused state
                focusedBorderColor = PrimaryBlue,
                focusedLabelColor = PrimaryBlue,
                focusedLeadingIconColor = PrimaryBlue,
                focusedTrailingIconColor = PrimaryBlue,
                // Unfocused state
                unfocusedBorderColor = BorderDefault,
                unfocusedLabelColor = TextSecondary,
                unfocusedLeadingIconColor = TextSecondary,
                unfocusedTrailingIconColor = TextSecondary,
                // Error state
                errorBorderColor = SecondaryRed,
                errorLabelColor = SecondaryRed,
                errorLeadingIconColor = SecondaryRed,
                errorTrailingIconColor = SecondaryRed,
                // Container
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                errorContainerColor = White,
                // Cursor
                cursorColor = PrimaryBlue
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = { onImeAction() },
                onNext = { onImeAction() },
                onSearch = { onImeAction() }
            ),
            textStyle = Typography.bodyLarge.copy(color = TextPrimary)
        )

        // Error message
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = SecondaryRed,
                style = Typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Password Text Field - Blue Theme Style
 */
@Composable
fun LafyuuPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Password",
    isError: Boolean = false,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = Poppins,
                    color = TextHint
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isError) SecondaryRed else TextSecondary
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = TextSecondary
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = isError,
            singleLine = true,
            shape = LafyuuShapes.InputFieldShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = BorderDefault,
                errorBorderColor = SecondaryRed,
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                errorContainerColor = White,
                cursorColor = PrimaryBlue
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = { onImeAction() },
                onNext = { onImeAction() }
            ),
            textStyle = Typography.bodyLarge.copy(color = TextPrimary)
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = SecondaryRed,
                style = Typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Email Text Field - Blue Theme Style
 */
@Composable
fun LafyuuEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Your Email",
    isError: Boolean = false,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    LafyuuTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = Icons.Default.Email,
        isError = isError,
        errorMessage = errorMessage,
        keyboardType = KeyboardType.Email,
        imeAction = imeAction,
        onImeAction = onImeAction
    )
}

/**
 * Search Text Field - Blue Theme Style
 */
@Composable
fun LafyuuSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search Product",
    onSearch: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                fontFamily = Poppins,
                color = TextHint
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TextSecondary
            )
        },
        singleLine = true,
        shape = LafyuuShapes.SearchBarShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = BorderDefault,
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            cursorColor = PrimaryBlue
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        ),
        textStyle = Typography.bodyLarge.copy(color = TextPrimary)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFAF9F6)
@Composable
fun LafyuuTextFieldsPreview() {
    ScamazonFrontendTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            LafyuuEmailField(
                value = "",
                onValueChange = {},
                placeholder = "Email Address"
            )
            LafyuuPasswordField(
                value = "",
                onValueChange = {},
                placeholder = "Password"
            )
            LafyuuSearchField(
                value = "",
                onValueChange = {},
                placeholder = "Search Product"
            )
            LafyuuTextField(
                value = "",
                onValueChange = {},
                placeholder = "Enter text",
                isError = true,
                errorMessage = "This field is required"
            )
        }
    }
}
