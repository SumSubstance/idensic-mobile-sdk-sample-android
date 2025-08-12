package com.sumsub.idensic

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.gms.security.ProviderInstaller
import timber.log.Timber

class AppActivity : AppCompatActivity(), ProviderInstaller.ProviderInstallListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyEdgeToEdge()
        setContentView(R.layout.activity_app)

        ProviderInstaller.installIfNeededAsync(this, this)
    }

    override fun onProviderInstalled() {
        Timber.d("onProviderInstalled")
    }

    override fun onProviderInstallFailed(errorCode: Int, recoveryIntent: Intent?) {
        Timber.d("onProviderInstallFailed: errorCode=$errorCode")
    }

    private fun applyEdgeToEdge() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) return

        runCatching {
            findViewById<View>(android.R.id.content)
        }.onSuccess { view ->
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
                val insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or
                            WindowInsetsCompat.Type.displayCutout() or
                            WindowInsetsCompat.Type.ime()
                )

                view.updatePadding(
                    left = insets.left,
                    right = insets.right,
                    top = insets.top,
                    bottom = insets.bottom
                )

                WindowInsetsCompat.CONSUMED
            }
        }
    }
}
