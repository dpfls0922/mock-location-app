package com.example.mocklocation.domain.usecase

import com.example.mocklocation.core.model.ApiResult
import com.example.mocklocation.domain.entity.Poi
import com.example.mocklocation.domain.entity.RouteGuidanceData
import com.example.mocklocation.domain.factory.ParserType
import com.example.mocklocation.domain.repository.TmapRepository
import javax.inject.Inject

class GetRouteGuidanceUseCase @Inject constructor(
    private val tmapRepository: TmapRepository
) {
    suspend operator fun invoke(
        start: Poi,
        dest: Poi,
        wayPoints: List<Poi> = emptyList(),
        parserType: ParserType
    ): ApiResult<RouteGuidanceData?> {
        return tmapRepository.searchRouteGuidance(start, dest, wayPoints, parserType)
    }
}