package de.astronarren.allsky.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationManager(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { continuation ->
        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                println("Debug: Location permission granted, requesting location")
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        println("Debug: Location received: $location")
                        continuation.resume(location)
                    }
                    .addOnFailureListener { e ->
                        println("Debug: Failed to get location: ${e.message}")
                        continuation.resume(null)
                    }
            } else {
                println("Debug: Location permission not granted")
                continuation.resume(null)
            }
        } catch (e: Exception) {
            println("Debug: Error getting location: ${e.message}")
            continuation.resume(null)
        }
    }
} 