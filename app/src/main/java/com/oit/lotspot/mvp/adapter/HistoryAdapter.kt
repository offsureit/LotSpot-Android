package com.oit.lotspot.mvp.adapter

import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.oit.lotspot.R
import com.oit.lotspot.interfaces.HistoryItemInterface
import com.oit.lotspot.mvp.activity.HistoryActivity
import com.oit.lotspot.mvp.holder.HistoryHolder
import com.oit.lotspot.retrofit.response.VehicleListResponseModel
import kotlinx.android.synthetic.main.item_swipe_delete.view.*
import android.widget.Filter
import android.widget.Filterable
import com.oit.lotspot.retrofit.response.VehicleDetailResponseModel
import java.util.ArrayList


class HistoryAdapter(private var context: HistoryActivity, private var historyItemInterface: HistoryItemInterface) :
    RecyclerView.Adapter<HistoryHolder>(), Filterable {

    private var vehicleListFirstResponseModel = VehicleListResponseModel()
    private var filteredVehicleList = VehicleListResponseModel()
    private val metrics = DisplayMetrics()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): HistoryHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.swipe_item_left, parent, false)

        context.windowManager.defaultDisplay.getMetrics(metrics)

        if (itemView.clDelete.layoutParams != null) {
            itemView.clDelete.layoutParams.width = (metrics.widthPixels / 3.3).toInt()
            itemView.clDelete.layoutParams.height = (metrics.heightPixels / 5.5).toInt()
        }

        return HistoryHolder(itemView)
    }

    override fun getItemCount(): Int {
        return filteredVehicleList.data.size
    }

    internal fun updateVehicleList(vehicleResponseModel: VehicleListResponseModel) {
        this.vehicleListFirstResponseModel = vehicleResponseModel

        this.filteredVehicleList.data.apply {
            clear()
            addAll(vehicleListFirstResponseModel.data)
        }
        notifyDataSetChanged()
    }

    /**
     * remove single vehicle from All vehicle history
     */
    internal fun deleteItem(vehicleId: Int): Int {
        val index = vehicleListFirstResponseModel.data.firstOrNull { it.id == vehicleId }?.let {
            vehicleListFirstResponseModel.data.indexOf(it)
        } ?: -1
        if (index > -1) {
            vehicleListFirstResponseModel.data.removeAt(index)
            notifyItemRemoved(index)
        }
        return index
    }

    /**
     * Clear all vehicle history
     */
    internal fun clearVehicleHistory() {
        this.filteredVehicleList.data.clear()
        this.vehicleListFirstResponseModel.data.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        holder.findView(filteredVehicleList, historyItemInterface)
    }

    override fun getFilter(): android.widget.Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                val filterResults = Filter.FilterResults()

                if (query != null && query.isNotEmpty()) {
                    val filteredList = VehicleListResponseModel()

                    for (i in 0 until filteredVehicleList.data.size) {
                        if (filteredVehicleList.data[i].vin!!.contains(query)) {
                            filteredList.data.add(filteredVehicleList.data[i])
                        }
                    }

                    filterResults.count = filteredList.data.size
                    filterResults.values = filteredList.data
                    Log.d("adapter", "size of searched data with query--> ${filteredList.data.size}")
                } else {

                    filterResults.count = vehicleListFirstResponseModel.data.size
                    filterResults.values = vehicleListFirstResponseModel.data
                    Log.d(
                        "adapter",
                        "size of searched data with query--> ${vehicleListFirstResponseModel.data.size}"
                    )
                }
                return filterResults
            }

            override fun publishResults(query: CharSequence?, filterResults: FilterResults?) {
                if (filterResults!!.values != null) {
                    filteredVehicleList.data =
                        filterResults.values as ArrayList<VehicleDetailResponseModel.VehicleDetailDataResponseModel>
                }
                notifyDataSetChanged()
            }
        }
    }
}