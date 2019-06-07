package com.oit.lotspot.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.oit.lotspot.R
import com.oit.lotspot.adapter.ManifestAdapter
import com.oit.lotspot.mvp.activity.BaseActivity
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.trucker.activity_manifest.*

class ManifestActivity : BaseActivity() {

    private val linearLayoutManager = LinearLayoutManager(this@ManifestActivity)
    private var manifestAdapter = ManifestAdapter(this@ManifestActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manifest)

        initUi()
        clickListener()
        initRecyclerView()
    }

    private fun initUi() {
        tvTitle.visibility = View.VISIBLE
        tvTitle.text = getString(R.string.manifest_title)
    }

    /**
     * Click events on view
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

    private fun initRecyclerView() {
        with(recyclerViewManifest) {
            layoutManager = linearLayoutManager
            adapter = manifestAdapter
        }
    }

    override fun onBackPressed() = finish()
}
