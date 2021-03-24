package com.sumsub.idensic.screen.splash

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.sumsub.idensic.R
import com.sumsub.idensic.common.Constants
import com.sumsub.idensic.screen.base.BaseFragment
import java.util.*
import java.util.concurrent.TimeUnit

class SplashFragment : BaseFragment(R.layout.fragment_splash) {

    private val selectEntryPoint = Runnable {
        val loggedIn = !prefManager.getToken().isNullOrBlank()
        val actionId = if (loggedIn) {
            R.id.action_splash_to_main
        } else {
            R.id.action_splash_to_sign_in
        }

        findNavController().navigate(actionId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var userId = prefManager.getUserId()
        if (userId.isNullOrEmpty()) {
            userId = String.format(Constants.USER_ID, UUID.randomUUID().toString())
            prefManager.setUserId(userId)
        }
    }

    override fun onPause() {
        super.onPause()
        view?.removeCallbacks(selectEntryPoint)
    }

    override fun onResume() {
        super.onResume()
        view?.postDelayed(selectEntryPoint, TimeUnit.SECONDS.toMillis(1))
    }
}