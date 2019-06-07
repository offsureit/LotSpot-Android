package com.oit.lotspot.activity

import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.oit.lotspot.R
import com.oit.lotspot.mvp.activity.BaseActivity
import kotlinx.android.synthetic.main.layout_toolbar.*

class TruckerMapActivity : BaseActivity(), OnMapReadyCallback {

    private var TAG = TruckerMapActivity::class.java.simpleName
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trucker_map)

        initUi()
        clickListener()
        setUpMap()
    }

    private fun initUi() {
        tvTitle.visibility = View.VISIBLE
        tvTitle.text = getString(R.string.map_title)
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

            }
            setPadding(0, 0, 0, 350)
            setMaxZoomPreference(20.0f)
        }

        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    /**
     * Click events on views
     */
    private fun clickListener() {
        ivMenu.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivMenu -> onBackPressed()
            }
    }

    override fun onBackPressed() = finish()
}
