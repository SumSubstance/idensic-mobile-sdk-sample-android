package com.sumsub.idensic

import android.content.Context
import androidx.multidex.MultiDexApplication

class App: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {

        lateinit var context: Context
    }
}