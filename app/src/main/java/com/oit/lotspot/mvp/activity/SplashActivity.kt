package com.oit.lotspot.mvp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.oit.lotspot.R
import com.oit.lotspot.activity.HomeActivity
import com.oit.lotspot.constants.Constants
import com.oit.lotspot.database.SharedPreferencesManager
import android.R.attr.versionName
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.oit.lotspot.mvp.dialog.DialogForceUpdate
import org.jsoup.Jsoup
import java.io.IOException


//import com.oit.lotspotter.activity.TruckerHomeActivity

class SplashActivity : AppCompatActivity() {
    var currentVersion = ""
    var TAG = SplashActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        getCurrentVersion()
    }

    private fun getCurrentVersion() {
        val pm = packageManager
        var pInfo: PackageInfo? = null

        try {
            pInfo = pm.getPackageInfo(this.packageName, 0)

        } catch (e1: PackageManager.NameNotFoundException) {
            e1.printStackTrace()
        }

        currentVersion = pInfo!!.versionName

        GetLatestVersion().execute()

    }

    private inner class GetLatestVersion : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String): String {

            val newVersion: String? = null

            try {

                return Jsoup.connect(
                    "https://play.google.com/store/apps/details?id="
                            + this@SplashActivity.packageName + "&hl=en"
                )
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select(".hAyfc .htlgb")
                    .get(7)
                    .ownText()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return currentVersion
        }

        override fun onPostExecute(onlineVersion: String) {
            super.onPostExecute(onlineVersion)

            Log.d(TAG, "currentVersion =$currentVersion  ,, onlineVersion = $onlineVersion")

            if (!currentVersion.equals(onlineVersion, ignoreCase = true)) {

                DialogForceUpdate(
                    this@SplashActivity,
                    "A new version of LotSpot is available on Play Store."
                ).show()
            } else {
                initializeApp()
            }
        }
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
        val isUserLogin = SharedPreferencesManager.with(this).getBoolean(Constants.SharedPref.PREF_IS_USER_LOGIN, false)
        if (isUserLogin) startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
        else startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
    }

    override fun onBackPressed() {

    }

}
