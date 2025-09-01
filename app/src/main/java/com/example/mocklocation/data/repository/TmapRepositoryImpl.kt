package com.example.mocklocation.data.repository

import com.example.mocklocation.data.datasource.TmapDataSource
import com.example.mocklocation.data.dto.common.RequestHeaderFactory
import com.example.mocklocation.data.dto.request.PoiSearchRequest
import com.example.mocklocation.data.dto.request.RouteGuidanceRequest
import com.example.mocklocation.data.dto.response.toDomain
import com.example.mocklocation.domain.entity.AutoComplete
import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.domain.entity.Poi
import com.example.mocklocation.domain.entity.RouteGuidance
import com.example.mocklocation.domain.repository.TmapRepository
import com.skt.tmap.vsm.coordinates.VSMCoordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TmapRepositoryImpl @Inject constructor(
    private val dataSource: TmapDataSource
) : TmapRepository {
    override suspend fun getAutoCompleteSuggestions(keyword: String): List<AutoComplete> {
        val dto = dataSource.getAutoCompleteSuggestions(keyword)
        return dto.toDomain()
    }

    override suspend fun searchPoi(
        searchTypCd: String,
        keyword: String,
        currentLocation: CurrentLocation
    ): List<Poi> = withContext(Dispatchers.IO) {
        val (startX, startY) = VSMCoordinates.wgs84ToSK(
            currentLocation.longitude,
            currentLocation.latitude
        )
        val requestDto = PoiSearchRequest(
            header = RequestHeaderFactory.create(),
            searchTypCd = searchTypCd,
            name = keyword,
            noorX = startX.toInt().toString(),
            noorY = startY.toInt().toString()
        )
        val dto = dataSource.searchPoi(requestDto)
        dto.poiSearches.map { it.toDomain(startX.toInt(), startY.toInt()) }
    }

    override suspend fun searchRouteGuidance(
        start: Poi,
        dest: Poi
    ): RouteGuidance = withContext(Dispatchers.IO) {
        val requestDto = RouteGuidanceRequest(
            header = RequestHeaderFactory.create(),
            departXPos = start.xPos,
            departYPos = start.yPos,
            destXPos = dest.xPos,
            destYPos = dest.yPos
        )
        val dto = dataSource.searchRouteGuidance(requestDto)
        dto.toDomain()
    }
}