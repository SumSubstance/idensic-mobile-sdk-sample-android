package com.sumsub.idensic.network

import com.sumsub.idensic.model.AccessTokenResponse
import com.sumsub.idensic.model.ApplicantRequest
import com.sumsub.idensic.model.ApplicantResponse
import com.sumsub.idensic.model.PayloadResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("resources/auth/login")
    suspend fun login(@Header("Authorization") authorization: String, @Query("ttlInSecs") ttlInSecs: Int = 999999): PayloadResponse

    @POST("resources/accessTokens")
    suspend fun getAccessToken(@Header("Authorization") authorization: String, @Query("applicantId") applicantId: String?, @Query("userId") userId: String?, @Query("ttlInSecs") ttlInSecs: Int = 999999): AccessTokenResponse

    @POST("resources/applicants")
    suspend fun getApplicantId(@Header("Authorization") authorization: String, @Body body: ApplicantRequest): ApplicantResponse
}