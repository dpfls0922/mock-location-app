package com.example.mocklocation.data.dto.response

import com.example.mocklocation.domain.entity.AutoComplete
import kotlinx.serialization.Serializable

@Serializable
data class AutoCompleteResponse (
    val response: Response
)

@Serializable
data class Response (
    val returnCode: Int,
    val totalTime: Double,
    val totalCount: Int,
    val suggestions: List<Suggestion>
)

@Serializable
data class Suggestion (
    val keyword: String,
    val coordinates: String?,
    val rpFlag: String?
)

fun AutoCompleteResponse.toDomain(): List<AutoComplete> {
    return this.response.suggestions.map {
        AutoComplete(keyword = it.keyword)
    }
}