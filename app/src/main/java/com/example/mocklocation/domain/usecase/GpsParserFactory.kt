package com.example.mocklocation.domain.usecase

import com.example.mocklocation.di.CsvParser
import java.io.InputStream
import javax.inject.Inject

class GpsParserFactory @Inject constructor(
    @CsvParser private val csvParser: GpsDataParser<InputStream>
) : GpsParserAbstractFactory {
    override fun createCsvParser(): GpsDataParser<InputStream> = csvParser
}