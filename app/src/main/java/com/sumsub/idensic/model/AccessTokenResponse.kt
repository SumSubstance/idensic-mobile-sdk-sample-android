package com.sumsub.idensic.model

import com.google.gson.annotations.SerializedName

data class AccessTokenResponse(
    @SerializedName("token")
    val token: String = "",
    @SerializedName("userId")
    val userId: String = "",
    @SerializedName("applicantId")
    val applicantId: String = ""
)