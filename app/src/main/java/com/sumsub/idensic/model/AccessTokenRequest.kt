package com.sumsub.idensic.model

import com.google.gson.annotations.SerializedName

data class AccessTokenRequest(
    @SerializedName("levelName")
    val levelName: String?,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("externalActionId")
    val externalActionId: String? = null,
    @SerializedName("ttlInSecs")
    val ttlInSecs: Int = 999999
)
