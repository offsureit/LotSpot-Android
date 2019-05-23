package com.oit.lotspot.mvp.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.oit.lotspot.R
import com.oit.lotspot.interfaces.HistoryItemInterface
import com.oit.lotspot.mvp.adapter.HistoryAdapter
import com.oit.lotspot.mvp.presenter.HistoryPresenter
import com.oit.lotspot.retrofit.response.ErrorResponse
import com.oit.lotspot.retrofit.response.VehicleDeleteResponse
import com.oit.lotspot.retrofit.response.VehicleListResponseModel
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.layout_toolbar_history.*


class HistoryActivity : BaseActivity(), HistoryItemInterface, HistoryPresenter.ResponseCallBack {

    private lateinit var presenter: HistoryPresenter
    private var authToken = ""
    private var vehicleId = 0
    var adapter = HistoryAdapter(this@HistoryActivity, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        initUi()
        initPresenter()

        clickListener()
        initRecyclerView()
        setAdapter()
    }

    private fun initUi() {
        ivAppTitle.visibility = View.VISIBLE
        ivSearch.visibility = View.VISIBLE
        ivDelete.visibility = View.VISIBLE
    }

    /**
     * initialization of presenter
     */
    private fun initPresenter() {
        presenter = HistoryPresenter(this)
        showProgressView()
        hitApiToGetVehicleList()
    }

    /**
     * Click event on views
     */
    private fun clickListener() {
        ivSearch.setOnClickListener(clickListener)
        ivDelete.setOnClickListener(clickListener)
        ivBack.setOnClickListener(clickListener)
        tvCancel.setOnClickListener(clickListener)
        ivClear.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivSearch -> enableDisableViews()
                R.id.ivDelete -> showAlertForDelete(getString(R.string.text_delete_all), null)
                R.id.ivBack -> onBackPressed()
                R.id.tvCancel -> clickedForCancel()
                R.id.ivClear -> clickForClearSearchedText()
            }
    }

    /**
     * enable/disable views of toolbar
     */
    private fun enableDisableViews() {
        clSearch.visibility = View.VISIBLE
        tvCancel.visibility = View.VISIBLE
        ivSearch.visibility = View.GONE
        ivDelete.visibility = View.GONE
        ivAppTitle.visibility = View.GONE

        initSearchView(false)
    }

    /**
     * clicked for cancel all search & disable searchview
     */
    private fun clickedForCancel() {
        ivSearch.visibility = View.VISIBLE
        ivDelete.visibility = View.VISIBLE
        ivAppTitle.visibility = View.VISIBLE
        clSearch.visibility = View.GONE
        tvCancel.visibility = View.GONE

        initSearchView(true)
    }

    private fun initSearchView(clickedCancel: Boolean) {
        when (clickedCancel) {
            true -> {
                searchView.text.clear()
                searchView.clearFocus()
                hideSoftKeyboard(searchView)
                hitApiToGetVehicleList()
            }

            false -> {
                searchView.isFocusableInTouchMode = true
                searchView.isFocusable = true
                showKeyboard(searchView)
                setTextWatcher()
            }
        }
    }

    /**
     * clicked on cross in search view to clear search text
     */
    private fun clickForClearSearchedText() {
        searchView.text.clear()
        hideSoftKeyboard(searchView)
        adapter.filter.filter("")
    }

    private fun setTextWatcher() {
        searchView.afterTextChanged { query ->
            if (query.trim().isNotEmpty()) {
                adapter.filter.filter(query)
            } else {
                adapter.filter.filter("")
                searchView.clearFocus()
            }
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerViewHistory.layoutManager = linearLayoutManager
    }

    private fun setAdapter() {
        recyclerViewHistory.adapter = adapter
    }

    /**
     * call for api to get vehicle history list
     */
    private fun hitApiToGetVehicleList() {
        authToken = getAuthToken()
        presenter.apiGetVehicleList(authToken)
    }

    /**
     * call for api to delete single vehicle history
     */
    private fun hitApiToDeleteVehicleFromList(vehicleId: Int) {
        presenter.apiToDeleteVehicleFromList(authToken, vehicleId)
    }

    /**
     * call for api to delete all vehicle history
     */
    private fun hitApiToDeleteAllVehiclesList() {
        presenter.apiToDeleteAllVehicles(authToken)
    }

    /**
     * show alert to delete single vehicle or delete all history
     */
    private fun showAlertForDelete(message: String, vehicleId: Int?) {
        val alertDialog = android.app.AlertDialog.Builder(this, R.style.MyDialogTheme)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(getString(R.string.text_yes)) { dialog, _ ->

            showProgressView()
            if (vehicleId == null) {
                hitApiToDeleteAllVehiclesList()
            } else hitApiToDeleteVehicleFromList(vehicleId!!)
            dialog.cancel()

        }
        alertDialog.setNegativeButton(getString(R.string.text_no)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    /**
     * show alert if response is list is empty
     */
    private fun showAlertForEmptyList(message: String) {
        val alertDialog = android.app.AlertDialog.Builder(this, R.style.MyDialogTheme)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(getString(R.string.text_ok)) { dialog, _ ->
            dialog.cancel()
            finish()
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    /**
     * Swipe to delete callback from view holder
     */
    override fun onItemSelected(vehicleId: Int) {
        this.vehicleId = vehicleId
        showAlertForDelete(getString(R.string.text_delete_one), vehicleId)
    }

    /**
     * When successful response or data retrieved from get vehicle history list
     *
     * @param response is successful response from Api
     */
    override fun onSuccessVehicleList(responseModel: VehicleListResponseModel) {
        hideProgressDialog()
        if (responseModel.data.isNotEmpty()) {
            recyclerViewHistory.visibility = View.VISIBLE
            adapter.updateVehicleList(responseModel)
        } else {
            showAlertForEmptyList(getString(R.string.text_empty_histroy))
        }
    }

    /**
     * When error occurred in getting successful response of get vehicle history list
     *
     * @param errorResponse for Error message
     */
    override fun onFailureVehicleList(errorResponse: ErrorResponse) {
        hideProgressDialog()
        responseFailure(errorResponse)
    }

    /**
     * When successful response or data retrieved from api
     * delete all history,
     * delete single vehicle history from list
     *
     * @param response is successful response from Api
     */
    override fun onSuccessVehicleDelete(response: VehicleDeleteResponse) {
        hideProgressDialog()
        showToast(response.message)
        if (response.message.equals(getString(R.string.delete_response_msg))) {
            adapter.clearVehicleHistory()
        } else adapter.deleteItem(this.vehicleId)
    }

    /**
     * When error occurred in getting successful response of api
     * delete all history,
     * delete single vehicle history from list
     *
     * @param errorResponse for Error message
     */
    override fun onFailureVehicleDelete(errorResponse: ErrorResponse) {
        hideProgressDialog()
        responseFailure(errorResponse)
    }

    override fun onBackPressed() = finish()
}
