package com.sumsub.idensic.model

import com.google.gson.annotations.SerializedName
import com.sumsub.sns.core.data.model.FlowType

data class FlowListResponse(@SerializedName("list") val list: FlowList, @SerializedName("totalItems") val totalItems: Int)

data class FlowList(@SerializedName("items") val items: List<FlowItem>)

data class FlowItem(
    @SerializedName("id")
    val id: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("clientId")
    val clientId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("levelName")
    val levelName: String,
    @SerializedName("target")
    val target: String,
    @SerializedName("type")
    val type: FlowType?
)