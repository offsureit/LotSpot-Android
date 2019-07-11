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
import java.io.IOException


//import com.oit.lotspotter.activity.TruckerHomeActivity

class SplashActivity : AppCompatActivity() {
    var currentVersion = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
      //  getCurrentVersion()
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
        val isUserLogin = SharedPreferencesManager.with(this).getBoolean(Constants.SharedPref.PREF_IS_USER_LOGIN, false)
        if (isUserLogin) startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
        else startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
    }

    override fun onBackPressed() {

    }

//    private fun getCurrentVersion() {
//        val pm = packageManager
//        var pInfo: PackageInfo? = null
//
//        try {
//            pInfo = pm.getPackageInfo(this.packageName, 0)
//
//        } catch (e1: PackageManager.NameNotFoundException) {
//            e1.printStackTrace()
//        }
//
//        currentVersion = pInfo!!.versionName
//
//        GetLatestVersion().execute()
//
//    }
//
//    private inner class GetLatestVersion : AsyncTask<String, String, String>() {
//
//        protected fun onPreExecute() {
//            super.onPreExecute()
//        }
//
//        protected fun doInBackground(vararg params: String): String {
//
//            val newVersion: String? = null
//
//            try {
//
//                return Jsoup.connect(
//                    "https://play.google.com/store/apps/details?id="
//                            + this@SplashActivity.packageName + "&hl=en"
//                )
//                    .timeout(30000)
//                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
//                    .referrer("http://www.google.com")
//                    .get()
//                    .select(".hAyfc .htlgb")
//                    .get(7)
//                    .ownText()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//            //            return newVersion;
//            return currentVersion
//        }
//
//        protected fun onPostExecute(onlineVersion: String) {
//            super.onPostExecute(onlineVersion)
//
////            Log.d(FragmentActivity.TAG, "currentVersion =$currentVersion  ,, onlineVersion = $onlineVersion")
//
//            if (!currentVersion.equalsIgnoreCase(onlineVersion)) {
//
//                DialogForceUpdate(
//                    this@SplashActivity,
//                    "A new version of LotSpot is available on Play Store."
//                )
//                    .show()
//            } else {
//
//                //                new Handler().postDelayed(new Runnable() {
//                //
//                //                    @Override
//                //                    public void run() {
//                handler = null
//                gotoFirstScreen()
//                //                    }
//                //                }, 2 * 1000);
//            }
//        }
//    }

}
