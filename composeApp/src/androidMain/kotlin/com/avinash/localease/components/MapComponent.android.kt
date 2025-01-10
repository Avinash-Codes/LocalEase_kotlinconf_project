package com.avinash.localease.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.avinash.localease.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*

@Composable
actual fun MapComponent() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 13f)
    }

    // Get current location when the component is first composed
    LaunchedEffect(Unit) {
        getCurrentLocation(context, fusedLocationClient) { location ->
            currentLocation = LatLng(location.latitude, location.longitude)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation!!, 15f)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = DefaultMapUiSettings.copy(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = false,
                tiltGesturesEnabled = false,
                mapToolbarEnabled = false,
            )
        ) {
            MapEffect { map: GoogleMap ->
                val success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
                )
                if (!success) {
                    // Handle the error
                }
            }

            currentLocation?.let { location ->
                Marker(
                    state = MarkerState(location),
                    title = "My Location",
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.currlocation)
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        cameraPositionState.position.target,
                        cameraPositionState.position.zoom + 1
                    )
                },
                backgroundColor = androidx.compose.ui.graphics.Color.White,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Zoom In", tint = androidx.compose.ui.graphics.Color.Blue)
            }

            FloatingActionButton(
                onClick = {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        cameraPositionState.position.target,
                        cameraPositionState.position.zoom - 1
                    )
                },
                backgroundColor = androidx.compose.ui.graphics.Color.White,
            ) {
                Icon(Icons.Filled.Remove, contentDescription = "Zoom Out", tint = androidx.compose.ui.graphics.Color.Blue)
            }

            FloatingActionButton(
                onClick = {
                    getCurrentLocation(context, fusedLocationClient) { location ->
                        currentLocation = LatLng(location.latitude, location.longitude)
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation!!, 15f)
                    }
                },
                modifier = Modifier.padding(bottom = 16.dp),
                backgroundColor = androidx.compose.ui.graphics.Color.White,
            ) {
                Icon(Icons.Filled.MyLocation, contentDescription = "My Location", tint = androidx.compose.ui.graphics.Color.Blue)
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (android.location.Location) -> Unit
) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Request permissions
        return
    }
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            onLocationReceived(it)
        }
    }
}