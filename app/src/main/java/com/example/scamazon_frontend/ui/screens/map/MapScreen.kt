package com.example.scamazon_frontend.ui.screens.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.scamazon_frontend.BuildConfig
import com.example.scamazon_frontend.ui.components.LafyuuTopAppBar
import com.example.scamazon_frontend.ui.theme.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

private val STORE_LOCATION = LatLng(10.8447, 106.7801)

@Composable
fun MapScreen(onNavigateBack: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(STORE_LOCATION, 15f)
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var isLocating by remember { mutableStateOf(false) }
    var isLoadingRoute by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Get current GPS location
    fun getCurrentLocation(onSuccess: (LatLng) -> Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    onSuccess(LatLng(loc.latitude, loc.longitude))
                } else {
                    fusedLocationClient.lastLocation.addOnSuccessListener { last ->
                        if (last != null) onSuccess(LatLng(last.latitude, last.longitude))
                        else errorMessage = "Không lấy được vị trí. Kiểm tra GPS."
                    }
                }
            }
            .addOnFailureListener {
                errorMessage = "Lỗi GPS: ${it.message}"
            }
    }

    // Pan camera to my location
    fun goToMyLocation() {
        isLocating = true
        getCurrentLocation { latLng ->
            isLocating = false
            userLocation = latLng
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
    }

    // Fetch route via Directions API and draw on map
    fun getDirections() {
        isLoadingRoute = true
        errorMessage = null
        getCurrentLocation { origin ->
            userLocation = origin
            scope.launch {
                val points = fetchDirectionsRoute(origin, STORE_LOCATION, BuildConfig.MAPS_API_KEY)
                isLoadingRoute = false
                if (points != null) {
                    routePoints = points
                    val bounds = LatLngBounds.builder().apply {
                        include(origin)
                        include(STORE_LOCATION)
                        points.forEach { include(it) }
                    }.build()
                    cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 120))
                } else {
                    errorMessage = "Không thể tải chỉ đường. Xem Logcat tag MapDirections."
                }
            }
        }
    }

    val mapProperties by remember(hasLocationPermission) {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                mapType = MapType.NORMAL
            )
        )
    }

    Scaffold(
        topBar = {
            LafyuuTopAppBar(title = "Store Location", onBackClick = onNavigateBack)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Map ──────────────────────────────────────────────────────────
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false,
                    compassEnabled = true
                )
            ) {
                // Store marker
                Marker(
                    state = MarkerState(position = STORE_LOCATION),
                    title = "Scamazon Store",
                    snippet = "123-125 Đ. Lê Văn Việt, Thủ Đức"
                )

                // Route polyline
                if (routePoints.size >= 2) {
                    Polyline(
                        points = routePoints,
                        color = PrimaryBlue,
                        width = 10f
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // My Location
                FloatingActionButton(
                    onClick = { goToMyLocation() },
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    containerColor = White,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                ) {
                    if (isLocating) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = PrimaryBlue)
                    } else {
                        Icon(Icons.Default.MyLocation, contentDescription = "My Location", tint = PrimaryBlue, modifier = Modifier.size(22.dp))
                    }
                }

                // Get Directions
                FloatingActionButton(
                    onClick = { getDirections() },
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    containerColor = if (routePoints.isNotEmpty()) PrimaryBlue else White,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                ) {
                    if (isLoadingRoute) {
                        CircularProgressIndicator(
                            Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = if (routePoints.isNotEmpty()) White else PrimaryBlue
                        )
                    } else {
                        Icon(
                            Icons.Default.Navigation,
                            contentDescription = "Directions",
                            tint = if (routePoints.isNotEmpty()) White else PrimaryBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Error snackbar
            errorMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { errorMessage = null }) {
                            Text("OK", color = White, fontFamily = Poppins)
                        }
                    },
                    containerColor = StatusError
                ) {
                    Text(msg, color = White, fontFamily = Poppins)
                }
            }
        }
    }
}


private val httpClient = OkHttpClient()

private suspend fun fetchDirectionsRoute(
    origin: LatLng,
    destination: LatLng,
    apiKey: String
): List<LatLng>? = withContext(Dispatchers.IO) {
    try {
        val url = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&mode=driving" +
                "&key=$apiKey"

        val request = Request.Builder().url(url).build()
        val response = httpClient.newCall(request).execute()
        val body = response.body?.string() ?: return@withContext null

        val json = JSONObject(body)
        if (json.getString("status") != "OK") return@withContext null

        val overviewPolyline = json
            .getJSONArray("routes")
            .getJSONObject(0)
            .getJSONObject("overview_polyline")
            .getString("points")

        decodePolyline(overviewPolyline)
    } catch (e: Exception) {
        null
    }
}

private fun decodePolyline(encoded: String): List<LatLng> {
    val result = mutableListOf<LatLng>()
    var index = 0
    var lat = 0
    var lng = 0

    while (index < encoded.length) {
        var b: Int
        var shift = 0
        var result1 = 0
        do {
            b = encoded[index++].code - 63
            result1 = result1 or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        lat += if (result1 and 1 != 0) (result1 shr 1).inv() else result1 shr 1

        shift = 0
        result1 = 0
        do {
            b = encoded[index++].code - 63
            result1 = result1 or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        lng += if (result1 and 1 != 0) (result1 shr 1).inv() else result1 shr 1

        result.add(LatLng(lat / 1e5, lng / 1e5))
    }
    return result
}
