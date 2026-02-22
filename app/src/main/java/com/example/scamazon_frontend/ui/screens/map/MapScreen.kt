package com.example.scamazon_frontend.ui.screens.map

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.scamazon_frontend.ui.components.LafyuuTopAppBar
import com.example.scamazon_frontend.ui.theme.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current

    // Store location: 123-125 Đ. Lê Văn Việt, Thủ Đức
    val storeLat = 10.8447
    val storeLng = 106.7801

    // Configure osmdroid user agent
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Scaffold(
        topBar = {
            LafyuuTopAppBar(
                title = "Store Location",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // OpenStreetMap view
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(17.0)
                        controller.setCenter(GeoPoint(storeLat, storeLng))

                        // Add marker
                        val marker = Marker(this)
                        marker.position = GeoPoint(storeLat, storeLng)
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = "Scamazon Store"
                        marker.snippet = "123-125 Đ. Lê Văn Việt, Thủ Đức, TP HCM"
                        overlays.add(marker)
                    }
                },
                update = { mapView ->
                    mapView.controller.setCenter(GeoPoint(storeLat, storeLng))
                }
            )
            
            // Bottom Info Card
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                shape = LafyuuShapes.CardShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Scamazon Flagship Store",
                        style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "123-125 Đ. Lê Văn Việt, P, Thủ Đức, Thành phố Hồ Chí Minh 700000\nOpen: 08:00 AM - 10:00 PM\nPhone: (123) 456-7890",
                        style = Typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // Open Google Maps for directions
                            val uri = Uri.parse("google.navigation:q=$storeLat,$storeLng")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.setPackage("com.google.android.apps.maps")
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Fallback to browser
                                val browserUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$storeLat,$storeLng")
                                context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Get Directions", color = White)
                    }
                }
            }
        }
    }
}
