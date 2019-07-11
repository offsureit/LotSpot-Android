package com.oit.lotspot.service


import android.content.Intent
import android.os.IBinder
import android.os.Bundle
import android.app.Activity
import android.app.Service
import android.content.DialogInterface
import android.location.LocationManager
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import com.oit.lotspot.constants.PermissionConst


class GPSTracker : Service, LocationListener {

    private var TAG = GPSTracker::class.java.simpleName
    private var context = this
    private var mContext: Activity? = null
    private var locationCHanged: LocationChangeInterface? = null

    // Flag for GPS status
    internal var isGPSEnabled = false

    // Flag for network status
    internal var isNetworkEnabled = false

    // Flag for GPS status
    internal var canGetLocation = false

    internal var location: Location? = null // Location
    internal var latitude: Double = 0.toDouble() // Latitude
    internal var longitude: Double = 0.toDouble() // Longitude

    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null

//    init {
//
//    }


    public constructor() : super()

    constructor(mContext: Activity, locationCHanged: LocationChangeInterface) : this() {
        this.mContext = mContext
        this.locationCHanged = locationCHanged

        getLocation()
    }


    fun getLocation(): Location? {
        try {

            Log.d(TAG, "Debug starts at 0")
            locationManager = mContext!!.getSystemService(LOCATION_SERVICE) as LocationManager?

            // Getting GPS status
            isGPSEnabled = locationManager!!
                .isProviderEnabled(LocationManager.GPS_PROVIDER)

            Log.d(TAG, "Debug starts at 1")
            // Getting network status
            isNetworkEnabled = locationManager!!
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            Log.d(TAG, "Debug starts at 2")
            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.d(TAG, "Debug starts at 3")
                // No network provider is enabled
            } else {
                Log.d(TAG, "Debug starts at 4")
                this.canGetLocation = true
                if (isNetworkEnabled) {
                    Log.d(TAG, "Debug starts at 5")
                    if (ContextCompat.checkSelfPermission(
                            mContext!!,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        ActivityCompat.requestPermissions(
                            mContext!!,
                            PermissionConst.PERMISSION.LOCATION_ARRAY,
                            PermissionConst.REQUEST_CODE.LOCATION
                        )
                        Log.d(TAG, "Debug starts at 6")
                    }
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                        this
                    )
                    Log.d("Network", "Network")
                    Log.d(TAG, "Debug starts at 6")
                    if (locationManager != null) {
                        Log.d(TAG, "Debug starts at 7")
                        location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {

                            latitude = location!!.latitude
                            longitude = location!!.longitude
                            Log.d(TAG, "Debug starts at 7i ,, latitude = $latitude ... $longitude")
                        }
                    }
                }
                Log.d(TAG, "Debug starts at 8")
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    Log.d(TAG, "Debug starts at 9")
                    if (location == null) {
                        Log.d(TAG, "Debug starts at 10")
                        if (ContextCompat.checkSelfPermission(
                                mContext!!,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            Log.d(TAG, "Debug starts at 11")

                            ActivityCompat.requestPermissions(
                                mContext!!,
                                PermissionConst.PERMISSION.LOCATION_ARRAY,
                                PermissionConst.REQUEST_CODE.LOCATION
                            )
                        }
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                            this
                        )
                        Log.d(TAG, "Debug starts at 12")
                        Log.d("GPS Enabled", "GPS Enabled")
                        if (locationManager != null) {
                            Log.d(TAG, "Debug starts at 13")
                            location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Debug starts at 14")
        }

        return location
    }


    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app.
     */
    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@GPSTracker)
        }
    }


    /**
     * Function to get latitude
     */
    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }

        // return latitude
        return latitude
    }


    /**
     * Function to get longitude
     */
    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }

        // return longitude
        return longitude
    }

    /**
     * Function to check GPS/Wi-Fi enabled
     * @return boolean
     */
    fun canGetLocation(): Boolean {
        return this.canGetLocation
    }


    /**
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     */
    fun showAlertForSettings() {
        val alertDialog = AlertDialog.Builder(mContext!!)

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings")

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings", DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext!!.startActivity(intent)
        })

        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        // Showing Alert Message
        alertDialog.show()
    }


    override fun onLocationChanged(location: Location) {
        Log.d(TAG, "On Location Change Listener")
        locationCHanged!!.myLocationChanged(location)
    }


    override fun onProviderDisabled(provider: String) {}


    override fun onProviderEnabled(provider: String) {}


    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}


    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    companion object {

        // The minimum distance to change Updates in meters
        private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

        // The minimum time between updates in milliseconds
        private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute
    }

    interface LocationChangeInterface {
        fun myLocationChanged(location: Location)
    }
}