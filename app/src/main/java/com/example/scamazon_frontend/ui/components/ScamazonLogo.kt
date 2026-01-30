package com.example.scamazon_frontend.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.ui.theme.*

/**
 * Scamazon Logo Component - Minimal Style
 * Premium logo with Dark Blue & Gold only
 */
@Composable
fun ScamazonLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    showBrandName: Boolean = true,
    showTagline: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo Icon
        Box(
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(size / 4),
                    spotColor = PrimaryBlue.copy(alpha = 0.25f)
                )
                .clip(RoundedCornerShape(size / 4))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PrimaryBlue,
                            PrimaryBlueLight,
                            PrimaryBlue
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Shopping Bag with S - Gold accent
            Canvas(
                modifier = Modifier.size(size * 0.65f)
            ) {
                val canvasWidth = this.size.width
                val canvasHeight = this.size.height

                // Shopping bag body
                val bagPath = Path().apply {
                    moveTo(canvasWidth * 0.15f, canvasHeight * 0.35f)
                    lineTo(canvasWidth * 0.15f, canvasHeight * 0.85f)
                    quadraticBezierTo(
                        canvasWidth * 0.15f, canvasHeight * 0.95f,
                        canvasWidth * 0.25f, canvasHeight * 0.95f
                    )
                    lineTo(canvasWidth * 0.75f, canvasHeight * 0.95f)
                    quadraticBezierTo(
                        canvasWidth * 0.85f, canvasHeight * 0.95f,
                        canvasWidth * 0.85f, canvasHeight * 0.85f
                    )
                    lineTo(canvasWidth * 0.85f, canvasHeight * 0.35f)
                    close()
                }

                // Bag with cream/gold gradient
                drawPath(
                    path = bagPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Cream,
                            CreamLight
                        )
                    )
                )

                // Bag handle with Gold
                val handlePath = Path().apply {
                    moveTo(canvasWidth * 0.32f, canvasHeight * 0.35f)
                    quadraticBezierTo(
                        canvasWidth * 0.32f, canvasHeight * 0.12f,
                        canvasWidth * 0.5f, canvasHeight * 0.12f
                    )
                    quadraticBezierTo(
                        canvasWidth * 0.68f, canvasHeight * 0.12f,
                        canvasWidth * 0.68f, canvasHeight * 0.35f
                    )
                }

                drawPath(
                    path = handlePath,
                    color = AccentGold,
                    style = Stroke(width = canvasWidth * 0.06f, cap = StrokeCap.Round)
                )

                // Stylized "S" with Gold (accent color)
                val sPath = Path().apply {
                    moveTo(canvasWidth * 0.62f, canvasHeight * 0.42f)
                    quadraticBezierTo(
                        canvasWidth * 0.62f, canvasHeight * 0.38f,
                        canvasWidth * 0.52f, canvasHeight * 0.38f
                    )
                    lineTo(canvasWidth * 0.42f, canvasHeight * 0.38f)
                    quadraticBezierTo(
                        canvasWidth * 0.32f, canvasHeight * 0.38f,
                        canvasWidth * 0.32f, canvasHeight * 0.48f
                    )
                    quadraticBezierTo(
                        canvasWidth * 0.32f, canvasHeight * 0.56f,
                        canvasWidth * 0.42f, canvasHeight * 0.58f
                    )
                    lineTo(canvasWidth * 0.58f, canvasHeight * 0.62f)
                    quadraticBezierTo(
                        canvasWidth * 0.68f, canvasHeight * 0.64f,
                        canvasWidth * 0.68f, canvasHeight * 0.74f
                    )
                    quadraticBezierTo(
                        canvasWidth * 0.68f, canvasHeight * 0.84f,
                        canvasWidth * 0.58f, canvasHeight * 0.84f
                    )
                    lineTo(canvasWidth * 0.42f, canvasHeight * 0.84f)
                    quadraticBezierTo(
                        canvasWidth * 0.32f, canvasHeight * 0.84f,
                        canvasWidth * 0.32f, canvasHeight * 0.78f
                    )
                }

                drawPath(
                    path = sPath,
                    color = AccentGold,
                    style = Stroke(width = canvasWidth * 0.08f, cap = StrokeCap.Round)
                )
            }

            // Gold accent ring
            Box(
                modifier = Modifier
                    .size(size * 0.85f)
                    .background(Color.Transparent)
            )
        }

        if (showBrandName) {
            Spacer(modifier = Modifier.height(20.dp))

            // Brand Name with minimal styling - Dark Blue & Gold only
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(
                        fontWeight = FontWeight.Light,
                        color = PrimaryBlue
                    )) {
                        append("Scam")
                    }
                    withStyle(style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )) {
                        append("azon")
                    }
                },
                fontFamily = Poppins,
                fontSize = 36.sp,
                letterSpacing = 2.sp
            )
        }

        if (showTagline) {
            Spacer(modifier = Modifier.height(8.dp))

            // Tagline with Gold decoration
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(1.dp)
                        .background(AccentGold)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Premium Shopping",
                    fontFamily = Poppins,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(1.dp)
                        .background(AccentGold)
                )
            }
        }
    }
}

/**
 * Compact logo version for smaller spaces
 */
@Composable
fun ScamazonLogoCompact(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    ScamazonLogo(
        modifier = modifier,
        size = size,
        showBrandName = false,
        showTagline = false
    )
}

/**
 * Logo with custom brand text size
 */
@Composable
fun ScamazonLogoMedium(
    modifier: Modifier = Modifier
) {
    ScamazonLogo(
        modifier = modifier,
        size = 100.dp,
        showBrandName = true,
        showTagline = false
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFAF9F6)
@Composable
fun ScamazonLogoPreview() {
    ScamazonFrontendTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            ScamazonLogo()
            ScamazonLogoMedium()
            ScamazonLogoCompact()
        }
    }
}
