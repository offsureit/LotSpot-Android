package com.oit.lotspot.application

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.oit.lotspot.BuildConfig
import io.fabric.sdk.android.Fabric


class AppController : Application(){

    override fun onCreate() {
        super.onCreate()
        val fabric = Fabric.Builder(this)
            .kits(Crashlytics())
            .debuggable(com.crashlytics.android.BuildConfig.DEBUG) // Enables Crashlytics debugger
            .build()
        Fabric.with(fabric)
    }
}