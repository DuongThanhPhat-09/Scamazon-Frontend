package com.example.scamazon_frontend.ui.screens.admin.category

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.admin.*
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.theme.*

/**
 * Shared form screen for both Category and Brand add/edit operations.
 *
 * @param isBrand true = Brand form, false = Category form
 * @param editId non-null means edit mode
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoryFormScreen(
    isBrand: Boolean = false,
    editId: Int? = null,
    viewModel: AdminCategoryViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isEdit = editId != null && editId > 0
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()
    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()
    val brandsState by viewModel.brandsState.collectAsStateWithLifecycle()

    val title = when {
        isBrand && isEdit -> "Edit Brand"
        isBrand -> "Add Brand"
        isEdit -> "Edit Category"
        else -> "Add Category"
    }

    // Form fields
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") } // For category
    var logoUrl by remember { mutableStateOf("") } // For brand
    var selectedParentId by remember { mutableStateOf<Int?>(null) }
    var parentExpanded by remember { mutableStateOf(false) }

    // Pre-fill for edit mode
    LaunchedEffect(editId, categoriesState, brandsState) {
        if (isEdit) {
            if (!isBrand) {
                val categories = (categoriesState as? Resource.Success)?.data ?: emptyList()
                val category = categories.find { it.id == editId }
                if (category != null) {
                    name = category.name
                    description = category.description ?: ""
                    imageUrl = category.imageUrl ?: ""
                    selectedParentId = category.parentId
                }
            } else {
                val brands = (brandsState as? Resource.Success)?.data ?: emptyList()
                val brand = brands.find { it.id == editId }
                if (brand != null) {
                    name = brand.name
                    description = brand.description ?: ""
                    logoUrl = brand.logoUrl ?: ""
                }
            }
        }
    }

    // Handle save result
    LaunchedEffect(saveState) {
        when (saveState) {
            is Resource.Success -> {
                Toast.makeText(
                    context,
                    if (isEdit) "$title updated!" else "$title created!",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetSaveState()
                onNavigateBack()
            }
            is Resource.Error -> {
                Toast.makeText(context, (saveState as Resource.Error).message, Toast.LENGTH_SHORT).show()
                viewModel.resetSaveState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
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
                label = { Text("Name *", fontFamily = Poppins) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description", fontFamily = Poppins) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )

            if (isBrand) {
                // Logo URL for Brand
                OutlinedTextField(
                    value = logoUrl,
                    onValueChange = { logoUrl = it },
                    label = { Text("Logo URL", fontFamily = Poppins) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            } else {
                // Image URL for Category
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL", fontFamily = Poppins) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Parent Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = parentExpanded,
                    onExpandedChange = { parentExpanded = !parentExpanded }
                ) {
                    val categories = (categoriesState as? Resource.Success)?.data ?: emptyList()
                    val parentCategory = categories.find { it.id == selectedParentId }
                    OutlinedTextField(
                        value = parentCategory?.name ?: "None (Top Level)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Parent Category", fontFamily = Poppins) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = parentExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = parentExpanded,
                        onDismissRequest = { parentExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("None (Top Level)", fontFamily = Poppins) },
                            onClick = {
                                selectedParentId = null
                                parentExpanded = false
                            }
                        )
                        categories
                            .filter { it.id != editId } // Can't be parent of itself
                            .forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name, fontFamily = Poppins) },
                                    onClick = {
                                        selectedParentId = category.id
                                        parentExpanded = false
                                    }
                                )
                            }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = {
                    if (name.isBlank()) {
                        Toast.makeText(context, "Name is required", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (isBrand) {
                        if (isEdit && editId != null) {
                            viewModel.updateBrand(
                                editId,
                                UpdateBrandRequest(
                                    name = name,
                                    description = description.ifBlank { null },
                                    logoUrl = logoUrl.ifBlank { null }
                                )
                            )
                        } else {
                            viewModel.createBrand(
                                CreateBrandRequest(
                                    name = name,
                                    description = description.ifBlank { null },
                                    logoUrl = logoUrl.ifBlank { null }
                                )
                            )
                        }
                    } else {
                        if (isEdit && editId != null) {
                            viewModel.updateCategory(
                                editId,
                                UpdateCategoryRequest(
                                    name = name,
                                    description = description.ifBlank { null },
                                    imageUrl = imageUrl.ifBlank { null },
                                    parentId = selectedParentId
                                )
                            )
                        } else {
                            viewModel.createCategory(
                                CreateCategoryRequest(
                                    name = name,
                                    description = description.ifBlank { null },
                                    imageUrl = imageUrl.ifBlank { null },
                                    parentId = selectedParentId
                                )
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = White),
                enabled = saveState !is Resource.Loading
            ) {
                if (saveState is Resource.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = White)
                } else {
                    Text(
                        text = if (isEdit) "Update" else "Create",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
