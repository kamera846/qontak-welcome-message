package com.topmortar.topmortarsales.commons.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.topmortar.topmortarsales.R
import com.topmortar.topmortarsales.commons.FIREBASE_CHILD_DELIVERY
import com.topmortar.topmortarsales.commons.utils.FirebaseUtils
import com.topmortar.topmortarsales.view.courier.CourierActivity

class TrackingService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var firebaseReference: DatabaseReference
    private lateinit var childDelivery: DatabaseReference
    private lateinit var childDriver: DatabaseReference

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        Log.d("Tracking Service", "On Start Service")
        startForegroundService()
        startLocationUpdates(intent)
        return START_STICKY
    }

    override fun onDestroy() {
        stopLocationUpdates()
        super.onDestroy()
    }

    private fun startForegroundService() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("tracking_service", "Tracking Background Service")
            } else {
                ""
            }

        val notificationIntent = Intent(this, CourierActivity::class.java)
        notificationIntent.putExtra("notif_intent", true)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.favicon_round)
            .setContentTitle("Selesaikan semua pengiriman!")
            .setContentText("Ketuk untuk melihat")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(1010, notification)
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_NONE
            )
            channel.lightColor = Color.BLUE
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)
            channelId
        } else {
            ""
        }
    }

    private fun startLocationUpdates(intent: Intent?) {

        val userDistributorId = intent?.getStringExtra("userDistributorId").toString()
        val deliveryId = intent?.getStringExtra("deliveryId").toString()

        firebaseReference = FirebaseUtils().getReference(distributorId = userDistributorId)
        childDelivery = firebaseReference.child(FIREBASE_CHILD_DELIVERY)
        childDriver = childDelivery.child(deliveryId)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(3000)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {

                val driverLocation = locationResult.lastLocation!!
                childDriver.child("lat").setValue(driverLocation.latitude)
                childDriver.child("lng").setValue(driverLocation.longitude)

            }
        }

        // Memulai permintaan pembaruan lokasi di sini
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) return
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        // Memberhentikan pembaruan lokasi di sini
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//            &&
//            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//        ) return
        fusedLocationClient.removeLocationUpdates(locationCallback)
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location: Location? ->
//                childDriver.child("endLat").setValue(location?.latitude)
//                childDriver.child("endLng").setValue(location?.longitude)
//                childDriver.child("tracking_mode").setValue(false)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    childDriver.child("endDatetime").setValue(DateFormat.now())
//                }
//            }

//        Handler().postDelayed({
//            childDriver.removeValue()
//        }, 200)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}