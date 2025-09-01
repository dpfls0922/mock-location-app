package com.example.mocklocation.data.datasource

import com.example.mocklocation.data.dto.request.PoiSearchRequest
import com.example.mocklocation.data.dto.request.RouteGuidanceRequest
import com.example.mocklocation.data.dto.request.TouchSearchRequest
import com.example.mocklocation.data.dto.response.AutoCompleteResponse
import com.example.mocklocation.data.dto.response.PoiSearchResponse
import com.example.mocklocation.data.dto.response.RouteGuidanceResponse
import com.example.mocklocation.data.dto.response.TouchSearchResponse

interface TmapDataSource {
    suspend fun getAutoCompleteSuggestions(keyword: String): AutoCompleteResponse
    suspend fun searchPoi(request: PoiSearchRequest): PoiSearchResponse
    suspend fun searchRouteGuidance(request: RouteGuidanceRequest): RouteGuidanceResponse
    suspend fun searchRoadName(request: TouchSearchRequest): TouchSearchResponse
}