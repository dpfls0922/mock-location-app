package com.example.mocklocation.presentation.search

import com.example.mocklocation.domain.entity.AutoComplete
import com.example.mocklocation.domain.entity.Poi

data class AutoCompleteUiModel(
    val keyword: String
)

fun AutoComplete.toUiModel() = AutoCompleteUiModel(
    keyword = this.keyword
)

fun AutoCompleteUiModel.toPoi(): Poi {
    return Poi(
        pkey = keyword,
        name = keyword,
        roadAddress = "",
        xPos = 0,
        yPos = 0,
        rpFlag = 0,
        distance = null,
        feature = null
    )
}