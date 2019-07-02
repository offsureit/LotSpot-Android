package com.oit.lotspot.constants

import android.Manifest

object Constants {

    interface App {
        companion object {
            const val CONTACT = "contact"
            const val AUTHORIZATION = "Bearer "

        }

        object Bundle_Key {
            const val TAG_LOCATION = "tag_location"
            const val TAG_LOCATION_MAP = "tag_location_map"
            const val IS_FROM_TAG_LOCATION = "from_tag_location"
        }

        object Api {
            const val TERMS = "site/#/terms-conditions"
            const val PRIVACY = "site/#/privacy-policy"
            const val USER_PROFILE = "#/dashboard/profile"
        }
    }

    interface SharedPref {
        companion object {
            val PREF_USER_PROFILE = "user_profile"
            val PREF_USER_CONTACT = "user_contact"
            val PREF_DEVICE_TOKEN = "pref_device_token"
            val PREF_IS_USER_LOGIN = "pref_is_user_login"
            val PREF_MSG_LONG_PRESS = "pref_msg_long_press"
            val PREF_IS_INTERNET_CONNECTED = "pref_is_internet_connected"
        }
    }


    object Country {
        const val Default = 92
    }

    object Permission {
        private const val CAMERA = Manifest.permission.CAMERA
        private const val FINE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        private const val COARSE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        val CAMERA_STORAGE = arrayOf(CAMERA)
        val LOCATION = arrayOf(FINE_LOCATION, COARSE_LOCATION)
    }

    object RequestCode {
        const val CAMERA = 1001
        const val LOCATION_REQUEST = 1002
        const val SETTINGS = 1003
    }

    object Duration {
        const val TIME = 300
        const val RESEND_TIMER_END = 0
        const val RESEND_TIMER_start = 1 * 1000
    }

    object ProceedClick {
        private var lastClickedMilliseconds: Long = 0L

        internal fun shouldProceedClick(): Boolean {
            var status = false

            if (System.currentTimeMillis() - lastClickedMilliseconds > Duration.TIME) {
                lastClickedMilliseconds = System.currentTimeMillis()
                status = true
            }

            return status
        }
    }

    object Number {
        const val ZERO = 0

        const val ONE = 1

        const val TWO = 2

        const val THREE = 3

        const val FOUR = 4

        const val FIVE = 5

        const val SIX = 6

        const val SEVEN = 7

        const val EIGHT = 8

        const val NINE = 9

    }

    object DefaultValue {
        const val OtpSize = 6
        const val DeviceType = "android"
    }
}