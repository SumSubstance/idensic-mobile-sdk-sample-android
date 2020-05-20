package com.sumsub.idensic.model

import com.google.gson.annotations.SerializedName

data class ResponseError(
    @SerializedName("description")
    val description: String,            // Human-readable error description.
    @SerializedName("code")
    val code: Int,                      // HTTP Status code.
    @SerializedName("correlationId")
    val correlationId: String,          // This id uniquely identifies the error. You can send this id to us, in case the cause of the problem is still unclear.
    @SerializedName("errorCode")
    val errorCode: Int? = null,         // Code of the exact problem (see the Error codes section). For some errors may not be present.
    @SerializedName("errorName")
    val errorName: String? = null       // String representation of the exact problem (see the Error codes section). It always appears when errorCode presents.
)