package com.example.scamazon_frontend.ui.theme

import androidx.compose.ui.graphics.Color

// ===========================================
// SCAMAZON E-COMMERCE - MINIMAL COLOR PALETTE
// Only 2 main colors: Dark Blue & Gold Dark
// ===========================================

// ============ PRIMARY COLORS ============

// Dark Blue - Primary Color (Text, Buttons, Main elements)
val PrimaryBlue = Color(0xFF223263)
val PrimaryBlueDark = Color(0xFF152044)
val PrimaryBlueLight = Color(0xFF3D4F7F)
val PrimaryBlueSoft = Color(0xFFEBF0FF)

// Gold Dark - Accent Color (CTA, Highlights, Stars)
val AccentGold = Color(0xFFCC9900)
val AccentGoldLight = Color(0xFFFFD966)
val AccentGoldSoft = Color(0xFFFFF8E5)

// ============ NEUTRAL COLORS ============

// White & Backgrounds
val White = Color(0xFFFFFFFF)
val BackgroundWhite = Color(0xFFFFFFFF)
val BackgroundLight = Color(0xFFF6F6F6)
val BackgroundGrey = Color(0xFFEBF0FF)

// ============ TEXT COLORS ============

val TextPrimary = PrimaryBlue           // Main text - Dark Blue
val TextSecondary = Color(0xFF9098B1)   // Secondary text - Grey
val TextHint = Color(0xFF9098B1)        // Hints/Placeholders

// ============ BORDER COLORS ============

val BorderLight = Color(0xFFEBF0FF)
val BorderDefault = Color(0xFFE0E0E0)

// ============ STATUS COLORS ============

val StatusSuccess = Color(0xFF53D1B6)   // Green
val StatusError = Color(0xFFFB7181)     // Red
val StatusWarning = AccentGold          // Gold
val StatusInfo = PrimaryBlue            // Dark Blue

// ============ STAR RATING ============

val StarFilled = AccentGold             // Gold stars
val StarEmpty = Color(0xFFD4D4D4)

// ============ COMPONENT SPECIFIC ============

// Button Colors
val ButtonPrimary = PrimaryBlue         // Primary button - Dark Blue
val ButtonPrimaryText = White
val ButtonAccent = AccentGold           // Accent button - Gold
val ButtonAccentText = White
val ButtonOutline = PrimaryBlue
val ButtonDisabled = Color(0xFFB0B0B0)

// Input Field Colors
val InputBackground = White
val InputBorder = BorderDefault
val InputBorderFocused = PrimaryBlue
val InputBorderError = StatusError

// Card Colors
val CardBackground = White
val CardBorder = BorderLight

// Navigation Colors
val NavBackground = White
val NavSelected = PrimaryBlue
val NavUnselected = TextSecondary
val NavBadge = AccentGold

// ============ LEGACY ALIASES ============
// (For backward compatibility)

val Navy = PrimaryBlue
val NavyLight = PrimaryBlueLight
val NavyDark = PrimaryBlueDark
val Navy20 = Color(0x33223263)
val Navy40 = Color(0x66223263)

val DarkBlue = PrimaryBlue
val DarkBlueLight = PrimaryBlueLight
val DarkBlueDark = PrimaryBlueDark
val DarkBlue20 = Navy20
val DarkBlue10 = Color(0x1A223263)

val Gold = AccentGold
val GoldLight = AccentGoldLight
val GoldDark = AccentGold
val Gold10 = Color(0x1ACC9900)
val Gold20 = Color(0x33CC9900)

val SecondaryRed = StatusError
val SecondaryRedDark = Color(0xFFE63946)
val Burgundy = StatusError
val BurgundyLight = StatusError
val BurgundyDark = SecondaryRedDark
val Burgundy20 = Color(0x33FB7181)
val Burgundy10 = Color(0x1AFB7181)

val SecondaryGreen = StatusSuccess
val SecondaryGreenDark = Color(0xFF3AAA96)
val Green = StatusSuccess
val GreenLight = StatusSuccess
val GreenDark = SecondaryGreenDark

val SecondaryYellow = AccentGold
val SecondaryOrange = AccentGold
val StarYellow = AccentGold
val StarGrey = StarEmpty

val Cream = BackgroundWhite
val CreamLight = BackgroundLight
val CreamDark = BackgroundGrey

val SecondaryPurple = PrimaryBlue

// Material 3 compatibility
val Purple80 = PrimaryBlueLight
val PurpleGrey80 = TextSecondary
val Pink80 = StatusError

val Purple40 = PrimaryBlueDark
val PurpleGrey40 = PrimaryBlue
val Pink40 = SecondaryRedDark
