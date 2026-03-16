package com.example.scamazon_frontend.ui.screens.cart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.core.utils.formatPrice
import com.example.scamazon_frontend.data.models.cart.CartItemDto
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateToCheckout: () -> Unit = {},
    onNavigateToProductDetail: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val cartState by viewModel.cartState.collectAsStateWithLifecycle()
    val operationMessage by viewModel.operationMessage.collectAsStateWithLifecycle()
    val selectedItems by viewModel.selectedItems.collectAsStateWithLifecycle()
    val isSelectionMode by viewModel.isSelectionMode.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showClearConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(operationMessage) {
        operationMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Xoá tất cả?", fontFamily = Poppins, fontWeight = FontWeight.Bold) },
            text = { Text("Toàn bộ sản phẩm trong giỏ sẽ bị xoá.", fontFamily = Poppins, color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { showClearConfirm = false; viewModel.clearCart() }) {
                    Text("Xoá", color = StatusError, fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Huỷ", fontFamily = Poppins)
                }
            }
        )
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundWhite)
        ) {
            if (isSelectionMode) {
                SelectionTopBar(
                    selectedCount = selectedItems.size,
                    totalCount = (cartState as? Resource.Success)?.data?.items?.size ?: 0,
                    onSelectAll = {
                        val allIds = (cartState as? Resource.Success)?.data?.items?.map { it.id } ?: emptyList()
                        viewModel.selectAll(allIds)
                    },
                    onDelete = { viewModel.removeSelectedItems() },
                    onClose = { viewModel.exitSelectionMode() }
                )
            } else {
                LafyuuTopAppBar(
                    title = "Your Cart",
                    onBackClick = onNavigateBack,
                    actions = {
                        val hasItems = (cartState as? Resource.Success)?.data?.items?.isNotEmpty() == true
                        if (hasItems) {
                            IconButton(onClick = { showClearConfirm = true }) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = "Clear cart", tint = StatusError)
                            }
                        }
                    }
                )
            }

            when (cartState) {
                is Resource.Loading -> {
                    Box(Modifier.fillMaxSize().weight(1f), Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
                is Resource.Error -> {
                    Box(Modifier.weight(1f).fillMaxWidth(), Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(cartState.message ?: "Error loading cart", style = Typography.bodyLarge, color = StatusError)
                            Spacer(Modifier.height(16.dp))
                            LafyuuPrimaryButton("Retry", onClick = { viewModel.fetchCart() }, modifier = Modifier.width(200.dp))
                        }
                    }
                }
                is Resource.Success -> {
                    val cart = cartState.data!!
                    if (cart.items.isEmpty()) {
                        Box(Modifier.weight(1f).fillMaxWidth(), Alignment.Center) {
                            EmptyState(
                                title = "Your Cart is Empty",
                                message = "Looks like you haven't added anything yet"
                            ) {
                                LafyuuPrimaryButton("Start Shopping", onClick = onNavigateBack, modifier = Modifier.width(200.dp))
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(cart.items, key = { it.id }) { item ->
                                SwipeToRevealCartItem(
                                    item = item,
                                    isSelected = item.id in selectedItems,
                                    isSelectionMode = isSelectionMode,
                                    onQuantityIncrease = {
                                        if (item.quantity < item.stockQuantity)
                                            viewModel.updateQuantity(item.id, item.quantity + 1)
                                    },
                                    onQuantityDecrease = {
                                        if (item.quantity > 1)
                                            viewModel.updateQuantity(item.id, item.quantity - 1)
                                    },
                                    onRemove = { viewModel.removeItem(item.id) },
                                    onLongPress = { viewModel.enterSelectionMode(item.id) },
                                    onTap = {
                                        if (isSelectionMode) viewModel.toggleSelection(item.id)
                                        else onNavigateToProductDetail(item.productId.toString())
                                    }
                                )
                                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                            }
                        }

                        CartBottomSection(
                            subtotal = cart.subtotal,
                            itemCount = cart.totalItems,
                            onCheckout = onNavigateToCheckout
                        )
                    }
                }
            }
        }
    }
}

// ── Selection top bar ──────────────────────────────────────────────────────────

@Composable
private fun SelectionTopBar(
    selectedCount: Int,
    totalCount: Int,
    onSelectAll: () -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit
) {
    Surface(color = PrimaryBlue, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.RadioButtonUnchecked, contentDescription = "Cancel", tint = White)
            }
            Text(
                text = "$selectedCount đã chọn",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                color = White,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onSelectAll) {
                Text("Chọn tất cả ($totalCount)", color = White, fontFamily = Poppins, fontSize = 13.sp)
            }
            IconButton(onClick = onDelete, enabled = selectedCount > 0) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete selected",
                    tint = if (selectedCount > 0) White else White.copy(alpha = 0.4f)
                )
            }
        }
    }
}

// ── Swipe-to-reveal item ───────────────────────────────────────────────────────

private val REVEAL_WIDTH = 80.dp

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun SwipeToRevealCartItem(
    item: CartItemDto,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit,
    onRemove: () -> Unit,
    onLongPress: () -> Unit,
    onTap: () -> Unit
) {
    val density = LocalDensity.current
    val revealPx = with(density) { REVEAL_WIDTH.toPx() }
    val scope = rememberCoroutineScope()

    // Animatable offset: 0f = settled, -revealPx = revealed
    val offsetX = remember { Animatable(0f) }

    // Collapse when entering selection mode
    LaunchedEffect(isSelectionMode) {
        if (isSelectionMode) offsetX.animateTo(0f, tween(200))
    }

    val itemBg = if (isSelected) PrimaryBlue.copy(alpha = 0.07f) else White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clipToBounds()
    ) {
        // ── Delete button (behind, shown when swiped left) ────────────────
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(REVEAL_WIDTH)
                .fillMaxHeight()
                .background(StatusError)
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(22.dp)
                )
                Text("Xoá", color = White, fontFamily = Poppins, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }

        // ── Item content (slides left on drag) ────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .then(
                    if (!isSelectionMode) Modifier.draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            scope.launch {
                                val newOffset = (offsetX.value + delta).coerceIn(-revealPx, 0f)
                                offsetX.snapTo(newOffset)
                            }
                        },
                        onDragStopped = { velocity ->
                            scope.launch {
                                val target = when {
                                    velocity < -300f -> -revealPx   // vuốt nhanh sang trái → reveal
                                    velocity > 300f -> 0f            // vuốt nhanh sang phải → đóng
                                    offsetX.value < -revealPx * 0.4f -> -revealPx  // qua ngưỡng → reveal
                                    else -> 0f                       // chưa qua ngưỡng → đóng lại
                                }
                                offsetX.animateTo(target, tween(250))
                            }
                        }
                    ) else Modifier
                )
                .background(itemBg)
                .combinedClickable(onClick = onTap, onLongClick = onLongPress)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox in selection mode
            if (isSelectionMode) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isSelected) PrimaryBlue else TextHint,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Image
            if (!item.productImage.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(item.productImage).crossfade(true).build(),
                    contentDescription = item.productName,
                    modifier = Modifier
                        .size(Dimens.ProductImageSize)
                        .clip(LafyuuShapes.ImageShape)
                        .background(BackgroundLight),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(Dimens.ProductImageSize)
                        .background(BackgroundLight, LafyuuShapes.ImageShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Img", style = Typography.bodySmall, color = TextHint)
                }
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    style = Typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 2
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${formatPrice(item.salePrice ?: item.price)}đ",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = PrimaryBlue
                    )
                    if (!isSelectionMode) {
                        CompactQuantitySelector(
                            quantity = item.quantity,
                            onIncrease = onQuantityIncrease,
                            onDecrease = onQuantityDecrease
                        )
                    }
                }
            }
        }
    }
}

// ── Bottom section ─────────────────────────────────────────────────────────────

@Composable
private fun CartBottomSection(subtotal: Double, itemCount: Int, onCheckout: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp, color = White) {
        Column(modifier = Modifier.fillMaxWidth().padding(Dimens.ScreenPadding)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Items ($itemCount)", style = Typography.bodyLarge, color = TextSecondary)
                Text("${formatPrice(subtotal)}đ", style = Typography.bodyLarge, color = TextPrimary)
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Shipping", style = Typography.bodyLarge, color = TextSecondary)
                Text("40.000đ", style = Typography.bodyLarge, color = TextPrimary)
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = BorderLight)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Price", style = Typography.titleLarge, color = TextPrimary)
                Text("${formatPrice(subtotal + 40000.0)}đ", style = Typography.titleLarge, color = PrimaryBlue)
            }
            Spacer(Modifier.height(16.dp))
            LafyuuPrimaryButton("Check Out", onClick = onCheckout)
        }
    }
}
