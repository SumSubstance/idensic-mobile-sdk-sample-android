package com.sumsub.idensic.model


import com.google.gson.annotations.SerializedName

data class LevelListResponse(@SerializedName("list") val list: LevelList, @SerializedName("totalItems") val totalItems: Int)

data class LevelList(@SerializedName("items") val items: List<LevelItem>)

data class LevelItem(
    @SerializedName("id")
    val id: String?,
    @SerializedName("key")
    val key: String?,
    @SerializedName("clientId")
    val clientId: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("reviewReasonCode")
    val reviewReasonCode: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("createdBy")
    val createdBy: String?,
    @SerializedName("modifiedAt")
    val modifiedAt: String?,
    @SerializedName("msdkFlowId")
    val msdkFlowId: String?,
    @SerializedName("type")
    val type: String?
)

data class Level(val id: String, val name: String?, val isAction: Boolean)