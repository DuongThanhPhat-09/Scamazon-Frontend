package com.example.scamazon_frontend.ui.screens.admin.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.product.ProductDto
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductListScreen(
    viewModel: AdminProductViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateToAddProduct: () -> Unit = {},
    onNavigateToEditProduct: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val productsState by viewModel.productsState.collectAsStateWithLifecycle()
    val deleteState by viewModel.deleteState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<ProductDto?>(null) }

    // Reload products when screen resumes (e.g. after creating/editing a product)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadProducts(search = searchQuery.ifEmpty { null })
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(deleteState) {
        if (deleteState is Resource.Success) {
            viewModel.resetDeleteState()
            viewModel.loadProducts(search = searchQuery.ifEmpty { null })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Products",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddProduct,
                containerColor = PrimaryBlue,
                contentColor = White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Product")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.loadProducts(search = it.ifEmpty { null })
                },
                placeholder = {
                    Text("Search products...", fontFamily = Poppins, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search", tint = TextSecondary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            viewModel.loadProducts()
                        }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = BorderDefault,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                )
            )

            when (productsState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }

                is Resource.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = (productsState as Resource.Error).message ?: "Error",
                                color = StatusError,
                                fontFamily = Poppins
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadProducts() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryBlue,
                                    contentColor = White
                                )
                            ) {
                                Text("Retry", fontFamily = Poppins, color = White)
                            }
                        }
                    }
                }

                is Resource.Success -> {
                    val products = (productsState as Resource.Success).data?.items ?: emptyList()
                    if (products.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No products found",
                                fontFamily = Poppins,
                                color = TextSecondary
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(products) { product ->
                                AdminProductItem(
                                    product = product,
                                    onEdit = { onNavigateToEditProduct(product.slug) },
                                    onDelete = { showDeleteDialog = product }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { product ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = {
                Text("Delete Product", fontFamily = Poppins, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Are you sure you want to delete \"${product.name}\"?",
                    fontFamily = Poppins
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProduct(product.id)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete", color = StatusError, fontFamily = Poppins)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel", color = TextSecondary, fontFamily = Poppins)
                }
            }
        )
    }
}

@Composable
private fun AdminProductItem(
    product: ProductDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            AsyncImage(
                model = product.primaryImage,
                contentDescription = product.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BackgroundLight),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Product Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    Text(
                        text = formatPrice(product.price),
                        fontFamily = Poppins,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    if (product.salePrice != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formatPrice(product.salePrice),
                            fontFamily = Poppins,
                            fontSize = 12.sp,
                            color = StatusError
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Stock: ${product.stockQuantity}",
                    fontFamily = Poppins,
                    fontSize = 11.sp,
                    color = if (product.stockQuantity <= 10) StatusError else TextSecondary
                )
            }

            // Actions
            Column {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = StatusError,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
    return "${formatter.format(price)}d"
}
