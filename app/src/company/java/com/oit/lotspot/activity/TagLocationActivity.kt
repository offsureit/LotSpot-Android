package com.oit.lotspot.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.oit.lotspot.R
import com.oit.lotspot.constants.Constants
import com.oit.lotspot.mvp.activity.BaseActivity
import com.oit.lotspot.retrofit.response.VehicleDetailResponseModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.company.activity_tag_location.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class TagLocationActivity : BaseActivity() {

    private var vehicleDetailResponseModel = VehicleDetailResponseModel.VehicleDetailDataResponseModel()

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
                    Gson().fromJson<VehicleDetailResponseModel.VehicleDetailDataResponseModel>(
                        intent.getStringExtra(Constants.App.Bundle_Key.TAG_LOCATION),
                        VehicleDetailResponseModel.VehicleDetailDataResponseModel::class.java
                    )
                setIntentDataInViews()
            }
        }
    }

    private fun setIntentDataInViews() {
        etVin.setText(vehicleDetailResponseModel.vin)
        etModel.setText(
            getString(
                R.string.text_car_model,
                vehicleDetailResponseModel.year.toString(),
                vehicleDetailResponseModel.make,
                vehicleDetailResponseModel.model
            )
        )

        if (vehicleDetailResponseModel.image == "") Picasso.get().load(R.drawable.place_holder).placeholder(R.drawable.place_holder).into(
            ivVehicle
        )
        else Picasso.get().load(vehicleDetailResponseModel.image).placeholder(R.drawable.place_holder).into(ivVehicle)
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

                R.id.btnLocation -> {
                    startActivity(
                        Intent(this, TagLocationMapActivity::class.java)
                            .putExtra(
                                Constants.App.Bundle_Key.TAG_LOCATION_MAP,
                                Gson().toJson(vehicleDetailResponseModel)
                            ).putExtra(Constants.App.Bundle_Key.IS_FROM_TAG_LOCATION, false)
                    )
                    finish()
                }
            }
    }

    override fun onBackPressed() = finish()

}
