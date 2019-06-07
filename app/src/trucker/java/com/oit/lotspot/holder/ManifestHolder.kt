package com.oit.lotspot.holder

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import com.oit.lotspot.activity.TruckerMapActivity
import kotlinx.android.synthetic.trucker.item_manifest.view.*

class ManifestHolder(itemView: View, private var context: Context) : RecyclerView.ViewHolder(itemView) {

    init {

    }

    fun bindView() {

        itemView.btnFindCar.setOnClickListener {
            itemView.context.startActivity(Intent(context, TruckerMapActivity::class.java))
        }
    }
}