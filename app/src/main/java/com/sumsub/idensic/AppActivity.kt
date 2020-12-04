package com.sumsub.idensic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.security.ProviderInstaller
import timber.log.Timber

class AppActivity : AppCompatActivity(), ProviderInstaller.ProviderInstallListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        ProviderInstaller.installIfNeededAsync(this, this)
    }

    override fun onProviderInstalled() {
        Timber.d("onProviderInstalled")
    }

    override fun onProviderInstallFailed(errorCode: Int, recoveryIntent: Intent?) {
        Timber.d("onProviderInstallFailed: errorCode=$errorCode")
    }
}
