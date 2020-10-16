package com.sumsub.idensic.manager

import com.google.gson.Gson
import com.sumsub.idensic.BuildConfig
import com.sumsub.idensic.model.*
import com.sumsub.idensic.network.ApiService
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiManager {

    private val service: ApiService by lazy { retrofit.create(
        ApiService::class.java) }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val gson: Gson by lazy { Gson() }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG)
                        HttpLoggingInterceptor.Level.BODY
                    else
                        HttpLoggingInterceptor.Level.NONE
            })
            .build()
    }

    suspend fun login(username: String, password: String): PayloadResponse = service.login(Credentials.basic(username, password))

    suspend fun getAccessTokenForFlow(token: String?, userId: String): AccessTokenResponse = service.getAccessToken("Bearer $token", userId, null)

    suspend fun getAccessTokenForAction(token: String?, userId: String, actionId: String?): AccessTokenResponse = service.getAccessToken("Bearer $token", userId, actionId)

    suspend fun getApplicantId(token: String?, docs: List<DocSet>, userId: String?): String {
        val request = ApplicantRequest(
            info = InfoAttribute(),
            requiredIdDocs = DocSets(docs),
            externalUserId = userId
        )

        return service.getApplicantId("Bearer $token", request).id
    }
}