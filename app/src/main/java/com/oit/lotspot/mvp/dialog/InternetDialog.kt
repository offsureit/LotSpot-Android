package com.oit.lotspot.mvp.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.oit.lotspot.R
import com.oit.lotspot.mvp.activity.BaseActivity
import kotlinx.android.synthetic.main.dialog_no_internet.*

class InternetDialog(private var baseActivity: BaseActivity, var message: String) : Dialog(baseActivity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_no_internet)
        initDialog()
        updateDataInUi()
        clickListener()
    }

    /**
     * Configuring dialog's properties
     */
    private fun initDialog() {
        setCancelable(false)

        window?.apply {
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        }
    }

    /**
     * update alert message in Ui
     */
    private fun updateDataInUi() {
        tvMsg.text = message
    }

    /**
     * Click event on views
     */
    private fun clickListener() {
        btnOk.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btnOk -> {
                dismiss()
            }
        }
    }
}