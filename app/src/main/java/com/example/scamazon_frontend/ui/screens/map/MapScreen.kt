package com.example.scamazon_frontend.ui.screens.map

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scamazon_frontend.ui.components.LafyuuTopAppBar
import com.example.scamazon_frontend.ui.theme.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current

    // Store location: 123-125 Đ. Lê Văn Việt, Thủ Đức
    val storeLocation = LatLng(10.8447, 106.7801)

    // Camera state - start at store location
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(storeLocation, 15f)
    }

    // Location permission state
    var hasLocationPermission by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // Request location permission on first launch
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Map UI properties
    val mapProperties by remember(hasLocationPermission) {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                mapType = MapType.NORMAL
            )
        )
    }

    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = true,
                compassEnabled = true
            )
        )
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
            // Google Map
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings
            ) {
                // Store marker
                Marker(
                    state = MarkerState(position = storeLocation),
                    title = "Scamazon Store",
                    snippet = "123-125 Đ. Lê Văn Việt, Thủ Đức, TP HCM"
                )
            }

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
                            // Open Google Maps for directions from current location
                            val storeLat = storeLocation.latitude
                            val storeLng = storeLocation.longitude
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
