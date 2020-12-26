package com.sumsub.idensic.network

import com.sumsub.idensic.model.AccessTokenResponse
import com.sumsub.idensic.model.FlowListResponse
import com.sumsub.idensic.model.PayloadResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("resources/auth/login")
    suspend fun login(@Header("Authorization") authorization: String, @Query("ttlInSecs") ttlInSecs: Int = 999999): PayloadResponse

    @POST("resources/accessTokens")
    suspend fun getAccessToken(@Header("Authorization") authorization: String, @Query("userId") userId: String, @Query("externalActionId") externalActionId: String? = null, @Query("ttlInSecs") ttlInSecs: Int = 999999): AccessTokenResponse

    @GET("/resources/sdkIntegrations/flows")
    suspend fun getFlows(@Header("Authorization") authorization: String): FlowListResponse

}