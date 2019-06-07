package com.oit.lotspot.constants

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

object PermissionHelper {

    internal fun checkPermission(mContext: Context, permission: String): Int =
        ContextCompat.checkSelfPermission(mContext, permission)

    internal fun requestPermission(mContext: Context, permissionArray: Array<String>, requestCode: Int) =
        ActivityCompat.requestPermissions(mContext as Activity, permissionArray, requestCode)

    internal fun requestPermission(mContext: Context, permission: String, requestCode: Int) =
        ActivityCompat.requestPermissions(mContext as Activity, arrayOf(permission), requestCode)

    internal fun hasRequiredLocationPermissionGranted(mContext: Context): Boolean =
        PermissionConst.PERMISSION.LOCATION_ARRAY
            .none { checkPermission(mContext, it) != PackageManager.PERMISSION_GRANTED }

    /**
     * This method is for Location Permission
     *
     * @param mContext
     * @param requestCode
     */
    internal fun requestLocationPermission(mContext: Context, requestCode: Int) {
        val permissionList: List<String> = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1)
            PermissionConst.PERMISSION.LOCATION_ARRAY
                .filter { checkPermission(mContext, it) != PackageManager.PERMISSION_GRANTED }
        else
            PermissionConst.PERMISSION.LOCATION_ARRAY
                .filter { checkPermission(mContext, it) != PackageManager.PERMISSION_GRANTED }

        if (permissionList.isNotEmpty())
            requestPermission(
                mContext = mContext,
                permissionArray = permissionList.toTypedArray(),
                requestCode = requestCode
            )
    }
}

