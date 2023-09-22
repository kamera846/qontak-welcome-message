package com.topmortar.topmortarsales.commons.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class URLUtility(private val context: Context) {

    @SuppressLint("MissingPermission")
    fun requestLocationUpdate() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Define a LocationListener to handle location updates
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Handle the newly received location here
                locationManager.removeUpdates(this) // Stop listening for updates once you have a location
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        // Request location updates
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, // Use the provider you need (GPS or NETWORK)
            0, // Minimum time interval between updates in milliseconds
            0f, // Minimum distance between updates in meters
            locationListener
        )
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Periksa apakah GPS atau jaringan lokasi telah diaktifkan
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        return isGpsEnabled || isNetworkEnabled
    }

    fun isUrl(stringToCheck: String): Boolean {
        val urlPattern = Pattern.compile(
            "^((http|https|ftp)://)?([a-zA-Z0-9.-]+(\\.[a-zA-Z]{2,4})+)(:[0-9]+)?(/.*)?$"
        )
        val matcher = urlPattern.matcher(stringToCheck)
        return matcher.matches()
    }

    fun fetchOriginalUrl(shortenedUrl: String, onComplete: (String) -> Unit) {
        FetchOriginalUrlTask(onComplete).execute(shortenedUrl)
    }

    @SuppressLint("MissingPermission")
    fun getDistance(mapsUrl: String): Double? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        return if (location != null) {
            val currentLatitude = location.latitude
            val currentLongitude = location.longitude

            val regex = Pattern.compile("@(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)")
            val matcher = regex.matcher(mapsUrl)
            if (matcher.find() && matcher.groupCount() == 2) {
                val urlLatitude = matcher.group(1).toDouble()
                val urlLongitude = matcher.group(2).toDouble()

                return calculateDistance(currentLatitude, currentLongitude, urlLatitude, urlLongitude)

            } else null
        } else null
    }

    @SuppressLint("MissingPermission")
    fun getLatLng(mapsUrl: String): LatLng? {
        val regex = Pattern.compile("@(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)")
        val matcher = regex.matcher(mapsUrl)
        return if (matcher.find() && matcher.groupCount() == 2) {
            val urlLatitude = matcher.group(1).toDouble()
            val urlLongitude = matcher.group(2).toDouble()

            LatLng(urlLatitude, urlLongitude)

        } else null
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radius = 6371 // Earth's radius in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return radius * c
    }

    private inner class FetchOriginalUrlTask(private val onComplete: (String) -> Unit) : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            val shortenedUrl = params[0]

            try {
                val url = URL(shortenedUrl)
                val connection = url.openConnection() as HttpURLConnection

                // Set up the connection
                connection.requestMethod = "HEAD"
                connection.instanceFollowRedirects = false
                connection.connect()

                // Check for redirection (HTTP 3xx)
                if (connection.responseCode in 300..399) {
                    val locationHeader = connection.getHeaderField("Location")
                    if (locationHeader != null) {
                        return locationHeader
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ""
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            if (result.isNotEmpty()) {
                onComplete(result)
            } else {
                onComplete("")
            }
        }
    }
}
