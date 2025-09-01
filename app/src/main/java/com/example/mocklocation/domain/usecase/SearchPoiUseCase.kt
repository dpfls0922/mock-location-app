package com.example.mocklocation.domain.usecase

import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.domain.entity.Poi
import com.example.mocklocation.domain.repository.TmapRepository
import javax.inject.Inject

class SearchPoiUseCase @Inject constructor(
    private val repository: TmapRepository
) {
    suspend operator fun invoke(
        searchTypCd: String,
        keyword: String,
        currentLocation: CurrentLocation
    ): Result<List<Poi>> = runCatching {
        repository.searchPoi(searchTypCd, keyword, currentLocation)
    }
}