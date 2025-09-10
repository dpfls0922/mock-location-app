package com.example.mocklocation.data.repository

import com.example.mocklocation.core.helper.safeApiCall
import com.example.mocklocation.core.model.ApiResult
import com.example.mocklocation.data.datasource.TmapDataSource
import com.example.mocklocation.data.dto.common.RequestHeaderFactory
import com.example.mocklocation.data.dto.common.WayPointRequest
import com.example.mocklocation.data.dto.request.PoiSearchRequest
import com.example.mocklocation.data.dto.request.RouteGuidanceRequest
import com.example.mocklocation.data.dto.request.TouchSearchRequest
import com.example.mocklocation.data.dto.response.toDomain
import com.example.mocklocation.data.factory.GpsParserFactory
import com.example.mocklocation.di.IoDispatcher
import com.example.mocklocation.domain.entity.AutoComplete
import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.domain.entity.Poi
import com.example.mocklocation.domain.entity.RouteGuidanceData
import com.example.mocklocation.domain.entity.SKCoord
import com.example.mocklocation.domain.entity.TouchLocation
import com.example.mocklocation.domain.factory.ParserType
import com.example.mocklocation.domain.repository.TmapRepository
import com.skt.tmap.vsm.coordinates.VSMCoordinates
import com.skt.tmap.vsm.data.VSMMapPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TmapRepositoryImpl @Inject constructor(
    private val dataSource: TmapDataSource,
    private val parserFactory: GpsParserFactory,
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
        val requestDto = buildPoiSearchRequest(searchTypCd, keyword, startX, startY)

        safeApiCall(
            apiCall = { dataSource.searchPoi(requestDto) },
            transformSuccess = { dto ->
                dto.poiSearches.map { it.toDomain() }
            }
        )
    }

    private fun buildPoiSearchRequest(
        searchTypCd: String,
        keyword: String,
        startX: Double,
        startY: Double
    ): PoiSearchRequest = PoiSearchRequest(
        header = RequestHeaderFactory.create(),
        searchTypCd = searchTypCd,
        name = keyword,
        noorX = startX.toInt().toString(),
        noorY = startY.toInt().toString()
    )

    override suspend fun searchRouteGuidance(
        start: Poi,
        dest: Poi,
        wayPoints: List<Poi>,
        parserType: ParserType
    ): ApiResult<RouteGuidanceData?> = withContext(ioDispatcher) {
        val requestDto = buildRouteGuidanceRequest(start, dest, wayPoints)

        safeApiCall(
            apiCall = { dataSource.searchRouteGuidance(requestDto) },
            transformSuccess = { dto ->
                parseRouteGuidance(dto.tvases, parserType)
            }
        )
    }

    private fun buildRouteGuidanceRequest(
        start: Poi,
        dest: Poi,
        wayPoints: List<Poi>
    ): RouteGuidanceRequest {
        val waypointRequests = wayPoints.map { poi ->
            WayPointRequest(x = poi.xPos, y = poi.yPos)
        }
        return RouteGuidanceRequest(
            header = RequestHeaderFactory.create(),
            departXPos = start.xPos,
            departYPos = start.yPos,
            destXPos = dest.xPos,
            destYPos = dest.yPos,
            wayPoints = waypointRequests.takeIf { it.isNotEmpty() }
        )
    }

    private suspend fun parseRouteGuidance(
        tvasString: String,
        parserType: ParserType
    ): RouteGuidanceData {
        val parser = parserFactory.createParser<String>(parserType)
        val startTime = System.currentTimeMillis()
        val gpsPoints = parser.parse(tvasString)
        val endTime = System.currentTimeMillis()
        val parsingTimeMs = endTime - startTime

        val vsmPoints = gpsPoints.map { gps -> VSMMapPoint(gps.longitude, gps.latitude) }

        return RouteGuidanceData(
            tvasString = tvasString,
            gpsPoints = gpsPoints,
            vsmPoints = vsmPoints,
            parsingTimeMs = parsingTimeMs
        )
    }

    override suspend fun searchRoadName(
        coord: SKCoord
    ): ApiResult<TouchLocation> = withContext(ioDispatcher) {
        val requestDto = TouchSearchRequest(
            header = RequestHeaderFactory.create(),
            noorX = coord.x.toInt().toString(),
            noorY = coord.y.toInt().toString()
        )

        safeApiCall(
            apiCall = { dataSource.searchRoadName(requestDto) },
            transformSuccess = { dto ->
                dto.toDomain()
            }
        )
    }
}