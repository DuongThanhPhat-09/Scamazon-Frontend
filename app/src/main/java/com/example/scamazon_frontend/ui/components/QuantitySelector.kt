package com.example.scamazon_frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.ui.theme.*

/**
 * Quantity Selector - Lafyuu Style
 * Used in Cart and Product Detail screens
 */
@Composable
fun QuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
    minQuantity: Int = 1,
    maxQuantity: Int = 99
) {
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = BorderLight,
                shape = RoundedCornerShape(5.dp)
            )
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Decrease Button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(topStart = 5.dp, bottomStart = 5.dp))
                .background(if (quantity > minQuantity) BackgroundLight else BackgroundLight.copy(alpha = 0.5f))
                .clickable(enabled = quantity > minQuantity) { onDecrease() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease",
                tint = if (quantity > minQuantity) TextSecondary else TextHint,
                modifier = Modifier.size(16.dp)
            )
        }

        // Quantity Text
        Box(
            modifier = Modifier
                .width(50.dp)
                .fillMaxHeight()
                .background(BackgroundWhite),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = quantity.toString(),
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        }

        // Increase Button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp))
                .background(if (quantity < maxQuantity) BackgroundLight else BackgroundLight.copy(alpha = 0.5f))
                .clickable(enabled = quantity < maxQuantity) { onIncrease() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                tint = if (quantity < maxQuantity) TextSecondary else TextHint,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Compact Quantity Selector - Lafyuu Style
 * Smaller version for cart items
 */
@Composable
fun CompactQuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
    minQuantity: Int = 1,
    maxQuantity: Int = 99
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Decrease Button
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (quantity > minQuantity) BackgroundLight else BackgroundLight.copy(alpha = 0.5f))
                .clickable(enabled = quantity > minQuantity) { onDecrease() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease",
                tint = if (quantity > minQuantity) TextSecondary else TextHint,
                modifier = Modifier.size(12.dp)
            )
        }

        // Quantity Text
        Text(
            text = quantity.toString(),
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = TextPrimary,
            modifier = Modifier.width(24.dp),
            textAlign = TextAlign.Center
        )

        // Increase Button
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (quantity < maxQuantity) BackgroundLight else BackgroundLight.copy(alpha = 0.5f))
                .clickable(enabled = quantity < maxQuantity) { onIncrease() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                tint = if (quantity < maxQuantity) TextSecondary else TextHint,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuantitySelectorsPreview() {
    ScamazonFrontendTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuantitySelector(
                quantity = 2,
                onIncrease = {},
                onDecrease = {}
            )

            CompactQuantitySelector(
                quantity = 5,
                onIncrease = {},
                onDecrease = {}
            )
        }
    }
}
