package com.oit.lotspot.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.Gravity
import android.view.View
import com.oit.lotspot.R
import com.oit.lotspot.constants.Constants
import com.oit.lotspot.mvp.activity.BaseActivity
import com.oit.lotspot.mvp.activity.LoginActivity
import com.oit.lotspot.presenter.NavigationPresenter
import com.oit.lotspot.retrofit.ApiClient
import com.oit.lotspot.retrofit.response.ErrorResponse
import com.oit.lotspot.retrofit.response.SaveVehicleDetailResponseModel
import kotlinx.android.synthetic.company.activity_navigation_drawer.*
import kotlinx.android.synthetic.company.drawer_navigation.*


open class NavigationDrawerActivity : BaseActivity(), NavigationPresenter.ResponseCallBack {

    private lateinit var presenter: NavigationPresenter
    private var TAG = NavigationDrawerActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_drawer)

        initUi()
        initPresenter()
        clickListener()
        addDrawerListener()
    }

    private fun initUi() {
        drawerLayoutRoot.closeDrawer(Gravity.START)
        rootLayout.layoutParams.width = (resources.displayMetrics.widthPixels / 1.45).toInt()
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        Log.d(TAG,"authToken -->${getAuthToken()}")
        presenter = NavigationPresenter(this)
    }

    /**
     * Click event on views
     */
    private fun clickListener() {
        llHistory.setOnClickListener(clickListener)
        llPrivacy.setOnClickListener(clickListener)
        llTerms.setOnClickListener(clickListener)
        clLogout.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.llHistory -> startActivity(Intent(this, HistoryActivity::class.java))

                R.id.llPrivacy -> openWebPage(ApiClient.BASE_URL_LINKS + Constants.App.Api.PRIVACY)

                R.id.llTerms -> openWebPage(ApiClient.BASE_URL_LINKS + Constants.App.Api.TERMS)

                R.id.clLogout -> showAlertForLogout()

            }
        drawerLayoutRoot.closeDrawer(Gravity.START)
    }


    private fun addDrawerListener() {

        drawerLayoutRoot.addDrawerListener(object : DrawerLayout.DrawerListener {

            override fun onDrawerStateChanged(view: Int) {


            }

            override fun onDrawerSlide(view: View, p1: Float) {

            }

            override fun onDrawerClosed(view: View) {

            }

            override fun onDrawerOpened(view: View) {
            }

        })
    }

    /**
     * Show Alert dialog for Logout
     */
    private fun showAlertForLogout() {
        val alertDialog = android.app.AlertDialog.Builder(this, R.style.MyDialogTheme)
        alertDialog.setMessage(getString(R.string.text_logout_alert))
        alertDialog.setPositiveButton(getString(R.string.text_yes)) { dialog, _ ->
            dialog.cancel()
            clickedForLogout()
        }
        alertDialog.setNegativeButton(getString(R.string.text_no)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    /**
     * Call for Api logout
     */
    private fun clickedForLogout() {
        showProgressView()
        val authToken = getAuthToken()
        presenter.apiPostToLogoutUser(authToken)
    }

    /**
     * when successful response or data retrieved from api logout
     *
     * @param response successful response from api
     */
    override fun onSuccess(responseModel: SaveVehicleDetailResponseModel) {
        hideProgressDialog()
        clearToken()
        startActivity(Intent(this@NavigationDrawerActivity, LoginActivity::class.java))
        finish()
    }

    /**
     * When error occurred in getting successful response of get logout
     *
     * @param errorResponse for Error message
     */
    override fun onFailure(errorResponse: ErrorResponse) {
        hideProgressDialog()
        responseFailure(errorResponse)
    }

    override fun onBackPressed() = finish()
}
