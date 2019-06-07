package com.oit.lotspot.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.oit.lotspot.R
import com.oit.lotspot.activity.ManifestActivity
import com.oit.lotspot.holder.ManifestHolder

class ManifestAdapter(private var context: ManifestActivity) : RecyclerView.Adapter<ManifestHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManifestHolder {
        return ManifestHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_manifest, parent, false),context)
    }

    override fun getItemCount(): Int {
        return 6
    }

    override fun onBindViewHolder(holder: ManifestHolder, position: Int) {
        holder.bindView()
    }
}