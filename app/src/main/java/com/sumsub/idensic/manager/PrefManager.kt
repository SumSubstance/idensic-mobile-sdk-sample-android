package com.sumsub.idensic.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.sumsub.idensic.App

object PrefManager {

    private const val KEY_USER_USERNAME = "user_username"
    private const val KEY_USER_PASSWORD = "user_password"
    private const val KEY_TOKEN = "token"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_APPLICANT_ID = "applicant_id"
    private const val KEY_USER_ID = "user_id"

    private val preferences: SharedPreferences by lazy { App.context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    fun setUsername(username: String?) = preferences.edit(commit = true) { putString(KEY_USER_USERNAME, username) }
    fun getUsername(): String? = preferences.getString(KEY_USER_USERNAME, null)

    fun setPassword(password: String?) = preferences.edit(commit = true) { putString(KEY_USER_PASSWORD, password) }
    fun getPassword() = preferences.getString(KEY_USER_PASSWORD, null)

    fun setToken(token: String?) = preferences.edit(commit = true) { putString(KEY_TOKEN, token) }
    fun getToken() = preferences.getString(KEY_TOKEN, null)

    fun setAccessToken(token: String?) = preferences.edit(commit = true) { putString(KEY_ACCESS_TOKEN, token) }
    fun getAccessToken() = preferences.getString(KEY_ACCESS_TOKEN, null)

    fun setApplicantId(applicantId: String?) = preferences.edit(commit = true) { putString(KEY_APPLICANT_ID, applicantId) }
    fun getApplicantId() = preferences.getString(KEY_APPLICANT_ID, null)

    fun setUserId(userId: String) = preferences.edit(commit = true) { putString(KEY_USER_ID, userId) }
    fun getUserId() = preferences.getString(KEY_USER_ID, null)
}