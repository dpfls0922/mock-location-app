package com.example.mocklocation

import android.app.Application
import com.example.mocklocation.core.helper.VSMHelper
import com.skt.tmap.vsmsdk.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MockLocationApp: Application() {
    override fun onCreate() {
        super.onCreate()
        VSMHelper.createHelper(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}