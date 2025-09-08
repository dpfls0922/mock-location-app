package com.example.mocklocation.domain.usecase

import com.example.mocklocation.core.model.ApiResult
import com.example.mocklocation.core.model.mapSuccess
import com.example.mocklocation.data.factory.GpsParserFactory
import com.example.mocklocation.domain.entity.Poi
import com.example.mocklocation.domain.entity.RouteGuidanceData
import com.example.mocklocation.domain.factory.ParserType
import com.example.mocklocation.domain.repository.TmapRepository
import com.skt.tmap.vsm.data.VSMMapPoint
import javax.inject.Inject

class GetRouteGuidanceUseCase @Inject constructor(
    private val tmapRepository: TmapRepository,
    private val parserFactory: GpsParserFactory
) {
    suspend operator fun invoke(
        start: Poi,
        dest: Poi,
        wayPoints: List<Poi> = emptyList(),
        parserType: ParserType
    ): ApiResult<RouteGuidanceData?> {
        val parser = parserFactory.createParser<String>(parserType)
        return tmapRepository.searchRouteGuidance(start, dest, wayPoints)
            .mapSuccess { routeGuidance ->
                val tvasString = routeGuidance.tvases ?: ""
                val gpsPoints = parser.parse(tvasString)
                val vsmPoints = gpsPoints.map { gps ->
                    VSMMapPoint(gps.longitude, gps.latitude)
                }
                RouteGuidanceData(
                    tvasString = tvasString,
                    gpsPoints = gpsPoints,
                    vsmPoints = vsmPoints
                )
            }
    }
}