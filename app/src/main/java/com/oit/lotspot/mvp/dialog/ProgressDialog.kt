package com.oit.lotspot.mvp.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import com.oit.lotspot.R


class ProgressDialog(private val mContext: Context) :
    Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_loading)

        initDialog()

    }

    private fun initDialog() {

        setCancelable(false)
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setDimAmount(0.0f)
    }

}