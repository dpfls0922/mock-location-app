package com.example.mocklocation.data.dto.common

import com.google.gson.annotations.SerializedName

enum class DepartSearchFlag(val id: Int) {
    GPS(0x00),
    RECENT_ROUTE(0x04),
    FAVORITE(0x05),
    LONGITUDE_SEARCH(0x06),
    NAME_SEARCH(0x07),
    ADDRESS_SEARCH(0x08)
}

enum class DepartRoadType {
    None,
    Highway,
    Carway,
    Overpass,
    Underpass,
    General,
    NearWay,
    NotSelected
}

enum class DestSearchFlag(val id: Int) {
    @SerializedName("LeaveReSearch") LEAVE_RE_SEARCH(1),
    @SerializedName("UserResearch") USER_RESEARCH(2),
    @SerializedName("NearFacility") NEAR_FACILITY(3),
    @SerializedName("RecentRouteGuide") RECENT_ROUTE_GUIDE(4),
    @SerializedName("Favorite") FAVORITE(5),
    @SerializedName("LongitudeSearch") LONGITUDE_SEARCH(6),
    @SerializedName("NameSearch") NAME_SEARCH(7),
    @SerializedName("AddressSearch") ADDRESS_SEARCH(8),
    @SerializedName("LeisureAndLife") LEISURE_AND_LIFE(9),
    @SerializedName("ReservedRouteGuide") RESERVED_ROUTE_GUIDE(10),
    @SerializedName("LocalBuisness") LOCAL_BUSINESS(11),
    @SerializedName("PhoneNumber") PHONE_NUMBER(12),
    @SerializedName("LocalName") LOCAL_NAME(13),
    @SerializedName("AfterMapMoving") AFTER_MAP_MOVING(15),
    @SerializedName("MyLocationView") MY_LOCATION_VIEW(16),
    @SerializedName("ReceiveLocationView") RECEIVE_LOCATION_VIEW(17),
    @SerializedName("SendingLocationView") SENDING_LOCATION_VIEW(18),
    @SerializedName("ForceResearch") FORCE_RESEARCH(23),
    @SerializedName("FamousRestaurant") FAMOUS_RESTAURANT(24),
    @SerializedName("RegularResearchOuterCase") REGULAR_RESEARCH_OUTER_CASE(25),
    @SerializedName("RegularResearchInnerCase") REGULAR_RESEARCH_INNER_CASE(26),
    @SerializedName("IntegrationSearch") INTEGRATION_SEARCH(27),
    @SerializedName("PrivateSecretary") PRIVATE_SECRETARY(28),
    @SerializedName("OptionChangeResearch") OPTION_CHANGE_RESEARCH(29),
    @SerializedName("NearLinkResearch") NEAR_LINK_RESEARCH(30),
    @SerializedName("RouteFavorite") ROUTE_FAVORITE(31),
    @SerializedName("GoHome") GO_HOME(32),
    @SerializedName("GoCompany") GO_COMPANY(33),
    @SerializedName("FrequentRoute") FREQUENT_ROUTE(34),
    @SerializedName("PublicTrafficeRouteSummary") PUBLIC_TRAFFICE_ROUTE_SUMMARY(112),
    @SerializedName("BranchAltResearch") BRANCH_ALT_RESEARCH(113),
    @SerializedName("AvoidAltResearch") AVOID_ALT_RESEARCH(114),
    @SerializedName("EndResearch") END_RESEARCH(116),
    @SerializedName("WaypointSearch") WAYPOINT_SEARCH(118),
    @SerializedName("SpeechRecogSearch") SPEECH_RECOG_SEARCH(119),
    @SerializedName("DestinationDirResearch") DESTINATION_DIR_RESEARCH(120),
    @SerializedName("Etc") ETC(255)
}

enum class DestSearchDetailFlag {
    @SerializedName("None") NONE,
    @SerializedName("ArrivalTimeRecalculate") ARRIVAL_TIME_RECALCULATE,
    @SerializedName("DestinationOppositeSearch") DESTINATION_OPPOSITE_SEARCH
}

enum class DetailLocFlag {
    NotConfirmed,
    Confirmed,
    NotApplied
}

enum class RoutePlanType(val id: Int, val code: String) {
    @SerializedName("Traffic_Recommend") TRAFFIC_RECOMMEND(id = 0, code = "Traffic_Recommend"),
    @SerializedName("Traffic_Free") TRAFFIC_FREE(id = 1, code = "Traffic_Free"),
    @SerializedName("Traffic_MinTime") TRAFFIC_MINTIME(id = 2, code = "Traffic_MinTime"),
    @SerializedName("Traffic_FirstTime") TRAFFIC_FIRSTTIME(id = 3, code = "Traffic_FirstTime"),
    @SerializedName("Traffic_Highway") TRAFFIC_HIGHWAY(id = 4, code = "Traffic_Highway"),
    @SerializedName("Shortest_ChargedAndFree") SHORTEST_CHARGED_AND_FREE(id = 10, code = "Shortest_ChargedAndFree"),
    @SerializedName("Traffic_GeneralRoad") TRAFFIC_GENERAL_ROAD(id = 12, code = "Traffic_GeneralRoad"),
    @SerializedName("Traffic_Truck") TRAFFIC_TRUCK(id = 17, code = "Traffic_Truck"),
    @SerializedName("Traffic_AvoidSchoolZone") TRAFFIC_AVOID_SCHOOL_ZONE(id = 19, code = "Traffic_AvoidSchoolZone")
}

enum class TollCarType {
    @SerializedName("None") NONE,
    @SerializedName("Car") CAR,
    @SerializedName("MediumVan") MEDIUM_VAN,
    @SerializedName("LargeVan") LARGE_VAN,
    @SerializedName("LargeTruck") LARGE_TRUCK,
    @SerializedName("SpecialTruck") SPECIAL_TRUCK,
    @SerializedName("SmallCar") SMALL_CAR,
    @SerializedName("TwoWheeledVehicle") TWO_WHEELED_VEHICLE
}

enum class CarOilType {
    @SerializedName("None") NONE,
    @SerializedName("Gasoline") GASOLINE,
    @SerializedName("Diesel") DIESEL,
    @SerializedName("LPG") LPG,
    @SerializedName("Electric") ELECTRIC,
    @SerializedName("ElectricAC3") ELECTRIC_AC3,
    @SerializedName("ElectricCombo") ELECTRIC_COMBO,
    @SerializedName("PremiumGasoline") PREMIUM_GASOLINE,
    @SerializedName("TeslaSupercharger") TESLA_SUPERCHARGER
}

enum class FareWeightOpts {
    BothChargedAndFree,
    OptimizedCharged,
    MinumCharged,
    FreeFirst,
    LogicApplied
}

enum class AddCameraType {
    @SerializedName("NoValue") NO_VALUE,
    @SerializedName("Bus") BUS,
    @SerializedName("SignalSpeeding") SIGNAL_SPEEDING,
    @SerializedName("Moving") MOVING,
    @SerializedName("ShoulderControl") SHOULDER_CONTROL,
    @SerializedName("ChangeableRoad") CHANGEABLE_ROAD,
    @SerializedName("OverLoaded") OVER_LOADED,
    @SerializedName("Security") SECURITY,
    @SerializedName("Traffic") TRAFFIC
}

enum class DangerAreaOption {
    @SerializedName("NoValue") NO_VALUE,
    @SerializedName("OccurFrequently") OCCUR_FREQUENTLY,
    @SerializedName("SharpCurveSection") SHARP_CURVE_SECTION,
    @SerializedName("SteepSlope") STEEP_SLOPE,
    @SerializedName("RoadKill") ROAD_KILL,
    @SerializedName("MistArea") MIST_AREA,
    @SerializedName("FallingRock") FALLING_ROCK,
    @SerializedName("SchoolZone") SCHOOL_ZONE,
    @SerializedName("TrainCrossing") TRAIN_CROSSING
}

enum class GuideImgResolutionCode {
    @SerializedName("NotSupported") NOT_SUPPORTED,
    @SerializedName("R170x207") R170X207,
    @SerializedName("R240x252") R240X252,
    @SerializedName("R320x316") R320X316,
    @SerializedName("R341x415") R341X415,
    @SerializedName("R480x504") R480X504,
    @SerializedName("R480x540") R480X540
}

enum class PatternWeek {
    Sunday,
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Holiday
}

enum class ServiceFlag {
    Realtime,
    DepartureTimeEstimation,
    ArrivalTimeEstimation
}