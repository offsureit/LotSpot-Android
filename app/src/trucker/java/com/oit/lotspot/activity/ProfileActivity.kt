package com.oit.lotspot.activity

import android.os.Bundle
import android.view.View
import com.oit.lotspot.R
import com.oit.lotspot.mvp.activity.BaseActivity
import kotlinx.android.synthetic.main.layout_toolbar.*

class ProfileActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initUi()
        clickListener()
    }

    private fun initUi() {
        tvTitle.visibility = View.VISIBLE
        tvTitle.text = getString(R.string.profile)
    }

    /**
     * Click event on views
     */
    private fun clickListener() {
        ivMenu.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivMenu -> onBackPressed()
            }
    }

    override fun onBackPressed() = finish()
}
