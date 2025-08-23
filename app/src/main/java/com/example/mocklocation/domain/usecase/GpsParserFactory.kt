package com.example.mocklocation.domain.usecase

import com.example.mocklocation.di.CsvParser
import java.io.InputStream
import javax.inject.Inject

enum class ParserType { CSV }

class GpsParserFactory @Inject constructor(
    @CsvParser private val csvParser: GpsDataParser<InputStream>
) {
    fun <T> getParser(type: ParserType): GpsDataParser<T> = when (type) {
        ParserType.CSV -> csvParser as GpsDataParser<T>
    }
}