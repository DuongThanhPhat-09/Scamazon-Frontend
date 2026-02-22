package com.example.scamazon_frontend.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateBack: () -> Unit = {},
    onOrderSuccess: (String) -> Unit = {}
) {
    val shippingName by viewModel.shippingName.collectAsStateWithLifecycle()
    val shippingPhone by viewModel.shippingPhone.collectAsStateWithLifecycle()
    val shippingAddress by viewModel.shippingAddress.collectAsStateWithLifecycle()
    val shippingCity by viewModel.shippingCity.collectAsStateWithLifecycle()
    val shippingDistrict by viewModel.shippingDistrict.collectAsStateWithLifecycle()
    val shippingWard by viewModel.shippingWard.collectAsStateWithLifecycle()
    val paymentMethod by viewModel.paymentMethod.collectAsStateWithLifecycle()
    val note by viewModel.note.collectAsStateWithLifecycle()
    val orderState by viewModel.orderState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Handle order result
    LaunchedEffect(orderState) {
        when (orderState) {
            is Resource.Success -> {
                val orderId = orderState?.data?.orderId?.toString() ?: ""
                viewModel.resetOrderState()
                onOrderSuccess(orderId)
            }
            is Resource.Error -> {
                snackbarHostState.showSnackbar(orderState?.message ?: "Order failed")
                viewModel.resetOrderState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundWhite)
        ) {
            LafyuuTopAppBar(title = "Checkout", onBackClick = onNavigateBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(Dimens.ScreenPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Shipping Information Section
                Text(
                    text = "Shipping Information",
                    style = Typography.titleLarge,
                    color = TextPrimary
                )

                LafyuuTextField(
                    value = shippingName,
                    onValueChange = { viewModel.onShippingNameChange(it) },
                    placeholder = "Full Name *",
                    leadingIcon = Icons.Default.Person
                )

                LafyuuTextField(
                    value = shippingPhone,
                    onValueChange = { viewModel.onShippingPhoneChange(it) },
                    placeholder = "Phone Number *",
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone
                )

                LafyuuTextField(
                    value = shippingAddress,
                    onValueChange = { viewModel.onShippingAddressChange(it) },
                    placeholder = "Shipping Address *",
                    leadingIcon = Icons.Default.LocationOn
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LafyuuTextField(
                        value = shippingCity,
                        onValueChange = { viewModel.onShippingCityChange(it) },
                        placeholder = "City",
                        modifier = Modifier.weight(1f)
                    )
                    LafyuuTextField(
                        value = shippingDistrict,
                        onValueChange = { viewModel.onShippingDistrictChange(it) },
                        placeholder = "District",
                        modifier = Modifier.weight(1f)
                    )
                }

                LafyuuTextField(
                    value = shippingWard,
                    onValueChange = { viewModel.onShippingWardChange(it) },
                    placeholder = "Ward"
                )

                HorizontalDivider(color = BorderLight)

                // Payment Method Section
                Text(
                    text = "Payment Method",
                    style = Typography.titleLarge,
                    color = TextPrimary
                )

                val paymentOptions = listOf(
                    "cod" to "Cash on Delivery (COD)",
                    "vnpay" to "VNPay"
                )

                paymentOptions.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onPaymentMethodChange(value) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = paymentMethod == value,
                            onClick = { viewModel.onPaymentMethodChange(value) },
                            colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, style = Typography.bodyLarge, color = TextPrimary)
                    }
                }

                HorizontalDivider(color = BorderLight)

                // Note
                Text(
                    text = "Order Note (optional)",
                    style = Typography.titleMedium,
                    color = TextPrimary
                )

                LafyuuTextField(
                    value = note,
                    onValueChange = { viewModel.onNoteChange(it) },
                    placeholder = "Any special instructions...",
                    singleLine = false,
                    imeAction = ImeAction.Done
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Place Order Button
            val isOrdering = orderState is Resource.Loading
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = White
            ) {
                LafyuuPrimaryButton(
                    text = if (isOrdering) "Placing Order..." else "Place Order",
                    onClick = { viewModel.placeOrder() },
                    enabled = !isOrdering,
                    modifier = Modifier.padding(Dimens.ScreenPadding)
                )
            }
        }
    }
}
