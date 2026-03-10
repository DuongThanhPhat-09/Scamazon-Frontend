package com.example.scamazon_frontend.ui.screens.checkout

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Message
import android.util.Log
import android.webkit.JsResult
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
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
 * Opens a WebView that loads the VNPay payment URL.
 * Intercepts the backend return URL to navigate back to the app with the result.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PaymentWebViewScreen(
    paymentUrl: String,
    returnUrl: String = "https://scamazon-backend-nman.onrender.com/api/payments/vnpay-return",
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
        LafyuuTopAppBar(
            title = "Payment",
            onBackClick = onNavigateBack
        )

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    val frameLayout = android.widget.FrameLayout(context)
                    frameLayout.layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    // Hàm tạo WebView chuẩn với đầy đủ config (Dùng cho cả Main và Popup)
                    fun createWebView(): WebView {
                        return WebView(context).apply {
                            layoutParams = android.widget.FrameLayout.LayoutParams(
                                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                                android.widget.FrameLayout.LayoutParams.MATCH_PARENT
                            )
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            
                            // Bật cờ cho phép mở popup OTP của VNPay
                            settings.javaScriptCanOpenWindowsAutomatically = true
                            settings.setSupportMultipleWindows(true)

                            webViewClient = object : WebViewClient() {
                                @SuppressLint("WebViewClientOnReceivedSslError")
                                override fun onReceivedSslError(
                                    view: WebView?, handler: SslErrorHandler?, error: SslError?
                                ) {
                                    handler?.proceed()
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?, request: WebResourceRequest?
                                ): Boolean {
                                    val url = request?.url?.toString() ?: return false
                                    Log.d("PaymentWebView", "Navigating to: $url")

                                    // Chặn đúng URL return của backend ở bất kỳ WebView nào (main hay popup)
                                    if (url.startsWith(returnUrl)) {
                                        val responseCode = request.url.getQueryParameter("vnp_ResponseCode")
                                        
                                        // Gửi request ngầm về backend để chắc chắn endpoint Return được gọi
                                        // vì WebView không navigate tới trang này do return true.
                                        Thread {
                                            try {
                                                java.net.URL(url).readText()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }.start()

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
                        }
                    }

                    // 1. Tạo WebView chính
                    val mainWebView = createWebView()
                    
                    mainWebView.webChromeClient = object : WebChromeClient() {
                        override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                            result?.confirm()
                            return true
                        }
                        override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                            result?.confirm()
                            return true
                        }
                        
                        // Khi VNPay gọi window.open(), tạo WebView mới đè lên trên FrameLayout để hiển thị popup OTP
                        override fun onCreateWindow(
                            view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?
                        ): Boolean {
                            Log.d("PaymentWebView", "Mở popup window mới cho VNPay OTP")
                            val newWebView = createWebView()
                            
                            // Nếu popup gọi window.close(), gỡ nó khỏi FrameLayout
                            newWebView.webChromeClient = object : WebChromeClient() {
                                override fun onCloseWindow(window: WebView?) {
                                    frameLayout.removeView(window)
                                }
                            }
                            
                            // Hiển thị popup WebView lên màn hình
                            frameLayout.addView(newWebView)
                            
                            // Gửi request form (bao gồm POST data) vào popup WebView mới tạo
                            val transport = resultMsg?.obj as? WebView.WebViewTransport
                            transport?.webView = newWebView
                            resultMsg?.sendToTarget()
                            return true
                        }
                    }

                    // 2. Thêm WebView chính vào Layout và bắt đầu tải URL VNPay
                    frameLayout.addView(mainWebView)
                    mainWebView.loadUrl(paymentUrl)
                    
                    // Trả về layout chứa các WebView
                    frameLayout
                }
            )

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
