package com.example.mocklocation.domain.repository

import com.example.mocklocation.core.model.ApiResult
import com.example.mocklocation.domain.entity.AutoComplete
import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.domain.entity.Poi
import com.example.mocklocation.domain.entity.RouteGuidance
import com.example.mocklocation.domain.entity.SKCoord
import com.example.mocklocation.domain.entity.TouchLocation

interface TmapRepository {
    suspend fun getAutoCompleteSuggestions(keyword: String): List<AutoComplete>
    suspend fun searchPoi(searchTypCd: String, keyword: String, currentLocation: CurrentLocation): ApiResult<List<Poi>>
    suspend fun searchRouteGuidance(start: Poi, dest: Poi, wayPoints: List<Poi> = emptyList()): ApiResult<RouteGuidance>
    suspend fun searchRoadName(coord: SKCoord): ApiResult<TouchLocation>
}