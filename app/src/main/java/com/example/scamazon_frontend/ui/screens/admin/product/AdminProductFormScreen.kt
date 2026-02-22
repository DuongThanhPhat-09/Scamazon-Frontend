package com.example.scamazon_frontend.ui.screens.admin.product

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.admin.*
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductFormScreen(
    productId: Int? = null,
    viewModel: AdminProductViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isEdit = productId != null && productId > 0
    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()
    val brandsState by viewModel.brandsState.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()
    val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()
    val productDetailState by viewModel.productDetailState.collectAsStateWithLifecycle()

    // Form fields
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var salePrice by remember { mutableStateOf("") }
    var stockQuantity by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var detailDescription by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var selectedBrandId by remember { mutableStateOf<Int?>(null) }
    var isFeatured by remember { mutableStateOf(false) }
    var imageUrls by remember { mutableStateOf(listOf<ProductImageRequest>()) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var brandExpanded by remember { mutableStateOf(false) }

    // Image picker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadImage(context, it) }
    }

    // Load product detail for editing
    LaunchedEffect(productId) {
        if (isEdit && productId != null) {
            viewModel.loadProductDetail(productId.toString())
        }
    }

    // Populate form when product detail loads
    LaunchedEffect(productDetailState) {
        if (productDetailState is Resource.Success) {
            val product = (productDetailState as Resource.Success).data!!
            name = product.name
            price = product.price.toString()
            salePrice = product.salePrice?.toString() ?: ""
            stockQuantity = product.stockQuantity?.toString() ?: ""
            description = product.description ?: ""
            detailDescription = product.detailDescription ?: ""
            selectedCategoryId = product.category?.id
            selectedBrandId = product.brand?.id
            isFeatured = product.isFeatured ?: false
            imageUrls = product.images.map {
                ProductImageRequest(
                    url = it.imageUrl,
                    isPrimary = it.isPrimary,
                    sortOrder = it.sortOrder
                )
            }
        }
    }

    // Handle upload success
    LaunchedEffect(uploadState) {
        if (uploadState is Resource.Success) {
            val uploadData = (uploadState as Resource.Success).data!!
            imageUrls = imageUrls + ProductImageRequest(
                url = uploadData.url,
                isPrimary = imageUrls.isEmpty(),
                sortOrder = imageUrls.size
            )
            viewModel.resetUploadState()
        }
    }

    // Handle save success
    LaunchedEffect(saveState) {
        if (saveState is Resource.Success) {
            Toast.makeText(context, if (isEdit) "Product updated!" else "Product created!", Toast.LENGTH_SHORT).show()
            viewModel.resetSaveState()
            onNavigateBack()
        } else if (saveState is Resource.Error) {
            Toast.makeText(context, (saveState as Resource.Error).message, Toast.LENGTH_SHORT).show()
            viewModel.resetSaveState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEdit) "Edit Product" else "Add Product",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name *", fontFamily = Poppins) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Price Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price *", fontFamily = Poppins) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = salePrice,
                    onValueChange = { salePrice = it },
                    label = { Text("Sale Price", fontFamily = Poppins) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            // Stock
            OutlinedTextField(
                value = stockQuantity,
                onValueChange = { stockQuantity = it },
                label = { Text("Stock Quantity", fontFamily = Poppins) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                val categories = (categoriesState as? Resource.Success)?.data ?: emptyList()
                val selectedCategory = categories.find { it.id == selectedCategoryId }
                OutlinedTextField(
                    value = selectedCategory?.name ?: "Select Category",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category", fontFamily = Poppins) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name, fontFamily = Poppins) },
                            onClick = {
                                selectedCategoryId = category.id
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // Brand Dropdown
            ExposedDropdownMenuBox(
                expanded = brandExpanded,
                onExpandedChange = { brandExpanded = !brandExpanded }
            ) {
                val brands = (brandsState as? Resource.Success)?.data ?: emptyList()
                val selectedBrand = brands.find { it.id == selectedBrandId }
                OutlinedTextField(
                    value = selectedBrand?.name ?: "Select Brand",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Brand", fontFamily = Poppins) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = brandExpanded,
                    onDismissRequest = { brandExpanded = false }
                ) {
                    brands.forEach { brand ->
                        DropdownMenuItem(
                            text = { Text(brand.name, fontFamily = Poppins) },
                            onClick = {
                                selectedBrandId = brand.id
                                brandExpanded = false
                            }
                        )
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description", fontFamily = Poppins) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Detail Description
            OutlinedTextField(
                value = detailDescription,
                onValueChange = { detailDescription = it },
                label = { Text("Detail Description", fontFamily = Poppins) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Featured toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Featured Product",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Switch(
                    checked = isFeatured,
                    onCheckedChange = { isFeatured = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = PrimaryBlue)
                )
            }

            // Images Section
            Text(
                text = "Product Images",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = TextPrimary
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(imageUrls) { image ->
                    Box(
                        modifier = Modifier.size(80.dp)
                    ) {
                        AsyncImage(
                            model = image.url,
                            contentDescription = "Product image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (image.isPrimary) 2.dp else 1.dp,
                                    color = if (image.isPrimary) PrimaryBlue else BorderDefault,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = {
                                imageUrls = imageUrls.filter { it != image }
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Remove",
                                tint = StatusError,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Add Image Button
                item {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, BorderDefault, RoundedCornerShape(8.dp))
                            .background(BackgroundLight)
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uploadState is Resource.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = PrimaryBlue
                            )
                        } else {
                            Icon(
                                Icons.Filled.AddPhotoAlternate,
                                contentDescription = "Add Image",
                                tint = TextSecondary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = {
                    if (name.isBlank() || price.isBlank()) {
                        Toast.makeText(context, "Name and Price are required", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (isEdit && productId != null) {
                        viewModel.updateProduct(
                            productId,
                            UpdateProductRequest(
                                name = name,
                                price = price.toDoubleOrNull(),
                                salePrice = salePrice.toDoubleOrNull(),
                                stockQuantity = stockQuantity.toIntOrNull(),
                                description = description.ifBlank { null },
                                detailDescription = detailDescription.ifBlank { null },
                                categoryId = selectedCategoryId,
                                brandId = selectedBrandId,
                                isFeatured = isFeatured,
                                images = imageUrls.ifEmpty { null }
                            )
                        )
                    } else {
                        viewModel.createProduct(
                            CreateProductRequest(
                                name = name,
                                price = price.toDoubleOrNull() ?: 0.0,
                                salePrice = salePrice.toDoubleOrNull(),
                                stockQuantity = stockQuantity.toIntOrNull() ?: 0,
                                description = description.ifBlank { null },
                                detailDescription = detailDescription.ifBlank { null },
                                categoryId = selectedCategoryId,
                                brandId = selectedBrandId,
                                isFeatured = isFeatured,
                                images = imageUrls.ifEmpty { null }
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = saveState !is Resource.Loading
            ) {
                if (saveState is Resource.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = White
                    )
                } else {
                    Text(
                        text = if (isEdit) "Update Product" else "Create Product",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
