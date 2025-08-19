package com.example.mocklocation.data.repository

import com.example.mocklocation.data.parser.CsvGpsParser
import com.example.mocklocation.di.IoDispatcher
import com.example.mocklocation.domain.repository.GpsRepository
import com.example.mocklocation.domain.entity.GpsPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GpsRepositoryImpl @Inject constructor(
    private val csvParser: CsvGpsParser,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GpsRepository {
    override suspend fun getPointsFromCsv(file: File): List<GpsPoint> =
        withContext(ioDispatcher) {
            csvParser.parse(file)
        }
}