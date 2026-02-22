package com.example.scamazon_frontend.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.ui.theme.*

/**
 * Primary Button - Elegant Vintage Style
 * Used for main actions like "Sign In", "Add to Cart", "Checkout"
 * Uses Burgundy as the primary CTA color
 */
@Composable
fun LafyuuPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.ButtonHeightLarge),
        enabled = enabled,
        shape = LafyuuShapes.ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue,
            contentColor = White,
            disabledContainerColor = ButtonDisabled,
            disabledContentColor = White.copy(alpha = 0.7f)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = text,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = White,
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * Secondary Button - Navy Style
 * Alternative primary button with Navy color
 */
@Composable
fun LafyuuSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.ButtonHeightLarge),
        enabled = enabled,
        shape = LafyuuShapes.ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Navy,
            contentColor = White,
            disabledContainerColor = ButtonDisabled,
            disabledContentColor = White.copy(alpha = 0.7f)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = text,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = White,
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * Outlined Button - Elegant Style
 * Used for secondary actions like "Cancel", "Back"
 */
@Composable
fun LafyuuOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.ButtonHeightLarge),
        enabled = enabled,
        shape = LafyuuShapes.ButtonShape,
        border = BorderStroke(
            1.5.dp,
            if (enabled) PrimaryBlue else ButtonDisabled
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = PrimaryBlue,
            disabledContentColor = ButtonDisabled
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = text,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            letterSpacing = 0.5.sp,
            color = if (enabled) PrimaryBlue else ButtonDisabled
        )
    }
}

/**
 * Text Button - Elegant Style
 * Used for tertiary actions like "Forgot Password", "Skip"
 */
@Composable
fun LafyuuTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = PrimaryBlue
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = textColor,
            letterSpacing = 0.3.sp
        )
    }
}

/**
 * Small Button - Elegant Style
 * Used for small actions like quantity buttons, filter chips
 */
@Composable
fun LafyuuSmallButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(Dimens.ButtonHeightSmall),
        enabled = enabled,
        shape = LafyuuShapes.ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue,
            contentColor = White,
            disabledContainerColor = ButtonDisabled,
            disabledContentColor = White.copy(alpha = 0.7f)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = White
        )
    }
}

/**
 * Gold Accent Button - Premium Style
 * Used for premium/special actions
 */
@Composable
fun LafyuuGoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.ButtonHeightLarge),
        enabled = enabled,
        shape = LafyuuShapes.ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Gold,
            contentColor = PrimaryBlue,
            disabledContainerColor = ButtonDisabled,
            disabledContentColor = DarkBlue.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = text,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = Navy,
            letterSpacing = 0.5.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFAF9F6)
@Composable
fun LafyuuButtonsPreview() {
    ScamazonFrontendTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LafyuuPrimaryButton(text = "Sign In", onClick = {})
            LafyuuSecondaryButton(text = "Continue", onClick = {})
            LafyuuOutlinedButton(text = "Create Account", onClick = {})
            LafyuuGoldButton(text = "Premium Action", onClick = {})
            LafyuuTextButton(text = "Forgot Password?", onClick = {})
            LafyuuSmallButton(text = "Apply", onClick = {})
        }
    }
}
