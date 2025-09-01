package com.example.mocklocation.domain.repository

import com.example.mocklocation.domain.entity.AutoComplete
import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.domain.entity.Poi
import com.example.mocklocation.domain.entity.RouteGuidance

interface TmapRepository {
    suspend fun getAutoCompleteSuggestions(keyword: String): List<AutoComplete>
    suspend fun searchPoi(searchTypCd: String, keyword: String, currentLocation: CurrentLocation): List<Poi>
    suspend fun searchRouteGuidance(start: Poi, dest: Poi, wayPoints: List<Poi> = emptyList()): RouteGuidance
}