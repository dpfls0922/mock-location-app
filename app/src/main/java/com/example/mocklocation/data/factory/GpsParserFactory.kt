package com.example.mocklocation.data.factory

import com.example.mocklocation.di.CsvParser
import com.example.mocklocation.di.TvasKotlinParser
import com.example.mocklocation.di.TvasNativeParser
import com.example.mocklocation.domain.factory.GpsParserAbstractFactory
import com.example.mocklocation.domain.parser.GpsDataParser
import java.io.InputStream
import javax.inject.Inject

class GpsParserFactory @Inject constructor(
    @CsvParser private val csvParser: GpsDataParser<InputStream>,
    @TvasKotlinParser private val tvasKotlinParser: GpsDataParser<String>,
    @TvasNativeParser private val tvasNativeParser: GpsDataParser<String>
) : GpsParserAbstractFactory {
    override fun createCsvParser(): GpsDataParser<InputStream> = csvParser
    override fun createTvasKotlinParser(): GpsDataParser<String> = tvasKotlinParser
    override fun createTvasNativeParser(): GpsDataParser<String> = tvasNativeParser
}