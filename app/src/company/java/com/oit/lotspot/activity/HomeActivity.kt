package com.oit.lotspot.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Gravity
import android.view.View
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder
import com.oit.lotspot.R
import com.oit.lotspot.constants.Constants
import com.google.android.gms.vision.barcode.Barcode
import com.google.gson.Gson
import com.oit.lotspot.database.SharedPreferencesManager
import com.oit.lotspot.presenter.HomePresenter
import com.oit.lotspot.retrofit.request.VehicleDetailRequest
import com.oit.lotspot.retrofit.response.ErrorResponse
import com.oit.lotspot.retrofit.response.LoginResponseModel
import com.oit.lotspot.retrofit.response.VehicleDetailResponseModel
import com.oit.lotspot.database.DatabaseHelper
import com.oit.lotspot.retrofit.ApiClient
import kotlinx.android.synthetic.company.activity_home.*
import kotlinx.android.synthetic.company.activity_navigation_drawer.*
import kotlinx.android.synthetic.company.layout_home.*


class HomeActivity : NavigationDrawerActivity(), HomePresenter.ResponseCallBack {

    private var TAG = HomeActivity::class.java.simpleName
    private var barcodeResult: Barcode? = null
    private lateinit var presenter: HomePresenter
    private var vinNumber = ""
    private var vehicleDetailRequest = VehicleDetailRequest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_home, flContainer)

        initPresenter()
        clickListener()
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = HomePresenter(this)
        hitApiForCompanyProfile()
    }

    /**
     * Click event on views
     */
    private fun clickListener() {
        ivMenu.setOnClickListener(clickListener)
        ivScanVin.setOnClickListener(clickListener)
        ivVinText.setOnClickListener(clickListener)
        etVinNumber.setOnClickListener(clickListener)
        ivBtnSearch.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {

                R.id.ivMenu -> drawerLayoutRoot.openDrawer(Gravity.START)

                R.id.ivScanVin -> checkCameraPermission()

                R.id.ivVinText -> enableSearchViewForEnterVin()

                R.id.etVinNumber -> enableViewForEnterVin()

                R.id.ivBtnSearch -> {
                    if (validateData()) {
                        updateVinNumber(etVinNumber.text.toString())
                    }
                }
            }
    }

    private fun enableViewForEnterVin() {
        etVinNumber.isCursorVisible = true
        ivBtnSearch.isClickable = true
    }

    private fun enableSearchViewForEnterVin() {
        enableViewForEnterVin()
        showKeyboard(etVinNumber)
    }

    /**
     * Validate fields
     */
    private fun validateData(): Boolean {
        if (etVinNumber.text.isEmpty()) {
            showAlert(getString(R.string.empty_vin_msg))
            return false
        }
        return true
    }

    /**
     * Request for Permissions of camera & external storage
     */
    private fun checkCameraPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    Constants.Permission.CAMERA_STORAGE,
                    Constants.RequestCode.CAMERA
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    Constants.Permission.CAMERA_STORAGE,
                    Constants.RequestCode.CAMERA
                )
                return true
            }
        }
        return true
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Constants.RequestCode.CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScanningBarcode()
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            android.Manifest.permission.CAMERA
                        )
                    ) {
                        // now, user has denied permission (but not permanently!)
                        showAlertForPermissions()
                    } else
                        showAlertForSettings()
                }
                return
            }
        }
    }

    /**
     * Show dialog if user deny permissions permanently
     */
    private fun showAlertForSettings() {
        val alertDialog = android.app.AlertDialog.Builder(this, R.style.MyDialogTheme)
        alertDialog.setMessage(getString(R.string.text_access_permission_msg))
        alertDialog.setPositiveButton(getString(R.string.text_settings)) { dialog, _ ->
            sendUserToSettings()
        }
        alertDialog.setNegativeButton(getString(R.string.text_cancel)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    /**
     * Show dialog if user deny permissions but temporary
     */
    private fun showAlertForPermissions() {
        val alertDialog = android.app.AlertDialog.Builder(this, R.style.MyDialogTheme)
        alertDialog.setMessage(getString(R.string.text_access_permission_msg))
        alertDialog.setPositiveButton(getString(R.string.text_ok)) { _, _ ->
            ActivityCompat.requestPermissions(this, Constants.Permission.CAMERA_STORAGE, Constants.RequestCode.CAMERA)
        }
        alertDialog.setNegativeButton(getString(R.string.text_no)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    /**
     * show alert for subscription
     */
    private fun showAlertForSubscription() {
        val alertDialog = AlertDialog.Builder(this, R.style.MyDialogTheme)
        alertDialog.setTitle(getString(R.string.text_alert))
        alertDialog.setMessage(getString(R.string.subscription_msg))
        alertDialog.setNegativeButton(getString(R.string.text_cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setPositiveButton(getString(R.string.text_renew)) { dialog, _ ->
            dialog.dismiss()
            openWebPage(ApiClient.BASE_URL_LIVE + Constants.App.Api.USER_LOGIN)
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    /**
     * Send user into settings (if user deny permissions permanently)
     */
    private fun sendUserToSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.data = Uri.parse("package:" + this.packageName)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        this.startActivity(intent)
    }

    /**
     * call for scanning a barcode
     */
    private fun startScanningBarcode() {
        val materialBarcodeScanner = MaterialBarcodeScannerBuilder()
            .withActivity(this@HomeActivity)
            .withEnableAutoFocus(true)
            .withBleepEnabled(true)
            .withBackfacingCamera()
            .withText("Scanning...")
            .withResultListener { barcode ->
                barcodeResult = barcode

                updateVinNumber(barcode.rawValue)
            }
            .build()
        materialBarcodeScanner.startScan()
    }

    /**
     * update vin number in request model for api
     */
    private fun updateVinNumber(vinNumber: String) {
        this.vinNumber = vinNumber
        vehicleDetailRequest.apply {
            vin = vinNumber
        }
        setScannedVinNumber(vinNumber)
    }

    /**
     * set scanned vin number in view
     */
    private fun setScannedVinNumber(vinNumber: String?) {
        this.vinNumber = vinNumber!!
        val arrayList = DatabaseHelper(this).getVehicleDetail()
        val index = arrayList.firstOrNull { it.vin == vinNumber }?.let {
            arrayList.indexOf(it)
        } ?: -1
        if (index > -1) {
            startActivity(
                Intent(this, TagLocationActivity::class.java)
                    .putExtra(Constants.App.Bundle_Key.TAG_LOCATION, Gson().toJson(arrayList[index]))
            )
        } else {
            Handler().postDelayed({
                Log.d(TAG, " Api hit for Get details postDelayed")
                hitApiToGetVehicleDetails()
            }, 600)
        }
    }

    private fun checkForUserSubScription(response: VehicleDetailResponseModel.VehicleDetailFirstResponseModel) {
        val subscription = checkForSubscription()
        if (subscription) {
            startActivity(
                Intent(this, TagLocationActivity::class.java)
                    .putExtra(Constants.App.Bundle_Key.TAG_LOCATION, Gson().toJson(response.data))
            )
            DatabaseHelper(this).saveVehicleRecords(response.data)
        } else showAlertForSubscription()
    }

    /**
     * Call for Api Company Profile
     */
    private fun hitApiForCompanyProfile() {
        val authToken = getAuthToken()
        Log.d(TAG, " Auth Token in Home ::--> $authToken")
        presenter.apiGetForCompanyProfile(authToken)
    }

    /**
     * Call for api to get vehicle details from vin number
     */
    private fun hitApiToGetVehicleDetails() {
        Log.d(TAG, " Api hit for Get details")
        showProgressView()
        val authToken = getAuthToken()
        presenter.apiGetForVehicleDetails(authToken, vehicleDetailRequest)
    }

    /**
     * when successful response or data retrieved from api get vehicle details from vin number
     *
     * @param response successful response from api
     */
    override fun onSuccess(response: VehicleDetailResponseModel.VehicleDetailFirstResponseModel) {
        hideProgressDialog()
        checkForUserSubScription(response)
    }

    /**
     * When error occurred in getting successful response of get vehicle details from vin number
     *
     * @param errorResponse for Error message
     */
    override fun onFailure(errorResponse: ErrorResponse) {
        hideProgressDialog()
        responseFailure(errorResponse)
    }

    /**
     * when successful response or data retrieved from api Company Profile
     *
     * @param response successful response from api
     */
    override fun onSuccessProfile(response: LoginResponseModel.LoginResponseProfileModel) {

    }
}

