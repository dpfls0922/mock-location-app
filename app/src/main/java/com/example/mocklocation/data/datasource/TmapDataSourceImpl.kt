package com.example.mocklocation.data.datasource

import com.example.mocklocation.data.dto.request.PoiSearchRequest
import com.example.mocklocation.data.dto.request.RouteGuidanceRequest
import com.example.mocklocation.data.dto.request.TouchSearchRequest
import com.example.mocklocation.data.dto.response.AutoCompleteResponse
import com.example.mocklocation.data.dto.response.PoiSearchResponse
import com.example.mocklocation.data.dto.response.RouteGuidanceResponse
import com.example.mocklocation.data.dto.response.TouchSearchResponse
import com.example.mocklocation.data.service.TmapService
import javax.inject.Inject
import javax.inject.Named

class TmapDataSourceImpl @Inject constructor(
    @Named("tmapService") private val tmapService: TmapService,
    @Named("autocompleteTmapService") private val autoService: TmapService
) : TmapDataSource {
    override suspend fun getAutoCompleteSuggestions(keyword: String): AutoCompleteResponse =
        autoService.getAutoCompleteSuggestions(keyword, "TMC_V1", "autoComplete")

    override suspend fun searchPoi(request: PoiSearchRequest): PoiSearchResponse =
        tmapService.searchPoi(request)

    override suspend fun searchRouteGuidance(request: RouteGuidanceRequest): RouteGuidanceResponse =
        tmapService.searchRouteGuidance(request)

    override suspend fun searchRoadName(request: TouchSearchRequest): TouchSearchResponse =
        tmapService.searchRoadName(request)
}