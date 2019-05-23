package com.oit.lotspot.mvp.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.oit.lotspot.R
import com.oit.lotspot.constants.Constants
import com.oit.lotspot.database.SharedPreferencesManager

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initializeApp()

    }

    private fun initializeApp() {
        Handler().postDelayed({
            checkForLogin()
            finish()
        }, 3000)
    }

    /**
     * check for user logged in or not
     */
    private fun checkForLogin() {
        val isUserLogin =
            SharedPreferencesManager.with(this).getBoolean(Constants.SharedPref.PREF_IS_USER_LOGIN, false)
        if (isUserLogin)
            startActivity(Intent(this, HomeActivity::class.java))
        else startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
    }
}
