package com.example.scamazon_frontend.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.scamazon_frontend.ui.theme.*

/**
 * Rating Bar - Lafyuu Style
 * Display star rating
 */
@Composable
fun RatingBar(
    rating: Float,
    modifier: Modifier = Modifier,
    starSize: Dp = Dimens.StarSize,
    showRatingText: Boolean = false,
    onRatingChange: ((Int) -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingXXSmall)
    ) {
        repeat(5) { index ->
            val starRating = index + 1
            val icon = when {
                rating >= starRating -> Icons.Default.Star
                rating >= starRating - 0.5f -> Icons.Default.StarHalf
                else -> Icons.Default.StarBorder
            }

            Icon(
                imageVector = icon,
                contentDescription = "Star $starRating",
                tint = if (rating >= starRating - 0.5f) AccentGold else StarEmpty,
                modifier = Modifier
                    .size(starSize)
                    .then(
                        if (onRatingChange != null) {
                            Modifier.clickable { onRatingChange(starRating) }
                        } else {
                            Modifier
                        }
                    )
            )
        }

        if (showRatingText) {
            Text(
                text = String.format("%.1f", rating),
                style = Typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

/**
 * Interactive Rating Bar - Lafyuu Style
 * Allows user to select rating
 */
@Composable
fun InteractiveRatingBar(
    currentRating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    starSize: Dp = Dimens.StarSizeLarge
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingXSmall)
    ) {
        repeat(5) { index ->
            val starRating = index + 1
            Icon(
                imageVector = if (currentRating >= starRating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Star $starRating",
                tint = if (currentRating >= starRating) AccentGold else StarEmpty,
                modifier = Modifier
                    .size(starSize)
                    .clickable { onRatingChange(starRating) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RatingBarsPreview() {
    ScamazonFrontendTheme {
        Column(
            modifier = Modifier.padding(Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
        ) {
            RatingBar(rating = 4.5f, showRatingText = true)
            RatingBar(rating = 3f)
            RatingBar(rating = 2.5f, starSize = Dimens.StarSizeLarge)
            InteractiveRatingBar(currentRating = 4, onRatingChange = {})
        }
    }
}
