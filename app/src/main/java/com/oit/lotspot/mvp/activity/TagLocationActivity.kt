package com.oit.lotspot.mvp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.oit.lotspot.R
import com.oit.lotspot.constants.Constants
import com.oit.lotspot.retrofit.response.VehicleDetailResponseModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_tag_location.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class TagLocationActivity : BaseActivity() {

    private var vehicleDetailResponseModel = VehicleDetailResponseModel.VehicleDetailFirstResponseModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_location)

        initUi()
        getIntentData()
        clickListener()
    }

    private fun initUi() {
        ivAppTitle.visibility = View.VISIBLE
    }

    private fun getIntentData() {
        when (intent.hasExtra(Constants.App.Bundle_Key.TAG_LOCATION)) {
            true -> {

                vehicleDetailResponseModel =
                    Gson().fromJson<VehicleDetailResponseModel.VehicleDetailFirstResponseModel>(
                        intent.getStringExtra(Constants.App.Bundle_Key.TAG_LOCATION),
                        VehicleDetailResponseModel.VehicleDetailFirstResponseModel::class.java
                    )

                setIntentDataInViews()
            }
        }
    }

    private fun setIntentDataInViews() {
        etVin.setText(vehicleDetailResponseModel.data.vin)
        etModel.setText(
            getString(
                R.string.text_car_model,
                vehicleDetailResponseModel.data.year.toString(),
                vehicleDetailResponseModel.data.make,
                vehicleDetailResponseModel.data.model
            )
        )

        Picasso.get().load(vehicleDetailResponseModel.data.image).placeholder(R.drawable.place_holder).into(ivVehicle)
    }

    /**
     * Click event on views
     */
    private fun clickListener() {
        ivMenu.setOnClickListener(clickListener)
        btnLocation.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivMenu -> onBackPressed()

                R.id.btnLocation -> startActivity(
                    Intent(this, TagLocationMapActivity::class.java)
                        .putExtra(
                            Constants.App.Bundle_Key.TAG_LOCATION_MAP,
                            Gson().toJson(vehicleDetailResponseModel.data)
                        ).putExtra(Constants.App.Bundle_Key.IS_FROM_TAG_LOCATION, false)
                )
            }
    }

    override fun onBackPressed() = finish()

}
