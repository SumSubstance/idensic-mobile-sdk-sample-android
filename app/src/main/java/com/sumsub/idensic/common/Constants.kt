package com.sumsub.idensic.common

import com.sumsub.idensic.model.DocSet
import com.sumsub.idensic.model.DocSetType
import com.sumsub.idensic.model.DocSubType
import com.sumsub.idensic.model.DocType

data class Endpoint(val name: String, val uri: String)

object Constants {

    const val USER_ID = "msdk2-demo-android-%s"

    val endpoints = listOf(
        Endpoint("TEST", "https://test-api.sumsub.com/"),
        Endpoint("DEV", "https://dev-api.sumsub.com/"),
        Endpoint("PROD", "https://api.sumsub.com/")
    )

    val identity = DocSet(
        idDocSetType = DocSetType.IDENTITY,
        types = listOf(DocType.ID_CARD, DocType.PASSPORT, DocType.DRIVERS),
        subTypes = listOf(DocSubType.FRONT_SIDE, DocSubType.BACK_SIDE)
    )

    val selfie = DocSet(
        idDocSetType = DocSetType.SELFIE,
        types = listOf(DocType.SELFIE)
    )

    val proofOfResidence = DocSet(
        idDocSetType = DocSetType.PROOF_OF_RESIDENCE,
        types = listOf(DocType.UTILITY_BILL)
    )
}