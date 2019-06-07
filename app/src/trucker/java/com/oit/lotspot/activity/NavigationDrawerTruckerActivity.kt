package com.oit.lotspot.activity

import android.content.Intent
import android.os.BaseBundle
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.oit.lotspot.R
import com.oit.lotspot.constants.Constants
import com.oit.lotspot.mvp.activity.BaseActivity
import com.oit.lotspot.retrofit.ApiClient
import kotlinx.android.synthetic.trucker.activity_trucker_navigation.*
import kotlinx.android.synthetic.trucker.layout_navigation_drawer_trucker.*

open class NavigationDrawerTruckerActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trucker_navigation)

        initUi()
        clickListener()
    }

    private fun initUi() {
        drawerLayoutRoot.closeDrawer(Gravity.START)
        rootLayout.layoutParams.width = (resources.displayMetrics.widthPixels / 1.45).toInt()
    }

    /**
     * Click event on views
     */
    private fun clickListener() {
        llProfile.setOnClickListener(clickListener)
        llManifest.setOnClickListener(clickListener)
        llPolicy.setOnClickListener(clickListener)
        llTerms.setOnClickListener(clickListener)
        llLogout.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.llProfile -> clickedProfile()

                R.id.llManifest -> clickedManifest()

                R.id.llPolicy -> clickedPrivacyPolicy()

                R.id.llTerms -> clickedTermsConditions()

                R.id.llLogout -> clickedLogout()
            }
        drawerLayoutRoot.closeDrawer(Gravity.START)
    }

    private fun clickedProfile() {
        startActivity(Intent(this@NavigationDrawerTruckerActivity, ProfileActivity::class.java))
    }

    private fun clickedManifest() {
        startActivity(Intent(this@NavigationDrawerTruckerActivity, ManifestActivity::class.java))
    }

    private fun clickedPrivacyPolicy() {
        openWebPage(ApiClient.BASE_URL_LINKS + Constants.App.Api.PRIVACY)
    }

    private fun clickedTermsConditions() {
        openWebPage(ApiClient.BASE_URL_LINKS + Constants.App.Api.TERMS)
    }

    private fun clickedLogout() {
        showAlertForLogout()
    }

    /**
     * Show Alert dialog for Logout
     */
    private fun showAlertForLogout() {
        val alertDialog = android.app.AlertDialog.Builder(this, R.style.MyDialogTheme)
        alertDialog.setMessage(getString(R.string.text_logout_alert))
        alertDialog.setPositiveButton(getString(R.string.text_yes)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setNegativeButton(getString(R.string.text_no)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }
}
