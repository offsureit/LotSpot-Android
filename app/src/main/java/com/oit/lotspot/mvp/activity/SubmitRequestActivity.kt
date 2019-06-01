package com.oit.lotspot.mvp.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.oit.lotspot.R
import com.oit.lotspot.constants.Constants
import com.oit.lotspot.database.SharedPreferencesManager
import com.oit.lotspot.mvp.presenter.SubmitRequestPresenter
import com.oit.lotspot.retrofit.request.AdminRequestModel
import com.oit.lotspot.retrofit.response.ErrorResponse
import com.oit.lotspot.retrofit.response.SaveVehicleDetailResponseModel
import kotlinx.android.synthetic.main.activity_submit_request.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class SubmitRequestActivity : BaseActivity(), SubmitRequestPresenter.ResponseCallBack {

    private lateinit var presenter: SubmitRequestPresenter
    private var adminRequestModel = AdminRequestModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_request)
        initUi()
        initPresenter()
        setTextWatcher()
        clickListener()
    }

    private fun initUi() {
        tvTitle.visibility = View.VISIBLE
        tvTitle.text = getString(R.string.title_submit_request)
    }

    private fun initPresenter() {
        presenter = SubmitRequestPresenter(this)
    }

    private fun setTextWatcher() {
        etSubject.afterTextChanged { subject ->
            adminRequestModel.subject = subject
        }

        etNote.afterTextChanged { note ->
            adminRequestModel.note = note
        }

        adminRequestModel.contact =
            SharedPreferencesManager.with(this).getString(Constants.SharedPref.PREF_USER_CONTACT, "")
    }

    /**
     * Click events in views
     */
    private fun clickListener() {
        ivMenu.setOnClickListener(clickListener)
        btnSubmit.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.ivMenu -> onBackPressed()
            R.id.btnSubmit -> apiHitForSubmitRequestToAdmin()
        }
    }

    /**
     * Call for api to submit request to admin
     */
    private fun apiHitForSubmitRequestToAdmin() {
        showProgressView()
        presenter.apiPostToSubmitRequest(adminRequestModel)
    }


    override fun onSuccess(responseModel: SaveVehicleDetailResponseModel) {
        hideProgressDialog()
        showToast(responseModel.message!!)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onFailure(errorResponse: ErrorResponse) {
        hideProgressDialog()
        showToast("Failure..")
    }

    override fun onBackPressed() = finish()
}
