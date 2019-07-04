package com.oit.lotspot.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.oit.lotspot.R
import com.oit.lotspot.constants.Constants
import com.oit.lotspot.constants.PermissionConst
import com.oit.lotspot.constants.PermissionHelper
import com.oit.lotspot.database.SharedPreferencesManager
import com.oit.lotspot.mvp.activity.BaseActivity
import com.oit.lotspot.presenter.TagLocationMapPresenter
import com.oit.lotspot.retrofit.ApiClient
import com.oit.lotspot.retrofit.request.SaveVehicleDetailsRequest
import com.oit.lotspot.retrofit.response.ErrorResponse
import com.oit.lotspot.retrofit.response.SaveVehicleDetailResponseModel
import com.oit.lotspot.retrofit.response.VehicleDetailResponseModel
import com.oit.lotspot.retrofit.response.google.GoogleMapPathModel
import com.oit.lotspot.service.GPSTracker
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.company.activity_tag_location_map.*
import kotlinx.android.synthetic.company.item_history.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.text.DecimalFormat


open class TagLocationMapActivity : BaseActivity(), GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener,
    OnMapReadyCallback, TagLocationMapPresenter.ResponseCallBack, GPSTracker.LocationChangeInterface {

    private var TAG = TagLocationActivity::class.java.simpleName
    private var mMap: GoogleMap? = null
    private var marker: Marker? = null
    private var vehicleMarker: Marker? = null
    private var locationAddress: List<Address>? = null
    private var geoCoder: Geocoder? = null
    private lateinit var gpsTracker: GPSTracker

    private var vehicleDetailResponseModel = VehicleDetailResponseModel.VehicleDetailDataResponseModel()
    private var saveVehicleDetailsRequest = SaveVehicleDetailsRequest()
    private lateinit var presenter: TagLocationMapPresenter
    private var is_From_Tag_Location = false

    private var routePolyLine: Polyline? = null
    private var routePathList: List<LatLng>? = null

    private var location: Location? = null

    private var checkPermission: Boolean = true
    private var locationDialog: android.support.v7.app.AlertDialog? = null
    private var permissionsDialog: android.support.v7.app.AlertDialog? = null
    private var alertDialogSettings: android.support.v7.app.AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_location_map)

        getIntentData()
        initUi()

        requestPermissionsIfNotGranted()
        initPresenter()
        clickListener()
        setUpMap()
    }


    private fun getIntentData() {
        when (intent.hasExtra(Constants.App.Bundle_Key.TAG_LOCATION_MAP) && intent.hasExtra(Constants.App.Bundle_Key.IS_FROM_TAG_LOCATION)) {
            true -> {
                vehicleDetailResponseModel =
                    Gson().fromJson<VehicleDetailResponseModel.VehicleDetailDataResponseModel>(
                        intent.getStringExtra(Constants.App.Bundle_Key.TAG_LOCATION_MAP),
                        VehicleDetailResponseModel.VehicleDetailDataResponseModel::class.java
                    )
                is_From_Tag_Location = intent.getBooleanExtra(Constants.App.Bundle_Key.IS_FROM_TAG_LOCATION, false)

                setIntentDataInViews()
            }
        }
    }

    /**
     * enable /disable views of toolbar
     */
    private fun initUi() {
        tvTitle.visibility = View.VISIBLE

        if (is_From_Tag_Location) {
            tvTitle.text = getString(R.string.text_title_vehicle_location)
            tvSave.visibility = View.GONE
            clDistance.visibility = View.VISIBLE
        } else {
            tvTitle.text = getString(R.string.text_title_map)
            tvSave.visibility = View.VISIBLE
            clDistance.visibility = View.GONE
        }
        gpsTracker = GPSTracker(this, this)
        initLatLngBounds()
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = TagLocationMapPresenter(this)
    }

    /**
     * Click event on views
     */
    private fun clickListener() {
        ivMenu.setOnClickListener(clickListener)
        tvSave.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivMenu -> onBackPressed()

                R.id.tvSave -> clickedSave()
            }
    }

    /**
     * called when user clicked save text view
     */
    private fun clickedSave() {
        hitApiToSaveDetails()
    }

    /**
     *  call for api to save vehicle details with lat/lng
     */
    private fun hitApiToSaveDetails() {
        fetchAddress()
        showProgressView()
        saveVehicleDetailsRequest.apply {
            vin = vehicleDetailResponseModel.vin!!
            year = vehicleDetailResponseModel.year!!
            make = vehicleDetailResponseModel.make!!
            model = vehicleDetailResponseModel.model!!
            image = vehicleDetailResponseModel.image!!
            engine = vehicleDetailResponseModel.engine!!
            address = locationAddress!![0].subAdminArea + "," + locationAddress!![0].subLocality
            lat = locationAddress!![0].latitude
            lng = locationAddress!![0].longitude
        }
        Log.d(TAG, "Area --> ${locationAddress!![0].subAdminArea}")
        val authToken = getAuthToken()
        presenter.saveVehicleDetails(authToken, saveVehicleDetailsRequest)
    }

    /**
     * fetch address from user current location if user does not set location
     */
    private fun fetchAddress() {
        if (locationAddress == null)
            locationAddress = geoCoder!!.getFromLocation(marker!!.position.latitude, marker!!.position.longitude, 1)
    }

    /**
     * set Intent data in bottom view
     */
    private fun setIntentDataInViews() {
        tvVinNumber.text = vehicleDetailResponseModel.vin
        tvVehicleName.text = getString(
            R.string.text_car_model,
            vehicleDetailResponseModel.year.toString(),
            vehicleDetailResponseModel.make,
            vehicleDetailResponseModel.model
        )
        updateVehicleImage(vehicleDetailResponseModel.image)
    }

    /**
     * update vehicle image in view
     */
    private fun updateVehicleImage(imageUrl: String?) {
        if (imageUrl!!.isNotEmpty())
            Picasso.get().load(imageUrl).placeholder(R.drawable.place_holder).into(ivVehicle)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val sydney = LatLng(-34.0, 151.0)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun setUpMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(readyCallBack)
    }

    private val readyCallBack = OnMapReadyCallback { googleMap ->

        mMap = googleMap

        mMap?.run {
            uiSettings.apply {

                setAllGesturesEnabled(true)
                isZoomGesturesEnabled = true
                isRotateGesturesEnabled = true

                isMapToolbarEnabled = true
                isZoomControlsEnabled = true
                isCompassEnabled = true

                isMyLocationButtonEnabled = isLocationPermissionGranted()
            }
            setPadding(0, 0, 0, 400)
            getCurrentLocation()
            setMaxZoomPreference(20.0f)
        }

        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap!!.setOnMapLongClickListener(this)
        geoCoder = Geocoder(this)

        if (is_From_Tag_Location) {
            showVehicleLocation()
            calculateDistance()
        }
    }

    /**
     * get current location
     */
    private fun getCurrentLocation(): LatLng {
        var latLng = LatLng(0.0, 0.0)
        location = gpsTracker.getLocation()

        if (location != null) {
            latLng = LatLng(location!!.latitude, location!!.longitude)
        }
//        else gpsTracker.showAlertForSettings()

        moveMarkerOnCurrentLocation(latLng)
        return latLng
    }

    /**
     * show marker on user current location
     */
    private fun moveMarkerOnCurrentLocation(latLng: LatLng) {
        if (marker == null)
            marker = mMap!!.addMarker(
                MarkerOptions().position(latLng).title(getString(R.string.text_me))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
            )
        else marker!!.position = latLng

        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 250F))

    }

    /**
     * show marker on vehicle's last saved location
     */
    private fun showVehicleLocation(): LatLng {
        val vehicleLatLng = LatLng(vehicleDetailResponseModel.lat!!, vehicleDetailResponseModel.lng!!)
        vehicleMarker = mMap!!.addMarker(
            MarkerOptions().position(vehicleLatLng).title(
                getString(
                    R.string.text_marker_title, vehicleDetailResponseModel.vin,
                    vehicleDetailResponseModel.year.toString(),
                    vehicleDetailResponseModel.make
                    , vehicleDetailResponseModel.model
                )
            ).icon(
                BitmapDescriptorFactory.fromResource(
                    R.drawable.mask_group
                )
            )
        )
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(vehicleLatLng, 200F))
        return vehicleLatLng
    }

    /**
     * calculate distance between vehicle's location and user current location
     */
    internal fun calculateDistance() {
        val userLatLng = getCurrentLocation()
        val vehicleLatLng = showVehicleLocation()

        val locationUser = Location("userLocation")
        val locationVehicle = Location("vehicleLocation")

        locationUser.latitude = userLatLng.latitude
        locationUser.longitude = userLatLng.longitude

        locationVehicle.latitude = vehicleLatLng.latitude
        locationVehicle.longitude = vehicleLatLng.longitude

        val distance: Float = locationUser.distanceTo(locationVehicle)
        Log.d(TAG, "Distance --> $distance")
        showDistanceInView(distance)

        val url = createUrl(userLatLng, vehicleLatLng)
        Log.d("onMapClick", url.toString())
    }

    /**
     * show distance of user and vehicle in view
     */
    private fun showDistanceInView(distance: Float) {
        var distanceInMeters: Double = distance.toDouble()
        val decim = DecimalFormat("0.00")

        if (distance <= 1609.0) {
            distanceInMeters = (distanceInMeters * 1.094)
            distanceInMeters = java.lang.Double.parseDouble(decim.format(distanceInMeters))
            tvDistanceValue.text = "$distanceInMeters Yard"
        } else {
            distanceInMeters /= 1609.344
            distanceInMeters = java.lang.Double.parseDouble(decim.format(distanceInMeters))
            tvDistanceValue.text = "$distanceInMeters Mi"
        }
    }

    /**
     * creating URL to the Google Directions API
     */
    private fun createUrl(userLatLng: LatLng, vehicleLatLng: LatLng) {
        // Origin of route
        val origin = "${userLatLng.latitude}" + "," + userLatLng.longitude

        // Destination of route
        val destination = "${vehicleLatLng.latitude}" + "," + vehicleLatLng.longitude

        // Sensor enabled
        val sensor = "false"

        val mode = "driving"

        val key = getString(R.string.google_maps_key)

        presenter.directionApiToGetRoute(origin, destination, sensor, mode, key)
    }

    /**
     * check for user subscription
     */

    private fun checkForScanSubscription() {
        if (checkForSubscription()) {
            showAlertOnSuccess(getString(R.string.text_location_save_msg))
        } else showAlertForSubscription()
    }

    /**
     * show route from user to vehicle location in map
     */
    private fun showRouteInMap(
        routePath: String?,
        focusStatus: Boolean,
        animStatus: Boolean
    ) {
        if (null != routePolyLine) {
            routePathList = null
            routePolyLine?.remove()
        }

        if (null != routePath) {
            routePathList = decodePoly(encoded = routePath)

            val polyOptions = PolylineOptions()
                .apply {
                    color(ContextCompat.getColor(this@TagLocationMapActivity, R.color.colorBlack))
                    width(8f)
                    addAll(routePathList)
                }

            routePolyLine = mMap!!.addPolyline(polyOptions)
            val latLng = LatLng(location!!.latitude, location!!.longitude)
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13F))
        }
    }

    /**
     * show alert on success response
     */
    private fun showAlertOnSuccess(message: String) {
        val alertDialog = android.app.AlertDialog.Builder(this, R.style.MyDialogTheme)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(getString(R.string.text_ok)) { dialog, _ ->
            dialog.cancel()
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    /**
     * show alert for scan subscription
     */
    private fun showAlertForSubscription() {
        val alertDialog = AlertDialog.Builder(this, R.style.MyDialogTheme)
        alertDialog.setMessage(getString(R.string.subscription_msg))
        alertDialog.setNegativeButton(getString(R.string.text_cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setPositiveButton(getString(R.string.text_buy)) { dialog, _ ->
            dialog.dismiss()
            openWebPage(ApiClient.BASE_URL + Constants.App.Api.USER_PROFILE)
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    /**
     * show alert on failure if subscription is expired
     */
    private fun showAlertSubscriptionExpired(message: String) {
        val alertDialog = AlertDialog.Builder(this, R.style.MyDialogTheme)
        alertDialog.setTitle(getString(R.string.text_alert))
        alertDialog.setMessage(message)
        alertDialog.setNegativeButton(getString(R.string.text_cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setPositiveButton(getString(R.string.text_renew)) { dialog, _ ->
            dialog.dismiss()
            openWebPage(ApiClient.BASE_URL + Constants.App.Api.USER_PROFILE)
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    /**
     * Request for location permissions if not granted
     */
    private fun requestPermissionsIfNotGranted() {
        when (isPermissionsGranted()) {
            true -> Handler().postDelayed(
                { checkLocationAndGpsStatus() },
                1000
            )

            false -> requestPermissions()
        }
    }

    private fun checkLocationAndGpsStatus() {
        if (isPermissionsGranted())
            if (isGPSEnabled()) {
                val isShowAgain =
                    SharedPreferencesManager.with(this).getBoolean(Constants.SharedPref.PREF_MSG_LONG_PRESS, false)
                if (isShowAgain && !is_From_Tag_Location) showAlertForLongPress()
                setUpMap()
            } else {
                showSettingsAlert()
            }
        else requestPermissions()
    }

    internal fun isPermissionsGranted(): Boolean {
        var isCoarsePermissionGranted = true
        var isFinePermissionGranted = true

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            isCoarsePermissionGranted = true
            isFinePermissionGranted = true
        } else {
            isCoarsePermissionGranted =
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            isFinePermissionGranted =
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

        Log.d(
            TAG,
            "isPermissionsGranted :: isCoarsePermissionGranted-$isCoarsePermissionGranted, isFinePermissionGranted-$isFinePermissionGranted"
        )

        return isCoarsePermissionGranted && isFinePermissionGranted
    }

    /**
     * Check for location permissions granted or not
     */
    internal fun isLocationPermissionGranted(): Boolean =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "Permission is granted")
                true
            } else {
                Log.d(TAG, "Permission is not granted")
                false
            }
        else {
            Log.d(TAG, "Permission is granted since API<23")
            true
        }

    /**
     * Request for Location permissions
     */
    private fun requestPermissions() =
        PermissionHelper.requestLocationPermission(
            mContext = this,
            requestCode = PermissionConst.REQUEST_CODE.LOCATION
        )

    /**
     * Check for Gps enabled or disabled
     */
    private fun isGPSEnabled(): Boolean =
        (getSystemService(Context.LOCATION_SERVICE) as LocationManager)
            .isProviderEnabled(LocationManager.GPS_PROVIDER)

    /**
     * Show Alert if Gps disable and send user to settings
     */
    private fun showSettingsAlert() {
        if (alertDialogSettings == null) {
            val alertDialog = android.support.v7.app.AlertDialog.Builder(this)

            alertDialog.apply {
                // Setting Dialog Title
                setTitle(getString(R.string.text_alert))
                setCancelable(false)

                // Setting Dialog Message
                setMessage(getString(R.string.app_name) + getString(R.string.text_gps_permission))

                // On pressing Settings button
                setPositiveButton(getString(R.string.text_settings)) { _, _ ->
                    startActivityForResult(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                        PermissionConst.REQUEST_CODE.GPS
                    )
                }
            }

            alertDialogSettings = alertDialog.create()
        }

        // Showing Alert Message
        alertDialogSettings!!.show()
    }

    /**
     * This method is fired if user denied with or without checked Never Show again in permission dialog
     */
    private fun requestPermissionOnDenied() =
        when (ActivityCompat.shouldShowRequestPermissionRationale(this, PermissionConst.PERMISSION.COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            PermissionConst.PERMISSION.FINE_LOCATION
        )) {
            true -> showRationalDialogForDeniedPermission()
            false -> showSettingDialogForDeniedAndNeverShowPermissions()
        }

    private fun showRationalDialogForDeniedPermission() {
        if (locationDialog == null) {
            val alertDialog = android.support.v7.app.AlertDialog.Builder(this)

            alertDialog.apply {
                setMessage(
                    String.format(
                        getString(R.string.text_alert_permissions),
                        getString(R.string.app_name)
                    )
                )
                setCancelable(false)

                setPositiveButton(getString(R.string.allow)) { dialog, _ ->
                    dialog.dismiss()
                    PermissionHelper.requestLocationPermission(
                        mContext = this@TagLocationMapActivity,
                        requestCode = PermissionConst.REQUEST_CODE.LOCATION
                    )
                }
            }

            locationDialog = alertDialog.create()
        }
        locationDialog?.show()
    }

    private fun showSettingDialogForDeniedAndNeverShowPermissions() {
        if (permissionsDialog == null) {
            val alertDialog = android.support.v7.app.AlertDialog.Builder(this)

            alertDialog.apply {
                setMessage(
                    String.format(
                        getString(R.string.text_alert_permissions_settings),
                        getString(R.string.app_name)
                    )
                )
                setCancelable(false)

                setPositiveButton(getString(R.string.text_settings)) { dialog, _ ->
                    dialog.dismiss()

                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse(String.format(getString(R.string.text_package), packageName))
                    startActivityForResult(intent, PermissionConst.REQUEST_CODE.SETTINGS)
                }
            }

            permissionsDialog = alertDialog.create()
        }

        permissionsDialog?.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PermissionConst.REQUEST_CODE.LOCATION -> {
                var isCoarsePermissionGranted = true
                var isFinePermissionGranted = true

                if (grantResults.isNotEmpty()) {
                    grantResults.indices.forEach { result ->
                        if (grantResults[result] != PackageManager.PERMISSION_GRANTED)
                            when (permissions[result]) {
                                PermissionConst.PERMISSION.COARSE_LOCATION -> isCoarsePermissionGranted = false
                                PermissionConst.PERMISSION.FINE_LOCATION -> isFinePermissionGranted = false
                            }
                    }

                    when (isCoarsePermissionGranted && isFinePermissionGranted) {
                        true -> {
                            Log.d(TAG, "Permission: Granted")

                            checkLocationAndGpsStatus()
                        }

                        false -> requestPermissionOnDenied()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PermissionConst.REQUEST_CODE.GPS -> {
                if (resultCode == RESULT_OK)
                    checkLocationAndGpsStatus()
            }

            PermissionConst.REQUEST_CODE.SETTINGS -> {
                checkPermission = true
                checkLocationAndGpsStatus()
            }
        }
    }

    /**
     * Show alert for message to user switch location with long press
     */
    private fun showAlertForLongPress() {
        val alertDialog = android.app.AlertDialog.Builder(this, R.style.MyDialogTheme)
        alertDialog.setMessage(getString(R.string.text_long_press_msg))
        alertDialog.setPositiveButton(getString(R.string.text_don_show_again)) { dialog, _ ->
            dialog.dismiss()

            SharedPreferencesManager.with(this).edit().putBoolean(Constants.SharedPref.PREF_MSG_LONG_PRESS, false)
                .apply()
        }
        alertDialog.setNegativeButton(getString(R.string.text_ok)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    /**
     * show marker on location after long click
     */
    override fun onMapLongClick(points: LatLng?) {
        if (!is_From_Tag_Location) {
            if (null == marker) {
                marker = mMap!!.addMarker(
                    MarkerOptions().position(points!!).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)).title(
                        getString(R.string.text_me)
                    )
                )
            } else {
                marker!!.position = points!!
            }
            locationAddress = geoCoder!!.getFromLocation(points.latitude, points.longitude, 1)
        }
    }

    override fun onMapClick(points: LatLng?) {

    }

    /**
     * When successful response or data retrieved from google directions api
     *
     * @param response is successful response from Api
     */
    override fun onSuccessRoute(response: GoogleMapPathModel) {

        val googleMapPathModel: GoogleMapPathModel? = response

        if (null != googleMapPathModel) {
            var routePath: String? = null

            if (!googleMapPathModel.routes.isNullOrEmpty())
                routePath = googleMapPathModel.routes!![0].overview_polyline?.points

            runOnUiThread {

                showRouteInMap(routePath = routePath, focusStatus = false, animStatus = false)
            }
        }
    }

    /**
     * When error occurred in getting successful response of google directions api
     *
     */
    override fun onFailureRoute() {

    }

    /**
     * When successful response or data retrieved from create save vehicle location and address Api
     *
     * @param response is successful response from Api
     */
    override fun onSuccess(responseModel: SaveVehicleDetailResponseModel) {
        hideProgressDialog()
        checkForScanSubscription()
    }

    /**
     * When error occurred in getting successful response of save vehicle location Api
     *
     * @param errorResponse for Error message
     */
    override fun onFailure(errorResponse: ErrorResponse) {
        hideProgressDialog()
        if (errorResponse.error.status_code == 429) {
            showAlertSubscriptionExpired(errorResponse.error.message)
        } else responseFailure(errorResponse)
    }

    override fun myLocationChanged(location: Location) {
        if (is_From_Tag_Location && mMap != null) {
            getCurrentLocation()
            showVehicleLocation()
            calculateDistance()
        }
    }

    override fun onBackPressed() = finish()
}
