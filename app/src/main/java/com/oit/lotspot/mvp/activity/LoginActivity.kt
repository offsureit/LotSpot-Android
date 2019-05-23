package com.oit.lotspot.mvp.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.oit.lotspot.R
import com.oit.lotspot.constants.Constants
import com.oit.lotspot.retrofit.ApiClient
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initUi()
        clickListener()
    }

    private fun initUi() {
        disableViewForCountryCode()
    }

    private fun disableViewForCountryCode() {
        spCountryCode.ccpDialogShowTitle = false
        spCountryCode.isSearchAllowed = false
        spCountryCode.showArrow(false)
        spCountryCode.showNameCode(false)
    }

    /**
     * Click event on views
     */
    private fun clickListener() {
        btnSignIn.setOnClickListener(clickListener)
        tvTerms.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {

                R.id.btnSignIn -> {
                    if (validateDetails()) {
                        val intent = Intent(this, VerificationActivity::class.java)
                        intent.putExtra(Constants.App.CONTACT, "+" + spCountryCode.selectedCountryCode + etNumber.text)
                        this.startActivity(intent)

                    }
                }

                R.id.tvTerms -> {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(ApiClient.BASE_URL_LINKS + Constants.App.Api.TERMS)
                        )
                    )
                }
            }
    }

    /**
     * Validates fields
     */
    private fun validateDetails(): Boolean {
        if (etNumber.text.isEmpty()) {
            etNumber.error = getString(R.string.text_empty_number_msg)
            return false
        }
        if (etNumber.text.length < 7 || etNumber.text.length > 14) {
            etNumber.error = getString(R.string.text_valid_length)
            return false
        }
        return true
    }

    private fun clearAllFields() {
        etNumber.text.clear()
    }


}
