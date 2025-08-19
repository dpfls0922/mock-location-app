package com.example.mocklocation

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MockLocationApp: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}