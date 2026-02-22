package com.example.scamazon_frontend.ui.screens.admin.category

import android.widget.Toast
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.admin.BrandDto
import com.example.scamazon_frontend.data.models.category.CategoryDto
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoryListScreen(
    viewModel: AdminCategoryViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateToAddCategory: () -> Unit = {},
    onNavigateToEditCategory: (Int) -> Unit = {},
    onNavigateToAddBrand: () -> Unit = {},
    onNavigateToEditBrand: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()
    val brandsState by viewModel.brandsState.collectAsStateWithLifecycle()
    val deleteState by viewModel.deleteState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf<Pair<String, Int>?>(null) } // type, id

    LaunchedEffect(deleteState) {
        when (deleteState) {
            is Resource.Success -> {
                Toast.makeText(context, "Deleted successfully!", Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteState()
                viewModel.loadCategories()
                viewModel.loadBrands()
            }
            is Resource.Error -> {
                Toast.makeText(context, (deleteState as Resource.Error).message, Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Categories & Brands",
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
                onClick = {
                    if (selectedTab == 0) onNavigateToAddCategory()
                    else onNavigateToAddBrand()
                },
                containerColor = PrimaryBlue,
                contentColor = White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BackgroundWhite,
                contentColor = PrimaryBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Categories",
                            fontFamily = Poppins,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Brands",
                            fontFamily = Poppins,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            when (selectedTab) {
                0 -> CategoriesTab(
                    categoriesState = categoriesState,
                    onEdit = { onNavigateToEditCategory(it) },
                    onDelete = { showDeleteDialog = Pair("category", it) },
                    onRetry = { viewModel.loadCategories() }
                )
                1 -> BrandsTab(
                    brandsState = brandsState,
                    onEdit = { onNavigateToEditBrand(it) },
                    onDelete = { showDeleteDialog = Pair("brand", it) },
                    onRetry = { viewModel.loadBrands() }
                )
            }
        }
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { (type, id) ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = {
                Text(
                    "Delete ${type.replaceFirstChar { it.uppercase() }}",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Are you sure you want to delete this $type?", fontFamily = Poppins)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (type == "category") viewModel.deleteCategory(id)
                        else viewModel.deleteBrand(id)
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
private fun CategoriesTab(
    categoriesState: Resource<List<CategoryDto>>,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onRetry: () -> Unit
) {
    when (categoriesState) {
        is Resource.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }
        is Resource.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(categoriesState.message ?: "Error", color = StatusError, fontFamily = Poppins)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)) {
                        Text("Retry", fontFamily = Poppins)
                    }
                }
            }
        }
        is Resource.Success -> {
            val categories = categoriesState.data ?: emptyList()
            if (categories.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No categories", fontFamily = Poppins, color = TextSecondary)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategoryItem(category, onEdit, onDelete)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: CategoryDto,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            if (category.imageUrl != null) {
                AsyncImage(
                    model = category.imageUrl,
                    contentDescription = category.name,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundLight),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (category.description != null) {
                    Text(
                        text = category.description,
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(onClick = { onEdit(category.id) }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = { onDelete(category.id) }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = StatusError, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun BrandsTab(
    brandsState: Resource<List<BrandDto>>,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onRetry: () -> Unit
) {
    when (brandsState) {
        is Resource.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }
        is Resource.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(brandsState.message ?: "Error", color = StatusError, fontFamily = Poppins)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)) {
                        Text("Retry", fontFamily = Poppins)
                    }
                }
            }
        }
        is Resource.Success -> {
            val brands = brandsState.data ?: emptyList()
            if (brands.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No brands", fontFamily = Poppins, color = TextSecondary)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(brands) { brand ->
                        BrandItem(brand, onEdit, onDelete)
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandItem(
    brand: BrandDto,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            if (brand.logoUrl != null) {
                AsyncImage(
                    model = brand.logoUrl,
                    contentDescription = brand.name,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundLight),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = brand.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (brand.description != null) {
                    Text(
                        text = brand.description,
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(onClick = { onEdit(brand.id) }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = { onDelete(brand.id) }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = StatusError, modifier = Modifier.size(20.dp))
            }
        }
    }
}
