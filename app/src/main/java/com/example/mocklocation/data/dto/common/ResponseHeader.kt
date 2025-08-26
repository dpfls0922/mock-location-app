package com.example.mocklocation.data.dto.common

import kotlinx.serialization.Serializable

@Serializable
data class ResponseHeader(
    val errorCode: String,
    val errorMessage: String,
    val errorDetailCode: String?,
    val errorDetailMessage: String?,
    val sessionId: String
)