package com.example.mocklocation.domain.usecase

import com.example.mocklocation.core.model.ApiResult
import com.example.mocklocation.core.model.mapSuccess
import com.example.mocklocation.di.TvasNativeParser
import com.example.mocklocation.domain.entity.Poi
import com.example.mocklocation.domain.entity.RouteGuidanceData
import com.example.mocklocation.domain.parser.GpsDataParser
import com.example.mocklocation.domain.repository.TmapRepository
import com.skt.tmap.vsm.data.VSMMapPoint
import javax.inject.Inject

class GetRouteGuidanceUseCase @Inject constructor(
    private val tmapRepository: TmapRepository,
    @TvasNativeParser
    private val tvasParser: GpsDataParser<String>
) {
    suspend operator fun invoke(
        start: Poi,
        dest: Poi,
        wayPoints: List<Poi> = emptyList()
    ): ApiResult<RouteGuidanceData?> {
        return tmapRepository.searchRouteGuidance(start, dest, wayPoints)
            .mapSuccess { routeGuidance ->
                val tvasString = routeGuidance.tvases ?: ""
                val gpsPoints = tvasParser.parse(tvasString)
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