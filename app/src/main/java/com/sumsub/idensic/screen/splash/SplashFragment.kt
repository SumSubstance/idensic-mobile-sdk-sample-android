package com.sumsub.idensic.screen.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sumsub.idensic.R
import com.sumsub.idensic.common.Constants
import com.sumsub.idensic.manager.PrefManager
import com.sumsub.idensic.screen.base.BaseFragment
import java.util.*

class SplashFragment: BaseFragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var userId = PrefManager.getUserId()
        if (userId.isNullOrEmpty()) {
            userId = String.format(Constants.USER_ID, UUID.randomUUID().toString())
            PrefManager.setUserId(userId)
        }

        handler.postDelayed({
            val loggedIn = !PrefManager.getUsername().isNullOrBlank()
            val actionId = if (loggedIn) {
                R.id.action_splash_to_main
            } else {
                R.id.action_splash_to_sign_in
            }

            findNavController().navigate(actionId)
        }, 3000)
    }
}