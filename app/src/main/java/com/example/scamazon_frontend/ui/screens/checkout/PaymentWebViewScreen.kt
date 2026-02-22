package com.example.scamazon_frontend.ui.screens.checkout

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.scamazon_frontend.ui.components.LafyuuTopAppBar
import com.example.scamazon_frontend.ui.theme.*

/**
 * PaymentWebViewScreen
 *
 * Opens a WebView that loads the VNPay (or other provider) payment URL.
 * Intercepts the callback/return URL to navigate back to the app with the result.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PaymentWebViewScreen(
    paymentUrl: String,
    returnUrl: String = "scamazon://payment/callback",
    onPaymentSuccess: () -> Unit = {},
    onPaymentFailed: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    var isLoading by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // Top bar
        LafyuuTopAppBar(
            title = "Payment",
            onBackClick = onNavigateBack
        )

        Box(modifier = Modifier.fillMaxSize()) {
            // WebView
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val url = request?.url?.toString() ?: return false

                                // Intercept return/callback URL
                                if (url.contains("payment/callback") ||
                                    url.contains("vnp_ResponseCode") ||
                                    url.startsWith(returnUrl)
                                ) {
                                    // Parse response code
                                    val responseCode = request.url.getQueryParameter("vnp_ResponseCode")
                                    if (responseCode == "00") {
                                        onPaymentSuccess()
                                    } else {
                                        onPaymentFailed()
                                    }
                                    return true
                                }
                                return false
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                            }
                        }

                        loadUrl(paymentUrl)
                    }
                }
            )

            // Loading Indicator overlay
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
        }
    }
}
