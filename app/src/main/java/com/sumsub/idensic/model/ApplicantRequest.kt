package com.sumsub.idensic.model

import com.google.gson.annotations.SerializedName

data class ApplicantRequest(
    @SerializedName("requiredIdDocs")
    val requiredIdDocs: DocSets,
    @SerializedName("externalUserId")
    val externalUserId: String? = null,
    @SerializedName("sourceKey")
    val sourceKey: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("lang")
    val lang: String? = null,
    @SerializedName("metadata")
    val metadata: List<String>? = null,
    @SerializedName("info")
    val info: InfoAttribute? = null
)

data class InfoAttribute(
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("middleName")
    val middleName: String? = null,
    @SerializedName("legalName")
    val legalName: String? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("dob")
    val dob: String? = null,
    @SerializedName("placeOfBirth")
    val placeOfBirth: String? = null,
    @SerializedName("countryOfBirth")
    val countryOfBirth: String? = null,
    @SerializedName("stateOfBirth")
    val stateOfBirth: String? = null,
    @SerializedName("country")
    val country: String? = null,
    @SerializedName("nationality")
    val nationality: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("addresses")
    val addresses: List<InfoAddress>? = null
)

data class InfoAddress(
    @SerializedName("country")
    val country: String? = null,
    @SerializedName("postCode")
    val postCode: String? = null,
    @SerializedName("town")
    val town: String? = null,
    @SerializedName("street")
    val street: String? = null,
    @SerializedName("subStreet")
    val subStreet: String? = null,
    @SerializedName("state")
    val state: String? = null,
    @SerializedName("buildingName")
    val buildingName: String? = null,
    @SerializedName("flatNumber")
    val flatNumber: String? = null,
    @SerializedName("buildingNumber")
    val buildingNumber: String? = null,
    @SerializedName("startDate")
    val startDate: String? = null,
    @SerializedName("endDate")
    val endDate: String? = null
)

data class DocSets(@SerializedName("docSets") val docSets: List<DocSet>)

data class DocSet(
    @SerializedName("idDocSetType")
    val idDocSetType: DocSetType,
    @SerializedName("types")
    val types: List<DocType>,
    @SerializedName("subTypes")
    val subTypes: List<DocSubType>? = null
)

enum class DocSetType { IDENTITY, IDENTITY2, SELFIE, SELFIE2, PROOF_OF_RESIDENCE, PAYMENT_METHODS }

enum class DocType { ID_CARD, PASSPORT, DRIVERS, SELFIE,  UTILITY_BILL }

enum class DocSubType { FRONT_SIDE, BACK_SIDE }