package com.example.scamazon_frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.ui.theme.*

/**
 * Category Card - Lafyuu Style
 * Circular icon with category name below
 */
@Composable
fun CategoryCard(
    name: String,
    icon: ImageVector,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    Column(
        modifier = modifier
            .width(70.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular Icon Container
        Box(
            modifier = Modifier
                .size(Dimens.CategoryCardSize)
                .clip(CircleShape)
                .background(if (isSelected) PrimaryBlue else PrimaryBlueSoft)
                .border(
                    width = 1.dp,
                    color = if (isSelected) PrimaryBlue else BorderLight,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = if (isSelected) White else PrimaryBlue,
                modifier = Modifier.size(Dimens.CategoryIconSize)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category Name
        Text(
            text = name,
            fontFamily = Poppins,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            color = if (isSelected) PrimaryBlue else TextSecondary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Category Chip - Lafyuu Style
 * Horizontal chip for filter/sort
 */
@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(LafyuuShapes.ChipShape)
            .background(if (isSelected) PrimaryBlue else White)
            .border(
                width = 1.dp,
                color = if (isSelected) PrimaryBlue else BorderLight,
                shape = LafyuuShapes.ChipShape
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = name,
            fontFamily = Poppins,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 12.sp,
            color = if (isSelected) White else TextSecondary
        )
    }
}

/**
 * Sale Banner Card - Lafyuu Style
 * Used for promotional banners
 */
@Composable
fun SaleBannerCard(
    title: String,
    subtitle: String,
    discount: String,
    backgroundColor: androidx.compose.ui.graphics.Color = PrimaryBlue,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.BannerHeight)
            .clip(LafyuuShapes.ImageShape)
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Discount Badge
            Box(
                modifier = Modifier
                    .background(
                        color = BackgroundWhite,
                        shape = LafyuuShapes.ButtonShape
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = discount,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = backgroundColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryCardsPreview() {
    ScamazonFrontendTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Category Cards Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryCard(
                    name = "Man Shirt",
                    icon = Icons.Default.Person,
                    isSelected = true
                )
                CategoryCard(
                    name = "Dress",
                    icon = Icons.Default.Checkroom
                )
                CategoryCard(
                    name = "Man Bag",
                    icon = Icons.Default.ShoppingBag
                )
                CategoryCard(
                    name = "Woman",
                    icon = Icons.Default.Woman
                )
            }

            // Category Chips Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryChip(name = "All", isSelected = true)
                CategoryChip(name = "Newest")
                CategoryChip(name = "Popular")
                CategoryChip(name = "Man")
            }

            // Sale Banner
            SaleBannerCard(
                title = "Super Flash Sale",
                subtitle = "50% Off",
                discount = "Get Now"
            )
        }
    }
}
