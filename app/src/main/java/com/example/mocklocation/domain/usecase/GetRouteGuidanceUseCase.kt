package com.example.mocklocation.domain.usecase

import com.example.mocklocation.domain.entity.Poi
import com.example.mocklocation.domain.entity.RouteGuidance
import com.example.mocklocation.domain.repository.TmapRepository
import javax.inject.Inject

class GetRouteGuidanceUseCase @Inject constructor(
    private val tmapRepository: TmapRepository
) {
    suspend operator fun invoke(
        start: Poi,
        dest: Poi,
        wayPoints: List<Poi> = emptyList()
    ): Result<RouteGuidance> = runCatching {
        tmapRepository.searchRouteGuidance(start, dest, wayPoints)
    }
}