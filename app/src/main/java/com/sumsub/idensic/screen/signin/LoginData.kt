package com.sumsub.idensic.screen.signin

import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("url")
    val url: String?,
    @SerializedName("t")
    val t: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("sandbox")
    val legacySandBox: Boolean?,
    @SerializedName("sbx")
    val sbx: Int?,
    @SerializedName("c")
    val clientId: String?,
) {
    val isSandBox: Boolean get() = sbx == 1 || legacySandBox == true
}