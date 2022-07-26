package com.sumsub.idensic.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PrefManager(private val context: Context) {

    private val preferences: SharedPreferences by lazy { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    fun setUrl(url: String?) = preferences.edit(commit = true) { putString(KEY_URL, url) }
    fun getUrl() = preferences.getString(KEY_URL, null)

    fun setToken(token: String?) = preferences.edit(commit = true) { putString(KEY_TOKEN, token) }
    fun getToken() = preferences.getString(KEY_TOKEN, null)

    fun setAccessToken(token: String?) = preferences.edit(commit = true) { putString(KEY_ACCESS_TOKEN, token) }
    fun getAccessToken() = preferences.getString(KEY_ACCESS_TOKEN, null)

    fun setUserId(userId: String) = preferences.edit(commit = true) { putString(KEY_USER_ID, userId) }
    fun getUserId() = preferences.getString(KEY_USER_ID, null)

    fun setActionId(actionId: String) = preferences.edit(commit = true) { putString(KEY_ACTION_ID, actionId) }
    fun getActionId() = preferences.getString(KEY_ACTION_ID, null)

    fun setAccessTokenAction(token: String?) = preferences.edit(commit = true) { putString(KEY_ACCESS_TOKEN_ACTION, token) }
    fun getAccessTokenAction() = preferences.getString(KEY_ACCESS_TOKEN_ACTION, null)

    fun setSandbox(sandbox: Boolean) = preferences.edit(commit = true) { putBoolean(KEY_SANDBOX, sandbox) }

    fun isSandbox() = preferences.getBoolean(KEY_SANDBOX, false)

    fun setClientId(clientId: String?) = preferences.edit(commit = true) { putString(KEY_CLIENT_ID, clientId) }

    fun getClientId() = preferences.getString(KEY_CLIENT_ID, null)

    companion object {
        private const val KEY_URL = "url"
        private const val KEY_TOKEN = "token"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_ACCESS_TOKEN_ACTION = "access_token_action"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_ACTION_ID = "action_id"
        private const val KEY_SANDBOX = "sandbox"
        private const val KEY_CLIENT_ID = "clientid"
    }
}