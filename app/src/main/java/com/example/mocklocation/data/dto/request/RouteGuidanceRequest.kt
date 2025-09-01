package com.example.mocklocation.data.dto.request

import com.example.mocklocation.data.dto.common.AddCameraType
import com.example.mocklocation.data.dto.common.CarOilType
import com.example.mocklocation.data.dto.common.DangerAreaOption
import com.example.mocklocation.data.dto.common.DepartRoadType
import com.example.mocklocation.data.dto.common.DepartSearchFlag
import com.example.mocklocation.data.dto.common.DestSearchDetailFlag
import com.example.mocklocation.data.dto.common.DestSearchFlag
import com.example.mocklocation.data.dto.common.DetailLocFlag
import com.example.mocklocation.data.dto.common.FareWeightOpts
import com.example.mocklocation.data.dto.common.GuideImgResolutionCode
import com.example.mocklocation.data.dto.common.PatternWeek
import com.example.mocklocation.data.dto.common.RequestHeader
import com.example.mocklocation.data.dto.common.RoutePlanType
import com.example.mocklocation.data.dto.common.ServiceFlag
import com.example.mocklocation.data.dto.common.TollCarType
import com.example.mocklocation.data.dto.common.WayPointRequest
import kotlinx.serialization.Serializable

@Serializable
data class RouteGuidanceRequest(
    val header: RequestHeader,
    val tvas: String = "5.9",
    val resFlag: Byte? = null,
    val routePlanTypes: List<RoutePlanType>? = listOf(RoutePlanType.TRAFFIC_RECOMMEND),
    val usedFavoriteRouteFlags: List<Byte>? = null,
    val detailLocFlag: DetailLocFlag = DetailLocFlag.NotApplied,
    val serviceFlag: ServiceFlag? = ServiceFlag.Realtime,
    val patternTime: String? = null,
    val patternWeek: PatternWeek? = null,
    val guideImgResolutionCode: GuideImgResolutionCode? = GuideImgResolutionCode.R480X540,
    val dangerAreaOptions: List<DangerAreaOption>? = null,
    val routePlanAroundRange: Byte? = 1,

    // 출발지 정보
    val departRoadType: DepartRoadType = DepartRoadType.None,
    val departName: String? = null,
    val departXPos: Int,
    val departYPos: Int,
    val angle: Short? = -1,
    val speed: Short? = 0,
    val departDirPriority: Byte? = 0,
    val departSrchFlag: Byte? = DepartSearchFlag.GPS.id.toByte(),

    // 목적지 정보
    val destName: String? = null,
    val destXPos: Int,
    val destYPos: Int,
    val destRpFlag: Byte? = 0x63,
    val destSearchFlag: DestSearchFlag = DestSearchFlag.ADDRESS_SEARCH,
    val destSearchDetailFlag: DestSearchDetailFlag? = null,
    val destPoiId: String? = null,
    val navSeq: String? = null,
    val destPkey: String? = null,

    // GPS 정보
    val gpsTraceData: String? = null,

    // 부가 정보
    val tollCarType: TollCarType? = null,
    val carOilType: CarOilType? = null,
    val addCameraTypes: List<AddCameraType>? = null,
    val fareWeightOpts: List<FareWeightOpts>? = null,
    val controlRouteReqFlag: Byte? = null,
    val hipassFlag: Byte? = 0,

    // 경유지 정보
    val wayPoints: List<WayPointRequest>? = null
)