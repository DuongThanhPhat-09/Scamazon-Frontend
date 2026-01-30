package com.example.scamazon_frontend.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ===========================================
// LAFYUU E-COMMERCE DESIGN SYSTEM - SHAPES
// ===========================================

val Shapes = Shapes(
    // Extra Small - for small elements like badges, chips
    extraSmall = RoundedCornerShape(4.dp),

    // Small - for buttons, input fields
    small = RoundedCornerShape(5.dp),

    // Medium - for cards, dialogs
    medium = RoundedCornerShape(8.dp),

    // Large - for bottom sheets, large cards
    large = RoundedCornerShape(12.dp),

    // Extra Large - for full screen modals
    extraLarge = RoundedCornerShape(16.dp)
)

// Custom Shapes for specific components
object LafyuuShapes {
    val ButtonShape = RoundedCornerShape(5.dp)
    val CardShape = RoundedCornerShape(5.dp)
    val InputFieldShape = RoundedCornerShape(5.dp)
    val BottomSheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    val ImageShape = RoundedCornerShape(8.dp)
    val BadgeShape = RoundedCornerShape(50)
    val ChipShape = RoundedCornerShape(50)
    val SearchBarShape = RoundedCornerShape(5.dp)
    val CategoryCardShape = RoundedCornerShape(66.dp)
}
