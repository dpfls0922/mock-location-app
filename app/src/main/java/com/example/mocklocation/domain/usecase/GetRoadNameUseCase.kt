package com.example.mocklocation.domain.usecase

import com.example.mocklocation.domain.entity.SKCoord
import com.example.mocklocation.domain.entity.TouchLocation
import com.example.mocklocation.domain.repository.TmapRepository
import com.skt.tmap.vsm.coordinates.VSMCoordinates
import javax.inject.Inject

class GetRoadNameUseCase @Inject constructor(
    private val repository: TmapRepository
) {
    suspend operator fun invoke(longitude: Double, latitude: Double): Result<TouchLocation> = runCatching {
        val coord = VSMCoordinates.wgs84ToSK(longitude, latitude)
        val skCoord = SKCoord(coord[0], coord[1])
        repository.searchRoadName(skCoord)
    }
}