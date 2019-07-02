package com.oit.lotspot.adapter

import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.oit.lotspot.R
import com.oit.lotspot.interfaces.HistoryItemInterface
import com.oit.lotspot.activity.HistoryActivity
import com.oit.lotspot.holder.HistoryHolder
import com.oit.lotspot.retrofit.response.VehicleListResponseModel
import android.widget.Filter
import android.widget.Filterable
import com.oit.lotspot.retrofit.response.VehicleDetailResponseModel
import kotlinx.android.synthetic.company.item_swipe_delete.view.*
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

    /**
     * update Vehicle Response list
     */
    internal fun updateVehicleList(vehicleResponseModel: VehicleListResponseModel) {

        val startPosition = this.vehicleListFirstResponseModel.data.size
        this.vehicleListFirstResponseModel.data.addAll(vehicleResponseModel.data)

        this.filteredVehicleList.data.clear()
        this.filteredVehicleList.data.apply {
            addAll(vehicleListFirstResponseModel.data)
        }
        Log.d(
            "size", "size of adapter list 1 ${this.vehicleListFirstResponseModel.data
                .size}"
        )
        notifyItemRangeInserted(startPosition, vehicleResponseModel.data.size)
    }

    /**
     * remove single vehicle from All vehicle history
     */
    internal fun deleteItem(vehicleId: Int): Int {
        val index = filteredVehicleList.data.firstOrNull { it.id == vehicleId }?.let {
            filteredVehicleList.data.indexOf(it)
        } ?: -1
        if (index > -1) {
            filteredVehicleList.data.removeAt(index)
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
        Log.d(
            "size", "size of adapter list 2 ${filteredVehicleList.data
                .size}"
        )
        holder.findView(filteredVehicleList, historyItemInterface)
    }

    override fun getFilter(): android.widget.Filter {
        return object : Filter() {

            override fun performFiltering(query: CharSequence?): FilterResults {
                val filterResults = Filter.FilterResults()

                if (query != null && query.isNotEmpty()) {
                    val filteredList = VehicleListResponseModel()

                    for (i in 0 until vehicleListFirstResponseModel.data.size) {
                        if (vehicleListFirstResponseModel.data[i].vin!!.contains(query)) {
                            filteredList.data.add(vehicleListFirstResponseModel.data[i])
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