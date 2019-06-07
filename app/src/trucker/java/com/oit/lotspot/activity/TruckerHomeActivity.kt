package com.oit.lotspot.activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.oit.lotspot.R
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.trucker.activity_trucker_navigation.*
import kotlinx.android.synthetic.trucker.layout_home_trucker_bottom.*

class TruckerHomeActivity : NavigationDrawerTruckerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_home_trucker, flContainer)
        initUi()
        clickListener()
    }

    private fun initUi() {
        ivAppTitle.visibility = View.VISIBLE
        ivMenu.setImageResource(R.drawable.ic_menu)
    }

    /**
     * Click events on views
     */
    private fun clickListener() {
        ivMenu.setOnClickListener(clickListener)
        btnFindCar.setOnClickListener(clickListener)
        btnManifest.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivMenu -> drawerLayoutRoot.openDrawer(Gravity.START)
                R.id.btnFindCar -> startActivity(Intent(this@TruckerHomeActivity, TruckerMapActivity::class.java))
                R.id.btnManifest -> startActivity(Intent(this@TruckerHomeActivity, ManifestActivity::class.java))
            }
    }
}
