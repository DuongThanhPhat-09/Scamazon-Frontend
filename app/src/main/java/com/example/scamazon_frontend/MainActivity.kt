package com.example.scamazon_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.scamazon_frontend.ui.navigation.MainScreen
import com.example.scamazon_frontend.ui.theme.BackgroundWhite
import com.example.scamazon_frontend.ui.theme.ScamazonFrontendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScamazonFrontendTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundWhite
                ) {
                    MainScreen()
                }
            }
        }
    }
}
