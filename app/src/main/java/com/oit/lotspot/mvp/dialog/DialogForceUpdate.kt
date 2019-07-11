package com.oit.lotspot.mvp.dialog

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.oit.lotspot.R
import com.oit.lotspot.mvp.activity.SplashActivity
import kotlinx.android.synthetic.main.dialog_force_update.*


class DialogForceUpdate(private val activity: Activity, private val message: String) : Dialog(activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (window != null)
//            window!!.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_force_update)
        initDialog()
        initData()
        clickOnOkayBtn()
    }


    private fun initDialog() {
        setCancelable(false)
//        if (activity != null && window != null)
//            window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(activity, android.R.color.transparent)))
//        else if (activity != null && window != null)
            window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(activity, android.R.color.transparent)))

        val params = window!!.attributes
        params.copyFrom(window!!.attributes)
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        window!!.attributes = params
    }

    private fun initData() {
        tv_alert_text!!.text = message
        tv_alert_okay_btn!!.text = activity!!.getString(R.string.update)
    }

    /**
     * click event on button update
     */
    internal fun clickOnOkayBtn() {
        tv_alert_okay_btn.setOnClickListener {

            val appPackageName = activity.packageName // getPackageName() from Context or Activity object
            try {
                activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (anfe: ActivityNotFoundException) {
                activity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }
    }
}

