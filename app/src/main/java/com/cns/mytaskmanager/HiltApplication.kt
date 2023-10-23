package com.cns.mytaskmanager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application() {

    companion object {
        lateinit var instance: HiltApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}