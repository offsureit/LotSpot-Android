package com.oit.lotspot.constants

import android.Manifest

/**
 * All data and constants regarding the Permissions are listed here
 */
interface PermissionConst {

    object REQUEST_CODE {
        const val LOCATION = 101
        const val GPS = 102
        const val SETTINGS = 103
    }

    object PERMISSION {
        const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION


        val LOCATION_ARRAY = arrayOf(FINE_LOCATION,COARSE_LOCATION)

    }

}
