package com.oit.lotspot.mvp.holder

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import com.daimajia.swipe.SwipeLayout
import com.google.gson.Gson
import com.oit.lotspot.R
import com.oit.lotspot.constants.Constants
import com.oit.lotspot.interfaces.HistoryItemInterface
import com.oit.lotspot.mvp.activity.TagLocationMapActivity
import com.oit.lotspot.retrofit.response.VehicleListResponseModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_history.view.*
import kotlinx.android.synthetic.main.item_swipe_delete.view.*
import kotlinx.android.synthetic.main.swipe_item_left.view.*
import java.lang.Exception


class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    fun findView(
        vehicleResponseModel: VehicleListResponseModel,
        historyItemInterface: HistoryItemInterface
    ) {

        itemView.apply {

            tvVinNumber.text = vehicleResponseModel.data[adapterPosition].vin
            tvVehicleName.text = context.getString(
                R.string.text_car_model,
                vehicleResponseModel.data[adapterPosition].year.toString(),
                vehicleResponseModel.data[adapterPosition].make,
                vehicleResponseModel.data[adapterPosition].model
            )

            updateImage(vehicleResponseModel.data[adapterPosition].image!!)

            clDelete.setOnClickListener {
                if (Constants.ProceedClick.shouldProceedClick())
                    historyItemInterface.onItemSelected(
                        vehicleResponseModel.data[adapterPosition].id!!
                    )
            }

            contentView.setOnClickListener {

                if (Constants.ProceedClick.shouldProceedClick())
                    if(swipe.openStatus == SwipeLayout.Status.Close) {
                        itemView.context.startActivity(
                            Intent(itemView.context, TagLocationMapActivity::class.java)
                                .putExtra(
                                    Constants.App.Bundle_Key.TAG_LOCATION_MAP,
                                    Gson().toJson(vehicleResponseModel.data[adapterPosition])
                                ).putExtra(Constants.App.Bundle_Key.IS_FROM_TAG_LOCATION, true)
                        )
                   }
            }
        }
    }

    private fun updateImage(uri: String) {
        if (uri.isNotEmpty())
            Picasso.get().load(uri)
                .placeholder(R.drawable.place_holder)
                .into(itemView.ivVehicle, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {
                        Picasso.get().load(R.drawable.place_holder).into(itemView.ivVehicle)
                    }
                })
    }

}