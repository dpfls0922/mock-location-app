package com.example.mocklocation.data.dto.response

import com.example.mocklocation.data.dto.common.ResponseHeader
import com.example.mocklocation.domain.entity.Destination
import com.example.mocklocation.domain.entity.RouteGuidance
import kotlinx.serialization.Serializable

@Serializable
data class RouteGuidanceResponse(
    val header: ResponseHeader,
    val roadCount: Int? = null,
    val roadType: Byte? = null,
    val routeCount: Byte,
    val routePlanTypes: List<Int>? = null,
    val fareWeightOpt: List<Int>? = null,
    val routeSummaryCode: List<String>? = null,
    val destination: DestinationDto,
    val tvasSize: List<Int>? = null,
    val tvases: String,
    val usedFavoriteRouteList: List<UsedFavoriteRouteDto>? = null
)

@Serializable
data class DestinationDto(
    val compressFlag: Byte,
    val destPoiId: String,
    val destRpFlag: Byte,
    val departCoordType: Byte,
    val destXPos: Int,
    val destYPos: Int,
    val destNameSize: Byte,
    val tvasCount: Byte,
    val destName: String,
    val skyCode: String? = null,
    val skyName: String? = null,
    val rainTypeCode: String? = null,
    val rainSinceOnTime: String? = null,
    val tempC1h: String? = null
)

@Serializable
data class UsedFavoriteRouteDto(
    val usedFavoriteRouteFlag: Byte,
    val diffSuccessFlag: Byte,
    val diffTime: Int,
    val diffDistance: Int,
    val diffFare: Int,
    val usedFavoriteRouteSaveFlag: Byte,
    val analysisPeriod: Int,
    val drivingCount: Int,
    val tripInitSessionId: String? = null
)

fun RouteGuidanceResponse.toDomain(): RouteGuidance {
    val destinationDomain = this.destination.toDomain()
    return RouteGuidance(
        routeCount = this.routeCount.toInt(),
        destination = destinationDomain,
        tvases = tvases
    )
}

fun DestinationDto.toDomain(): Destination {
    return Destination(
        destXPos = this.destXPos,
        destYPos = this.destYPos,
    )
}