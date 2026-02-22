package com.example.scamazon_frontend.core.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Format a price value as Vietnamese currency style: 100.000.000
 * Uses dot as thousand separator, no decimal places.
 */
fun formatPrice(price: Double): String {
    val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = '.'
    }
    val formatter = DecimalFormat("#,###", symbols)
    return formatter.format(price.toLong())
}
