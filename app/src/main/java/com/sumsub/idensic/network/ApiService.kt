package com.sumsub.idensic.network

import com.sumsub.idensic.model.*
import retrofit2.http.*

interface ApiService {

    @POST("/resources/dashboard/accessTokens/sdk")
    suspend fun getAccessToken(@Header("Authorization") authorization: String, @Body request: AccessTokenRequest): AccessTokenResponse

    @GET("resources/applicants/-/levels")
    suspend fun getLevels(@Header("Authorization") authorization: String): LevelListResponse

    @GET("/resources/sdkIntegrations/flows")
    suspend fun getFlows(@Header("Authorization") authorization: String): FlowListResponse

}