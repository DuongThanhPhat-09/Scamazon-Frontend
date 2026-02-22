package com.example.scamazon_frontend.ui.screens.checkout

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.core.utils.formatPrice
import com.example.scamazon_frontend.ui.components.LafyuuOutlinedButton
import com.example.scamazon_frontend.ui.components.LafyuuPrimaryButton
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun OrderSuccessScreen(
    orderCode: String = "",
    totalAmount: Double = 0.0,
    paymentMethod: String = "cod",
    onViewOrder: () -> Unit = {},
    onContinueShopping: () -> Unit = {}
) {
    // Animate the checkmark icon
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(Dimens.ScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Success Icon with animation
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale.value)
                .background(StatusSuccess.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(StatusSuccess, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = "Order Placed Successfully!",
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "Your order has been confirmed and will be processed shortly.",
            style = Typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Order Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (orderCode.isNotEmpty()) {
                    InfoRow(label = "Order Code", value = orderCode)
                }
                if (totalAmount > 0) {
                    InfoRow(label = "Total", value = "${formatPrice(totalAmount)}đ")
                }
                InfoRow(
                    label = "Payment",
                    value = when (paymentMethod.lowercase()) {
                        "vnpay" -> "VNPay"
                        "zalopay" -> "ZaloPay"
                        else -> "Cash on Delivery"
                    }
                )
                InfoRow(label = "Status", value = "Pending")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        LafyuuPrimaryButton(
            text = "View My Orders",
            onClick = onViewOrder
        )

        Spacer(modifier = Modifier.height(12.dp))

        LafyuuOutlinedButton(
            text = "Continue Shopping",
            onClick = onContinueShopping
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = Typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = value,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = TextPrimary
        )
    }
}
