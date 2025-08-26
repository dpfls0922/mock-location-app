package com.example.mocklocation.data.datasource

import com.example.mocklocation.data.dto.request.RouteGuidanceRequest
import com.example.mocklocation.data.dto.response.AutoCompleteResponse
import com.example.mocklocation.data.dto.response.RouteGuidanceResponse

interface TmapDataSource {
    suspend fun getAutoCompleteSuggestions(keyword: String): AutoCompleteResponse
    suspend fun searchRouteGuidance(request: RouteGuidanceRequest): RouteGuidanceResponse
}