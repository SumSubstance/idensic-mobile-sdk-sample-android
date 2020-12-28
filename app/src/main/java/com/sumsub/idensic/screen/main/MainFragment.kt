package com.sumsub.idensic.screen.main

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sumsub.idensic.R
import com.sumsub.idensic.common.Constants
import com.sumsub.idensic.manager.ApiManager
import com.sumsub.idensic.model.FlowItem
import com.sumsub.idensic.screen.base.BaseFragment
import com.sumsub.sns.core.SNSActionResult
import com.sumsub.sns.core.SNSMobileSDK
import com.sumsub.sns.core.data.listener.TokenExpirationHandler
import com.sumsub.sns.core.data.model.FlowType
import com.sumsub.sns.core.data.model.SNSCompletionResult
import com.sumsub.sns.core.data.model.SNSException
import com.sumsub.sns.core.data.model.SNSSDKState
import com.sumsub.sns.liveness3d.SNSLiveness3d
import com.sumsub.sns.prooface.SNSProoface
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.*

class MainFragment : BaseFragment(R.layout.fragment_main) {

    private lateinit var toolbar: Toolbar
    private lateinit var gContent: Group
    private lateinit var pbProgress: ProgressBar
    private lateinit var tlUserId: TextInputLayout
    private lateinit var etUserId: TextInputEditText
    private lateinit var tlAccessToken: TextInputLayout
    private lateinit var etAccessToken: TextInputEditText
    private lateinit var tlFlowName: TextInputLayout
    private lateinit var etFlowName: TextInputEditText
    private lateinit var btnGenerateUserId: MaterialButton
    private lateinit var btnStartFlow: MaterialButton
    private lateinit var tlActionId: TextInputLayout
    private lateinit var etActionId: TextInputEditText
    private lateinit var tlAccessTokenAction: TextInputLayout
    private lateinit var etAccessTokenAction: TextInputEditText
    private lateinit var tlActionName: TextInputLayout
    private lateinit var etActionName: TextInputEditText
    private lateinit var btnGenerateActionId: MaterialButton
    private lateinit var btnStartAction: MaterialButton
    private lateinit var ibGetFlows: ImageButton
    private lateinit var ibGetActions: ImageButton

    private lateinit var apiManager: ApiManager

    private val sdkFlowAccessTokenExpirationHandler = object : TokenExpirationHandler {
        override fun onTokenExpired(): String = runBlocking {
            val token = prefManager.getToken()
            val userId = prefManager.getUserId() ?: generateUserId()

            val newAccessToken = try {
                val newResponse = apiManager.getAccessTokenForFlow(token, userId)
                newResponse.token
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(context, "An error while refreshing an access token. Please, check your internet connection", Toast.LENGTH_SHORT).show()
                ""
            }

            prefManager.setAccessToken(newAccessToken)
            return@runBlocking newAccessToken
        }
    }

    private val sdkActionAccessTokenExpirationHandler = object : TokenExpirationHandler {
        override fun onTokenExpired(): String = runBlocking {
            val token = prefManager.getToken()
            val userId = prefManager.getUserId() ?: generateUserId()
            val actionId = prefManager.getActionId() ?: generateActionId()

            val newAccessToken = try {
                val newResponse = apiManager.getAccessTokenForAction(token, userId, actionId)
                newResponse.token
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(context, "An error while refreshing an access token. Please, check your internet connection", Toast.LENGTH_SHORT).show()
                ""
            }

            prefManager.setAccessTokenAction(newAccessToken)
            return@runBlocking newAccessToken
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback {
            activity?.finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiUrl = prefManager.getUrl()
        if (apiUrl == null) {
            findNavController().navigate(R.id.action_main_to_sign_in)
            return
        }

        apiManager = ApiManager(apiUrl)

        toolbar = view.findViewById(R.id.toolbar)
        gContent = view.findViewById(R.id.g_content)
        pbProgress = view.findViewById(R.id.pb_progress)
        tlUserId = view.findViewById(R.id.tl_userid)
        etUserId = view.findViewById(R.id.et_userid)
        tlAccessToken = view.findViewById(R.id.tl_access_token)
        etAccessToken = view.findViewById(R.id.et_access_token)
        tlFlowName = view.findViewById(R.id.tl_flow_name)
        etFlowName = view.findViewById(R.id.et_flow_name)
        btnGenerateUserId = view.findViewById(R.id.btn_generate_userid)
        btnStartFlow = view.findViewById(R.id.btn_start_flow)
        tlActionId = view.findViewById(R.id.tl_actionid)
        etActionId = view.findViewById(R.id.et_actionid)
        tlAccessTokenAction = view.findViewById(R.id.tl_access_token_action)
        etAccessTokenAction = view.findViewById(R.id.et_access_token_action)
        tlActionName = view.findViewById(R.id.tl_action_name)
        etActionName = view.findViewById(R.id.et_action_name)
        btnGenerateActionId = view.findViewById(R.id.btn_generate_action_id)
        btnStartAction = view.findViewById(R.id.btn_start_action)
        ibGetFlows = view.findViewById(R.id.ib_get_flows)
        ibGetActions = view.findViewById(R.id.ib_get_actions)

        showProgress(false)
        setAccessToken(prefManager.getAccessToken())
        setAccessTokenAction(prefManager.getAccessTokenAction())
        etUserId.setText(prefManager.getUserId())
        etActionId.setText(prefManager.getUserId())

        toolbar.inflateMenu(R.menu.main)
        toolbar.setOnMenuItemClickListener {
            // Clear cache
            prefManager.setUrl(null)
            prefManager.setToken(null)
            prefManager.setAccessToken(null)

            findNavController().navigate(R.id.action_main_to_sign_in)
            return@setOnMenuItemClickListener true
        }
        btnGenerateUserId.setOnClickListener { generateUserId() }
        btnStartFlow.setOnClickListener { startSDKFlow() }
        btnGenerateActionId.setOnClickListener { generateActionId() }
        btnStartAction.setOnClickListener { startSDKAction() }
        ibGetFlows.setOnClickListener {
            showFlowListDialog(filter = { it.type != FlowType.Actions }) { etFlowName.setText(it) }
        }
        ibGetActions.setOnClickListener {
            showFlowListDialog(filter = { it.type == FlowType.Actions }) { etActionName.setText(it) }
        }
    }

    private fun generateUserId(): String {
        return String.format(Constants.USER_ID, UUID.randomUUID().toString()).also {
            prefManager.setUserId(it)
            etUserId.setText(it)
        }
    }

    private fun generateActionId(): String {
        return String.format(Constants.ACTION_ID, UUID.randomUUID().toString()).also {
            prefManager.setActionId(it)
            etActionId.setText(it)
        }
    }

    private fun startSDKFlow() {
        val token = prefManager.getToken() ?: run {
            Toast.makeText(context, "A token is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = prefManager.getUserId() ?: run {
            Toast.makeText(context, "An external user id is empty", Toast.LENGTH_SHORT).show()
            return
        }

        showProgress(true)


        lifecycleScope.launch {
            val accessToken = try {
                apiManager.getAccessTokenForFlow(token, userId).token
            } catch (e: Exception) {
                Toast.makeText(context, "An error while getting an access token. Please, check your applicant", Toast.LENGTH_SHORT).show()
                showProgress(false)
                return@launch
            }

            setAccessToken(accessToken)

            val apiUrl = prefManager.getUrl() ?: return@launch

            // force SDK to show open settings dialog request
            val modules = listOf(SNSProoface(feature = SNSProoface.FEATURE_FACE_SHOW_SETTINGS))

            val flowName = etFlowName.text.toString()

            val onSDKStateChangedHandler = getOnStateChangeListener()

            val onSDKCompletedHandler = getOnSDKCompletedHandler(requireContext().applicationContext)

            val onSDKErrorHandler = getOnSDKErrorHandler(requireContext().applicationContext)

            val snsSdk = SNSMobileSDK.Builder(requireActivity(), apiUrl, flowName)
                    .withAccessToken(accessToken, onTokenExpiration = sdkFlowAccessTokenExpirationHandler)
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

    private fun startSDKAction() {
        val token = prefManager.getToken() ?: run {
            Toast.makeText(context, "A token is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = prefManager.getUserId() ?: run {
            Toast.makeText(context, "An external user id is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val actionId = prefManager.getActionId() ?: run {
            Toast.makeText(context, "An external action id is empty", Toast.LENGTH_SHORT).show()
            return
        }

        showProgress(true)

        lifecycleScope.launch {
            val accessToken = try {
                apiManager.getAccessTokenForAction(token, userId, actionId).token
            } catch (e: Exception) {
                Toast.makeText(context, "An error while getting an access token. Please, check your applicant", Toast.LENGTH_SHORT).show()
                showProgress(false)
                return@launch
            }

            setAccessTokenAction(accessToken)

            val apiUrl = prefManager.getUrl() ?: return@launch

            val modules = listOf(SNSLiveness3d(), SNSProoface())
            val actionName = etActionName.text.toString()

            val onSDKStateChangedHandler = getOnStateChangeListener()

            val onSDKCompletedHandler = getOnSDKCompletedHandler(requireContext().applicationContext)

            val onSDKErrorHandler = getOnSDKErrorHandler(requireContext().applicationContext)

            val onActionResult = getOnActionResult()

            val snsSdk = SNSMobileSDK.Builder(requireActivity(), apiUrl, actionName)
                    .withAccessToken(accessToken, onTokenExpiration = sdkActionAccessTokenExpirationHandler)
                    .withDebug(true)
                    .withModules(modules)
                    .withHandlers(
                        onStateChanged = onSDKStateChangedHandler,
                        onCompleted = onSDKCompletedHandler,
                        onError = onSDKErrorHandler,
                        onActionResult = onActionResult
                    )
                    .build()

            snsSdk.launch()
            showProgress(false)
        }
    }

    private fun getOnStateChangeListener(): (SNSSDKState, SNSSDKState) -> Unit = { newState, prevState ->
        Timber.d("The SDK state was changed: $prevState -> $newState")

        when (newState) {
            is SNSSDKState.Ready -> Timber.d("SDK is ready")
            is SNSSDKState.Failed -> {
                when (newState) {
                    is SNSSDKState.Failed.Unauthorized -> Timber.e(newState.exception, "Invalid token or a token can't be refreshed by the SDK. Please, check your token expiration handler")
                    is SNSSDKState.Failed.Unknown -> Timber.e(newState.exception, "Unknown error")
                }
            }
            is SNSSDKState.Initial -> Timber.d("No verification steps are passed yet")
            is SNSSDKState.Incomplete -> Timber.d("Some but not all verification steps are passed over")
            is SNSSDKState.Pending -> Timber.d("Verification is in pending state")
            is SNSSDKState.FinallyRejected -> Timber.d("Applicant has been finally rejected")
            is SNSSDKState.TemporarilyDeclined -> Timber.d("Applicant has been declined temporarily")
            is SNSSDKState.Approved -> Timber.d("Applicant has been approved")
            is SNSSDKState.ActionCompleted -> Timber.d("Action is completed")
        }
    }

    private fun getOnSDKCompletedHandler(context: Context): (SNSCompletionResult, SNSSDKState) -> Unit = { result, state ->
        Timber.d("The SDK is finished. Result: $result, State: $state")
        Toast.makeText(context, "The SDK is finished. Result: $result, State: $state", Toast.LENGTH_SHORT).show()

        when (result) {
            is SNSCompletionResult.SuccessTermination -> Timber.d(result.toString())
            is SNSCompletionResult.AbnormalTermination -> Timber.d(result.exception)
        }
    }

    private fun getOnSDKErrorHandler(context: Context): (SNSException) -> Unit = { exception ->
        Timber.d("The SDK throws an exception. Exception: $exception")
        Toast.makeText(context, "The SDK throws an exception. Exception: $exception", Toast.LENGTH_SHORT).show()

        when (exception) {
            is SNSException.Api -> Timber.d("Api exception. ${exception.description}")
            is SNSException.Network -> Timber.d(exception, "Network exception.")
            is SNSException.Unknown -> Timber.d(exception, "Unknown exception.")
        }
    }

    private fun getOnActionResult(): (String, String?) -> SNSActionResult = { actionId, answer ->
        Timber.d("Action Result: actionId: $actionId answer: $answer")
        // use default scenario
        SNSActionResult.Continue
    }

    private fun showProgress(show: Boolean) {
        gContent.visibility = if (show) View.GONE else View.VISIBLE
        pbProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setAccessToken(accessToken: String?) {
        etAccessToken.setText(accessToken)
        prefManager.setAccessToken(accessToken)
    }

    private fun setAccessTokenAction(accessToken: String?) {
        etAccessTokenAction.setText(accessToken)
        prefManager.setAccessTokenAction(accessToken)
    }

    private fun getFlows(onFlowList: (items: List<FlowItem>) -> Unit) {
        val token = prefManager.getToken() ?: run {
            Toast.makeText(context, "A token is empty", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            showProgress(true)
            try {
                onFlowList(apiManager.getFlows(token).list.items.filter { it.target == "msdk" })
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(context, "An error while getting flow list. Please, check your internet connection", Toast.LENGTH_SHORT).show()
            }
            showProgress(false)
        }
    }

    private fun showFlowListDialog(filter: (FlowItem) -> Boolean, onSelected: (CharSequence) -> Unit) {
        getFlows { flows ->
            val items = flows.filter(filter).map { it.name }.toTypedArray()
            MaterialAlertDialogBuilder(requireContext())
                    .setItems(items) { dialog, which ->
                        dialog.dismiss()
                        onSelected(items[which])
                    }
                    .create()
                    .show()
        }
    }

    override fun getSoftInputMode(): Int = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
}