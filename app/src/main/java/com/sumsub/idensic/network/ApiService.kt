package com.sumsub.idensic.network

import com.sumsub.idensic.model.*
import retrofit2.http.*

interface ApiService {

    @POST("resources/accessTokens")
    suspend fun getAccessToken(@Header("Authorization") authorization: String, @Query("levelName") levelName: String?, @Query("userId") userId: String, @Query("externalActionId") externalActionId: String? = null, @Query("ttlInSecs") ttlInSecs: Int = 999999): AccessTokenResponse

    @GET("resources/applicants/-/levels")
    suspend fun getLevels(@Header("Authorization") authorization: String): LevelListResponse

    @GET("resources/sdkIntegrations/flows/{flowid}")
    suspend fun getFlow(@Header("Authorization") authorization: String, @Path("flowid") flowid: String) : FlowItem?

}