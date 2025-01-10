package com.avinash.localease.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.Foundation.NSError
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.darwin.NSObject

@Composable
actual fun MapComponent() {
    val locationManager = remember { CLLocationManager() }
    var currentLocation by remember { mutableStateOf<CLLocation?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Your map view code here

        FloatingActionButton(
            onClick = {
                getCurrentLocation(locationManager) { location ->
                    currentLocation = location
                    // Update your map view with the new location
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource("my_location.svg"),
                contentDescription = "My Location"
            )
        }
    }
}

private fun getCurrentLocation(
    locationManager: CLLocationManager,
    onLocationReceived: (CLLocation) -> Unit
) {
    locationManager.requestWhenInUseAuthorization()
    if (locationManager.authorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse) {
        locationManager.requestLocation()
        locationManager.delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                val location = didUpdateLocations.firstOrNull() as? CLLocation
                location?.let {
                    onLocationReceived(it)
                }
            }

            override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                // Handle error
            }
        }
    }
}