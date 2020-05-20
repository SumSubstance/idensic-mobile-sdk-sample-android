package com.sumsub.idensic.screen.signin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sumsub.idensic.R
import com.sumsub.idensic.manager.ApiManager
import com.sumsub.idensic.manager.PrefManager
import com.sumsub.idensic.screen.base.BaseFragment
import kotlinx.coroutines.launch

class SignInFragment: BaseFragment(R.layout.fragment_sign_in) {

    private lateinit var gContent: Group
    private lateinit var pbProgress: ProgressBar
    private lateinit var tlUsername: TextInputLayout
    private lateinit var etUsername: TextInputEditText
    private lateinit var tlPassword: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnSignIn: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gContent = view.findViewById(R.id.g_content)
        pbProgress = view.findViewById(R.id.pb_progress)
        tlUsername = view.findViewById(R.id.tl_username)
        etUsername = view.findViewById(R.id.et_username)
        tlPassword = view.findViewById(R.id.tl_password)
        etPassword = view.findViewById(R.id.et_password)
        btnSignIn = view.findViewById(R.id.btn_sign_in)

        showProgress(false)
        btnSignIn.setOnClickListener { login() }
        etUsername.doAfterTextChanged { tlUsername.error = null }
        etPassword.doAfterTextChanged { tlPassword.error = null }
    }

    private fun login() {
        val username = etUsername.text?.toString()
        val password = etPassword.text?.toString()
        var error = false

        if (username.isNullOrEmpty()) {
            error = true
            tlUsername.error = "Username is empty"
        }

        if (password.isNullOrEmpty()) {
            error = true
            tlPassword.error = "Password is empty"
        }

        if (error) {
            return
        }

        showProgress(true)

        uiScope.launch {
            try {
                val response = ApiManager.login(username!!, password!!)
                PrefManager.setUsername(username)
                PrefManager.setPassword(password)
                PrefManager.setToken(response.payload)

                findNavController().navigate(R.id.action_sign_in_to_main)
            } catch (exception: Exception) {
                Toast.makeText(context, "Please, check your credentials or the internet connection and try again", Toast.LENGTH_SHORT).show()
            } finally {
                showProgress(false)
            }
        }
    }

    private fun showProgress(show: Boolean) {
        gContent.visibility = if (show) View.GONE else View.VISIBLE
        pbProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun getSoftInputMode(): Int = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
}