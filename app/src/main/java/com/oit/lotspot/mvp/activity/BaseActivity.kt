package com.oit.lotspot.mvp.activity

import android.app.AlertDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.oit.lotspot.R
import com.oit.lotspot.constants.Constants
import com.oit.lotspot.database.SharedPreferencesManager
import com.oit.lotspot.mvp.dialog.ProgressDialog
import com.oit.lotspot.retrofit.response.LoginResponseModel
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.view.MotionEvent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.oit.lotspot.database.DatabaseHelper
import com.oit.lotspot.mvp.dialog.InternetDialog
import com.oit.lotspot.receiver.ConnectionReceiver
import com.oit.lotspot.retrofit.response.ErrorResponse
import java.math.BigDecimal


open class BaseActivity : AppCompatActivity(), ConnectionReceiver.ConnectivityReceiverListener {

    private var progressDialog: ProgressDialog? = null
    private var lastClickedMilliseconds: Long = 0L
    private var latLngBounds: LatLngBounds.Builder? = null
    private var context = this
    private var isInternetConnected: Boolean? = null
    var databaseHelper = DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver()
    }

    override fun onResume() {
        super.onResume()
        ConnectionReceiver.connectivityReceiverListener = this@BaseActivity
    }

    fun showToast(message: String) =
        Toast.makeText(this@BaseActivity, message, Toast.LENGTH_SHORT).show()

    /**
     * call for show progess loader
     */
    internal fun showProgressView() {
        if (progressDialog == null)
            progressDialog = ProgressDialog(this)

        progressDialog!!.show()
    }

    /**
     * call for hide progress loader
     */
    fun hideProgressDialog() {
        if (progressDialog != null)
            progressDialog!!.dismiss()
    }

    /**
     * restrict to entering emojis in view
     */
    internal var emojiFilter: InputFilter = InputFilter { source, start, end, dest, dstart, dend ->
        for (index in start until end) {
            val type = Character.getType(source[index])

            if (type == Character.SURROGATE.toInt())
                return@InputFilter ""
        }
        null
    }

    internal fun shouldProceedClick(): Boolean {
        var status = false

        if (System.currentTimeMillis() - lastClickedMilliseconds > Constants.Duration.TIME) {
            lastClickedMilliseconds = System.currentTimeMillis()
            status = true
        }

        return status
    }

    internal fun formatDistance(context: Context, price: BigDecimal): String =
        String.format(
            context.getString(R.string.format_distance),
            price.setScale(Constants.Number.TWO, BigDecimal.ROUND_HALF_EVEN).toString()
        )

    /**
     * show alert dialog
     */
    internal fun showAlert(message: String) {
        val alert = AlertDialog.Builder(this@BaseActivity, R.style.MyDialogTheme)
        alert.setTitle(getString(R.string.text_alert))
        alert.setMessage(message)
        alert.setPositiveButton(getString(R.string.text_ok)) { dialog, _ ->
            dialog.dismiss()
        }
        alert.show()
    }

    /**
     * Here is call to show alert when session of user is expire,
     * Profile is blocked
     */
    private fun showAlertForSession(message: String) {
        val alertDialog = android.support.v7.app.AlertDialog.Builder(
            this@BaseActivity, R.style.MyDialogTheme
        )
        // Setting DialogAction Message
        alertDialog.setTitle(getString(R.string.text_alert))
        alertDialog.setMessage(message)
        // On pressing dialog button
        alertDialog.setPositiveButton(getString(R.string.text_ok)) { dialog, _ ->
            dialog.cancel()
            startActivity(Intent(this@BaseActivity, LoginActivity::class.java))
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    /**
     * show alert for Internet connection
     */
    private fun showAlertForInternet(message: String) {
        val alertDialog = AlertDialog.Builder(this, R.style.MyDialogTheme)
        alertDialog.setTitle(getString(R.string.text_alert))
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(getString(R.string.text_ok)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    /**
     * get auth token from shared preferences
     */
    internal fun getAuthToken(): String {
        val model = Gson().fromJson(
            SharedPreferencesManager
                .with(this).getString(Constants.SharedPref.PREF_USER_PROFILE, ""),
            LoginResponseModel.LoginResponseFirstModel::class.java
        )

        val authToken = model.token

        return authToken!!
    }

    /**
     * clear token when session expire
     */
    internal fun clearToken() {
        val model = Gson().fromJson(
            SharedPreferencesManager.with(this)
                .getString(Constants.SharedPref.PREF_USER_PROFILE, ""),
            LoginResponseModel.LoginResponseFirstModel::class.java
        )

        if (model.token!!.isNotEmpty())
            model.apply { token = "" }

        SharedPreferencesManager.with(this)
            .edit()
            .putString(Constants.SharedPref.PREF_USER_PROFILE, Gson().toJson(model))
            .putBoolean(Constants.SharedPref.PREF_IS_USER_LOGIN, false)
            .putBoolean(Constants.SharedPref.PREF_MSG_LONG_PRESS, false)
            .apply()
    }

    /**
     * check for user subscription
     */
    internal fun checkForSubscription(): Boolean {
        var subscription = false
        val model = Gson().fromJson(
            SharedPreferencesManager.with(this).getString(Constants.SharedPref.PREF_USER_PROFILE, ""),
            LoginResponseModel.LoginResponseFirstModel::class.java
        )

        if (model.profile.activeSubscriptions != null) {
            subscription = true
        }
        return subscription
    }


    /**
     * Check failure status code of api
     */
    internal fun responseFailure(errorResponse: ErrorResponse) {
        when (errorResponse.error.status_code) {
            422 -> {
                if (errorResponse.error.errors.contact!![0].contains(getString(R.string.text_user_not_exists)))
                    (context as VerificationActivity).showAlertForSignUp(errorResponse.error.errors.contact!![0])
            }
            401 -> {
                clearToken()
                showAlertForSession(errorResponse.error.message)
            }
            else -> {
                if (errorResponse.error.message.isNotEmpty())
                    showToast(errorResponse.error.message)
            }
        }
    }

    internal fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString().trim())
            }
        })
    }

    fun showKeyboard(ettext: EditText) {
        ettext.requestFocus()
        val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.showSoftInput(ettext, 0)
    }

    fun hideSoftKeyboard(view: View) {
        val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.pointerCount > 1 && !enableMultiTouch())
            return true

        currentFocus?.apply {
            if (this is EditText
                && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE)
                && !javaClass.simpleName.startsWith("android.webkit.")
            ) {
                val scrcoords = IntArray(2)
                this.getLocationOnScreen(scrcoords)

                val x = ev.rawX + left - scrcoords[0]
                val y = ev.rawY + top - scrcoords[1]

                if (x < left || x > right || y < top || y > bottom)
                    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(window.decorView.applicationWindowToken, 0)
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    private fun enableMultiTouch(): Boolean = true


    /**
     * Method to decode polyline points
     */
    internal fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0

            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1

            lat += dlat
            shift = 0
            result = 0

            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1

            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)

            poly.add(p)
        }

        return poly
    }

    protected fun addLocationForFocusingMap(
        mMap: GoogleMap?,
        latLngBounds: LatLngBounds.Builder?,
        location: LatLng?,
        focusStatus: Boolean,
        animStatus: Boolean
    ) {
        if (location != null) {
            latLngBounds!!.include(location)

            mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds!!.build(), 200))

//            if (focusStatus)
//                focusMap(mMap, latLngBounds, animStatus)
        }
    }

    private fun focusMap(mMap: GoogleMap?, latLngBounds: LatLngBounds.Builder?, animStatus: Boolean) {
        mMap?.apply {
            when (animStatus) {
                true -> animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds!!.build(), 200))
                false -> moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds!!.build(), 200))
            }
        }
    }

    protected fun initLatLngBounds() {
        latLngBounds = LatLngBounds.Builder()
    }


    private fun registerReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(ConnectionReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
    }

    /**
     * This method is called to show the toast when internet is not connected.
     */
    private fun showMessage(isConnected: Boolean) {
        if (!isConnected)
            showAlertForInternet(getString(R.string.no_internet_connection))
    }

    /**
     * Callback will be called when there is change
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }

}
