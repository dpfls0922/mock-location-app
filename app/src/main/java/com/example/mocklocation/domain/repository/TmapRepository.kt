package com.example.mocklocation.domain.repository

import com.example.mocklocation.core.model.ApiResult
import com.example.mocklocation.domain.entity.AutoComplete
import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.domain.entity.Poi
import com.example.mocklocation.domain.entity.RouteGuidanceData
import com.example.mocklocation.domain.entity.SKCoord
import com.example.mocklocation.domain.entity.TouchLocation
import com.example.mocklocation.domain.factory.ParserType

interface TmapRepository {
    suspend fun getAutoCompleteSuggestions(keyword: String): List<AutoComplete>
    suspend fun searchPoi(searchTypCd: String, keyword: String, currentLocation: CurrentLocation): ApiResult<List<Poi>>
    suspend fun searchRouteGuidance(start: Poi, dest: Poi, wayPoints: List<Poi> = emptyList(), parserType: ParserType): ApiResult<RouteGuidanceData?>
    suspend fun searchRoadName(coord: SKCoord): ApiResult<TouchLocation>
}