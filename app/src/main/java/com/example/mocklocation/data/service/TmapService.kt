package com.example.mocklocation.data.service

import com.example.mocklocation.data.dto.request.PoiSearchRequest
import com.example.mocklocation.data.dto.request.RouteGuidanceRequest
import com.example.mocklocation.data.dto.request.TouchSearchRequest
import com.example.mocklocation.data.dto.response.AutoCompleteResponse
import com.example.mocklocation.data.dto.response.PoiSearchResponse
import com.example.mocklocation.data.dto.response.RouteGuidanceResponse
import com.example.mocklocation.data.dto.response.TouchSearchResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TmapService {
    @GET("TmapAutoCompleteServer/suggest")
    suspend fun getAutoCompleteSuggestions(
        @Query("q") query: String,
        @Query("client_code") clientCode: String,
        @Query("referrer_code") referrerCode: String
    ): AutoCompleteResponse

    @POST("poi/search/findpois")
    suspend fun searchPoi(
        @Body keyword: PoiSearchRequest
    ): PoiSearchResponse

    @POST("rsd/route/planningroutemultiformat")
    suspend fun searchRouteGuidance(
        @Body request: RouteGuidanceRequest
    ): RouteGuidanceResponse

    @POST("poi/search/findnearby")
    suspend fun searchRoadName(
        @Body request: TouchSearchRequest
    ): TouchSearchResponse
}