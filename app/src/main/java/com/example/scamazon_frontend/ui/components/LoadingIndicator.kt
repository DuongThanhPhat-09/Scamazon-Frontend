package com.example.scamazon_frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.scamazon_frontend.ui.theme.*

/**
 * Full Screen Loading - Lafyuu Style
 */
@Composable
fun FullScreenLoading(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Cream),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = PrimaryBlue,
            strokeWidth = 3.dp
        )
    }
}

/**
 * Loading Dialog - Lafyuu Style
 */
@Composable
fun LoadingDialog(
    message: String = "Loading...",
    onDismiss: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(White)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(
                    color = PrimaryBlue,
                    strokeWidth = 3.dp
                )

                Text(
                    text = message,
                    style = Typography.bodyLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Inline Loading Indicator - Lafyuu Style
 */
@Composable
fun InlineLoading(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = PrimaryBlue,
            strokeWidth = 2.dp
        )

        message?.let {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = it,
                style = Typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

/**
 * Button Loading Indicator - Lafyuu Style
 */
@Composable
fun ButtonLoading(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        modifier = modifier.size(20.dp),
        color = White,
        strokeWidth = 2.dp
    )
}

/**
 * Empty State - Lafyuu Style
 */
@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = Typography.headlineMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = Typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        action?.let {
            Spacer(modifier = Modifier.height(24.dp))
            it()
        }
    }
}

/**
 * Error State - Lafyuu Style
 */
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Oops!",
            style = Typography.headlineMedium,
            color = SecondaryRed,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = Typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        LafyuuPrimaryButton(
            text = "Try Again",
            onClick = onRetry,
            modifier = Modifier.width(150.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingStatesPreview() {
    ScamazonFrontendTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            InlineLoading(message = "Loading products...")

            EmptyState(
                title = "Your Cart is Empty",
                message = "Looks like you haven't added anything to your cart yet"
            ) {
                LafyuuPrimaryButton(
                    text = "Start Shopping",
                    onClick = {},
                    modifier = Modifier.width(200.dp)
                )
            }

            ErrorState(
                message = "Something went wrong. Please try again.",
                onRetry = {}
            )
        }
    }
}
