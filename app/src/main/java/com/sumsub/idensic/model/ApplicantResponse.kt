package com.sumsub.idensic.model

import com.google.gson.annotations.SerializedName

data class ApplicantResponse(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("key")
    val key: String? = null,
    @SerializedName("clientId")
    val clientId: String? = null,
    @SerializedName("inspectionId")
    val inspectionId: String? = null,
    @SerializedName("externalUserId")
    val externalUserId: String? = null,
    @SerializedName("info")
    val info: InfoAttribute? = null,
    @SerializedName("env")
    val env: String? = null,
    @SerializedName("requiredIdDocs")
    val requiredIdDocs: DocSets? = null,
    @SerializedName("review")
    val review: Review,
    @SerializedName("type")
    val type: String? = null
) {
    data class Review(
        @SerializedName("createDate")
        val createDate: String? = null,
        @SerializedName("reviewStatus")
        val reviewStatus: String? = null,
        @SerializedName("notificationFailureCnt")
        val notificationFailureCnt: Int? = null,
        @SerializedName("priority")
        val priority: Int? = null,
        @SerializedName("autoChecked")
        val autoChecked: Boolean? = null
    )
}