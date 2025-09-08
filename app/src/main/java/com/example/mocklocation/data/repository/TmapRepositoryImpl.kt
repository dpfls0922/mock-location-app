package com.example.mocklocation.data.repository

import com.example.mocklocation.core.model.ApiResult
import com.example.mocklocation.data.datasource.TmapDataSource
import com.example.mocklocation.data.dto.common.RequestHeaderFactory
import com.example.mocklocation.data.dto.common.WayPointRequest
import com.example.mocklocation.data.dto.common.errorMessageOrDefault
import com.example.mocklocation.data.dto.common.isSuccess
import com.example.mocklocation.data.dto.request.PoiSearchRequest
import com.example.mocklocation.data.dto.request.RouteGuidanceRequest
import com.example.mocklocation.data.dto.request.TouchSearchRequest
import com.example.mocklocation.data.dto.response.toDomain
import com.example.mocklocation.di.IoDispatcher
import com.example.mocklocation.domain.entity.AutoComplete
import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.domain.entity.Poi
import com.example.mocklocation.domain.entity.RouteGuidance
import com.example.mocklocation.domain.entity.SKCoord
import com.example.mocklocation.domain.entity.TouchLocation
import com.example.mocklocation.domain.repository.TmapRepository
import com.skt.tmap.vsm.coordinates.VSMCoordinates
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TmapRepositoryImpl @Inject constructor(
    private val dataSource: TmapDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TmapRepository {
    override suspend fun getAutoCompleteSuggestions(
        keyword: String
    ): List<AutoComplete> = withContext(ioDispatcher) {
        dataSource.getAutoCompleteSuggestions(keyword).toDomain()
    }

    override suspend fun searchPoi(
        searchTypCd: String,
        keyword: String,
        currentLocation: CurrentLocation
    ): ApiResult<List<Poi>> = withContext(ioDispatcher) {
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
        if (!dto.isSuccess()) {
            return@withContext ApiResult.Error(dto.errorMessageOrDefault())
        }
        ApiResult.Success(dto.poiSearches.map { it.toDomain(startX.toInt(), startY.toInt()) })
    }

    override suspend fun searchRouteGuidance(
        start: Poi,
        dest: Poi,
        wayPoints: List<Poi>
    ): ApiResult<RouteGuidance> = withContext(ioDispatcher) {
        val waypointRequests = wayPoints.map { poi ->
            WayPointRequest(
                x = poi.xPos,
                y = poi.yPos
            )
        }

        val requestDto = RouteGuidanceRequest(
            header = RequestHeaderFactory.create(),
            departXPos = start.xPos,
            departYPos = start.yPos,
            destXPos = dest.xPos,
            destYPos = dest.yPos,
            wayPoints = waypointRequests.takeIf { it.isNotEmpty() }
        )
        val dto = dataSource.searchRouteGuidance(requestDto)
        if (!dto.isSuccess()) {
            return@withContext ApiResult.Error(dto.errorMessageOrDefault())
        }
        ApiResult.Success(dto.toDomain())
    }

    override suspend fun searchRoadName(
        coord: SKCoord
    ): ApiResult<TouchLocation> = withContext(ioDispatcher) {
        val requestDto = TouchSearchRequest(
            header = RequestHeaderFactory.create(),
            noorX = coord.x.toInt().toString(),
            noorY = coord.y.toInt().toString()
        )
        val dto = dataSource.searchRoadName(requestDto)
        if (!dto.isSuccess()) {
            return@withContext ApiResult.Error(dto.errorMessageOrDefault())
        }
        ApiResult.Success(dto.toDomain())
    }
}