package com.example.mocklocation.domain.usecase

import com.example.mocklocation.di.CsvParser
import com.example.mocklocation.di.TvasKotlinParser
import java.io.InputStream
import javax.inject.Inject

class GpsParserFactory @Inject constructor(
    @CsvParser private val csvParser: GpsDataParser<InputStream>,
    @TvasKotlinParser private val tvasKotlinParser: GpsDataParser<String>
) : GpsParserAbstractFactory {
    override fun createCsvParser(): GpsDataParser<InputStream> = csvParser
    override fun createTvasKotlinParser(): GpsDataParser<String> = tvasKotlinParser
}