package com.example.scamazon_frontend.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.ui.components.LafyuuPrimaryButton
import com.example.scamazon_frontend.ui.components.LafyuuTopAppBar
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit = {},
    onOrderSuccess: () -> Unit = {}
) {
    var selectedShipping by remember { mutableStateOf(0) }
    var selectedPayment by remember { mutableStateOf(0) }
    var showOrderConfirmDialog by remember { mutableStateOf(false) }

    val shippingOptions = listOf(
        ShippingOption("Economy", "Estimated 5-7 days", 5.00),
        ShippingOption("Regular", "Estimated 3-5 days", 7.50),
        ShippingOption("Cargo", "Estimated 1-2 days", 15.00)
    )

    val paymentMethods = listOf(
        PaymentMethod("Credit Card", "Visa ending in 4242", Icons.Default.CreditCard),
        PaymentMethod("Debit Card", "Mastercard ending in 5353", Icons.Default.CreditCard),
        PaymentMethod("Cash on Delivery", "Pay when you receive", Icons.Default.LocalShipping)
    )

    val subtotal = 749.41
    val shippingCost = shippingOptions[selectedShipping].price
    val total = subtotal + shippingCost

    if (showOrderConfirmDialog) {
        OrderConfirmDialog(
            total = total,
            onConfirm = {
                showOrderConfirmDialog = false
                onOrderSuccess()
            },
            onDismiss = { showOrderConfirmDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        LafyuuTopAppBar(
            title = "Checkout",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Shipping Address Section
            CheckoutSection(title = "Shipping Address") {
                AddressCard(
                    name = "John Doe",
                    address = "3 Newbridge Court, Chino Hills, CA 91709",
                    phone = "+1 (555) 234-5678"
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = LafyuuShapes.ButtonShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add New Address",
                        fontFamily = Poppins,
                        fontSize = 13.sp
                    )
                }
            }

            // Shipping Method Section
            CheckoutSection(title = "Shipping Method") {
                shippingOptions.forEachIndexed { index, option ->
                    ShippingOptionItem(
                        option = option,
                        isSelected = selectedShipping == index,
                        onClick = { selectedShipping = index }
                    )
                    if (index < shippingOptions.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Payment Method Section
            CheckoutSection(title = "Payment Method") {
                paymentMethods.forEachIndexed { index, method ->
                    PaymentMethodItem(
                        method = method,
                        isSelected = selectedPayment == index,
                        onClick = { selectedPayment = index }
                    )
                    if (index < paymentMethods.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Order Summary Section
            CheckoutSection(title = "Order Summary") {
                OrderSummaryRow(label = "Items (3)", value = "$${"%.2f".format(subtotal)}")
                Spacer(modifier = Modifier.height(8.dp))
                OrderSummaryRow(label = "Shipping (${shippingOptions[selectedShipping].name})", value = "$${"%.2f".format(shippingCost)}")
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "$${"%.2f".format(total)}",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Place Order Button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = White
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                LafyuuPrimaryButton(
                    text = "Place Order • $${"%.2f".format(total)}",
                    onClick = { showOrderConfirmDialog = true }
                )
            }
        }
    }
}

@Composable
private fun CheckoutSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = TextPrimary
        )
        content()
    }
}

@Composable
private fun AddressCard(
    name: String,
    address: String,
    phone: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = LafyuuShapes.CardShape,
        colors = CardDefaults.cardColors(containerColor = BackgroundLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Text(
                    text = address,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = phone,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun ShippingOptionItem(
    option: ShippingOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) PrimaryBlue else BorderDefault
    val bgColor = if (isSelected) PrimaryBlueSoft else White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, borderColor, LafyuuShapes.CardShape)
            .background(bgColor, LafyuuShapes.CardShape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isSelected) PrimaryBlue else TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.name,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Text(
                text = option.duration,
                fontFamily = Poppins,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
        Text(
            text = "$${"%.2f".format(option.price)}",
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = PrimaryBlue
        )
    }
}

@Composable
private fun PaymentMethodItem(
    method: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) PrimaryBlue else BorderDefault
    val bgColor = if (isSelected) PrimaryBlueSoft else White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, borderColor, LafyuuShapes.CardShape)
            .background(bgColor, LafyuuShapes.CardShape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(PrimaryBlueSoft, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = method.icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = method.name,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Text(
                text = method.detail,
                fontFamily = Poppins,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
        Icon(
            imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isSelected) PrimaryBlue else TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun OrderSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = TextSecondary
        )
        Text(
            text = value,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = TextPrimary
        )
    }
}

@Composable
private fun OrderConfirmDialog(
    total: Double,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = StatusSuccess,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "Confirm Order",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Text(
                text = "Place order for $${"%.2f".format(total)}?",
                fontFamily = Poppins,
                color = TextSecondary,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = LafyuuShapes.ButtonShape
            ) {
                Text("Place Order", fontFamily = Poppins)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontFamily = Poppins, color = TextSecondary)
            }
        },
        containerColor = White
    )
}

private data class ShippingOption(
    val name: String,
    val duration: String,
    val price: Double
)

private data class PaymentMethod(
    val name: String,
    val detail: String,
    val icon: ImageVector
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CheckoutScreenPreview() {
    ScamazonFrontendTheme {
        CheckoutScreen()
    }
}
