package com.sumsub.idensic.screen.main

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sumsub.idensic.BuildConfig
import com.sumsub.idensic.R
import com.sumsub.idensic.common.Constants
import com.sumsub.idensic.manager.ApiManager
import com.sumsub.idensic.manager.PrefManager
import com.sumsub.idensic.screen.base.BaseFragment
import com.sumsub.sns.core.SNSMobileSDK
import com.sumsub.sns.core.SNSModule
import com.sumsub.sns.core.data.listener.TokenExpirationHandler
import com.sumsub.sns.core.data.model.SNSCompletionResult
import com.sumsub.sns.core.data.model.SNSException
import com.sumsub.sns.core.data.model.SNSSDKState
import com.sumsub.sns.liveness3d.SNSLiveness3d
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.*

class MainFragment: BaseFragment(R.layout.fragment_main) {

    private lateinit var toolbar: Toolbar
    private lateinit var gContent: Group
    private lateinit var pbProgress: ProgressBar
    private lateinit var tlApplicant: TextInputLayout
    private lateinit var etApplicant: TextInputEditText
    private lateinit var tlAccessToken: TextInputLayout
    private lateinit var etAccessToken: TextInputEditText
    private lateinit var tlFlowName: TextInputLayout
    private lateinit var etFlowName: TextInputEditText
    private lateinit var btnApplicant: MaterialButton
    private lateinit var btnStart: MaterialButton

    private val sdkExpirationHandler = object : TokenExpirationHandler {
        override fun onTokenExpired(): String? = runBlocking {
            val token = PrefManager.getToken()
            val applicantId = PrefManager.getApplicantId()
            val userId = PrefManager.getUserId()

            val newAccessToken = try {
                val newResponse = ApiManager.getAccessToken(token, applicantId, userId)
                newResponse.token
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(context, "An error while refreshing an access token. Please, check your internet connection", Toast.LENGTH_SHORT).show()
                ""
            }

            PrefManager.setAccessToken(newAccessToken)
            return@runBlocking newAccessToken
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar)
        gContent = view.findViewById(R.id.g_content)
        pbProgress = view.findViewById(R.id.pb_progress)
        tlApplicant = view.findViewById(R.id.tl_applicant)
        etApplicant = view.findViewById(R.id.et_applicant)
        tlAccessToken = view.findViewById(R.id.tl_access_token)
        etAccessToken = view.findViewById(R.id.et_access_token)
        tlFlowName = view.findViewById(R.id.tl_flow_name)
        etFlowName = view.findViewById(R.id.et_flow_name)
        btnApplicant = view.findViewById(R.id.btn_generate_applicant)
        btnStart = view.findViewById(R.id.btn_start)

        showProgress(false)
        setApplicantId(PrefManager.getApplicantId())
        setAccessToken(PrefManager.getAccessToken())

        toolbar.inflateMenu(R.menu.main)
        toolbar.setOnMenuItemClickListener {
            // Clear cache
            PrefManager.setUsername(null)
            PrefManager.setPassword(null)
            PrefManager.setToken(null)
            PrefManager.setAccessToken(null)
            PrefManager.setApplicantId(null)

            findNavController().navigate(R.id.action_main_to_sign_in)
            return@setOnMenuItemClickListener true
        }
        btnApplicant.setOnClickListener { generateApplicant() }
        btnStart.setOnClickListener { startSDK() }
    }

    private fun generateApplicant() {
        showProgress(true)

        uiScope.launch {
            try {
                val token = PrefManager.getToken()
                val userId = String.format(Constants.USER_ID, UUID.randomUUID().toString())
                PrefManager.setUserId(userId)
                val applicantId = ApiManager.getApplicantId(token, listOf(Constants.identity, Constants.selfie, Constants.proofOfResidence), userId)
                setApplicantId(applicantId)
                Toast.makeText(context, "New Applicant has been created", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Timber.e(e, "An error while creating a new applicant")
                Toast.makeText(context, "An error while creating a new applicant, Please, check logs in LogCat.", Toast.LENGTH_SHORT).show()
            } finally {
                showProgress(false)
            }
        }
    }

    private fun startSDK() {
        val token = PrefManager.getToken()
        val applicantId = etApplicant.text?.toString()
        val userId = PrefManager.getUserId()

        if (token.isNullOrEmpty()) {
            Toast.makeText(context, "A token is empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (applicantId.isNullOrEmpty()) {
            Toast.makeText(context, "An applicant is empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId.isNullOrEmpty()) {
            Toast.makeText(context, "An user id is empty", Toast.LENGTH_SHORT).show()
            return
        }

        showProgress(true)

        uiScope.launch {
            val accessToken = try {
                ApiManager.getAccessToken(token, applicantId, userId).token
            } catch (e: Exception) {
                Toast.makeText(context, "An error while getting an access token. Please, check your applicant", Toast.LENGTH_SHORT).show()
                showProgress(false)
                return@launch
            }

            setAccessToken(accessToken)

            val apiUrl = BuildConfig.API_URL // test-api.sumsub.com
            val modules = listOf(SNSLiveness3d())
            val flowName = etFlowName.text.toString()

            val onSDKStateChangedHandler: (SNSSDKState, SNSSDKState) -> Unit = { newState, prevState ->
                Timber.d("The SDK state was changed: $prevState -> $newState")

                when (newState) {
                    is SNSSDKState.Ready -> Timber.d("SDK is ready")
                    is SNSSDKState.Failed -> {
                        when (newState) {
                            is SNSSDKState.Failed.Unauthorized -> Timber.e(newState.exception,"Invalid token or a token can't be refreshed by the SDK. Please, check your token expiration handler")
                            is SNSSDKState.Failed.Unknown -> Timber.e(newState.exception, "Unknown error")
                        }
                    }
                    is SNSSDKState.Initial -> Timber.d("No verification steps are passed yet")
                    is SNSSDKState.Incomplete -> Timber.d("Some but not all verification steps are passed over")
                    is SNSSDKState.Pending -> Timber.d("Verification is in pending state")
                    is SNSSDKState.FinallyRejected -> Timber.d("Applicant has been finally rejected")
                    is SNSSDKState.TemporarilyDeclined -> Timber.d("Applicant has been declined temporarily")
                    is SNSSDKState.Approved -> Timber.d("Applicant has been approved")
                }
            }

            val onSDKCompletedHandler: (SNSCompletionResult, SNSSDKState) -> Unit = { result, state ->
                Timber.d("The SDK is finished. Result: $result, State: $state")
                Toast.makeText(requireContext(), "The SDK is finished. Result: $result, State: $state", Toast.LENGTH_SHORT).show()

                when (result) {
                    is SNSCompletionResult.SuccessTermination -> Timber.d(result.toString())
                    is SNSCompletionResult.AbnormalTermination -> Timber.d(result.exception)
                }
            }

            val onSDKErrorHandler: (SNSException) -> Unit = { exception ->
                Timber.d("The SDK throws an exception. Exception: $exception")
                Toast.makeText(requireContext(), "The SDK throws an exception. Exception: $exception", Toast.LENGTH_SHORT).show()

                when (exception) {
                    is SNSException.Api -> Timber.d("Api exception. ${exception.description}")
                    is SNSException.Network -> Timber.d(exception, "Network exception.")
                    is SNSException.Unknown -> Timber.d(exception, "Unknown exception.")
                }
            }

            val snsSdk = SNSMobileSDK.Builder(requireActivity(), apiUrl, flowName)
                .withAccessToken(token, onTokenExpiration = sdkExpirationHandler)
                .withDebug(true)
                .withModules(modules)
                .withHandlers(
                    onStateChanged = onSDKStateChangedHandler,
                    onCompleted = onSDKCompletedHandler,
                    onError = onSDKErrorHandler)
                .build()

            snsSdk.launch()
            showProgress(false)
        }
    }

    private fun showProgress(show: Boolean) {
        gContent.visibility = if (show) View.GONE else View.VISIBLE
        pbProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setApplicantId(applicantId: String?) {
        etApplicant.setText(applicantId)
        PrefManager.setApplicantId(applicantId)
    }

    private fun setAccessToken(accessToken: String?) {
        etAccessToken.setText(accessToken)
        PrefManager.setAccessToken(accessToken)
    }

    override fun getSoftInputMode(): Int = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
}