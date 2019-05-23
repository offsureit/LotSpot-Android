package com.oit.lotspot.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

 class ConnectionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, arg1: Intent) {
        if (connectivityReceiverListener != null) {
            connectivityReceiverListener!!.onNetworkConnectionChanged(isConnectedOrConnecting(context))
        }
    }

    /**
     * Check if the network interface is available,
     * doesn't guarantee a particular network service is available,
     * for example, there could be low signal or server downtime
     *
     * @param context
     */
    private fun isConnectedOrConnecting(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }
}