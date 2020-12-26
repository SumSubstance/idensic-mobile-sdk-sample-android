package com.sumsub.idensic

import androidx.multidex.MultiDexApplication
import com.sumsub.idensic.manager.PrefManager

class App: MultiDexApplication() {

    lateinit var prefManager: PrefManager

    override fun onCreate() {
        super.onCreate()
        prefManager = PrefManager(this)
    }

}