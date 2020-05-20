package com.sumsub.idensic.model

import com.google.gson.annotations.SerializedName

data class PayloadResponse(
    @SerializedName("status") val status: String,
    @SerializedName("payload") val payload: String
)