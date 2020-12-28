package com.sumsub.idensic.screen.signin

import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("url")
    val url: String?,
    @SerializedName("t")
    val t: String?
)