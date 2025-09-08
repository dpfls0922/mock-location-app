package com.example.mocklocation.data.factory

import com.example.mocklocation.di.CsvParser
import com.example.mocklocation.di.TvasKotlinParser
import com.example.mocklocation.di.TvasNativeParser
import com.example.mocklocation.domain.factory.GpsParserAbstractFactory
import com.example.mocklocation.domain.factory.ParserType
import com.example.mocklocation.domain.parser.GpsDataParser
import java.io.InputStream
import javax.inject.Inject

class GpsParserFactory @Inject constructor(
    @CsvParser private val csvParser: GpsDataParser<InputStream>,
    @TvasKotlinParser private val tvasKotlinParser: GpsDataParser<String>,
    @TvasNativeParser private val tvasNativeParser: GpsDataParser<String>
) : GpsParserAbstractFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <IN> createParser(type: ParserType): GpsDataParser<IN> {
        return when(type) {
            ParserType.CSV -> csvParser as GpsDataParser<IN>
            ParserType.TVAS_KOTLIN -> tvasKotlinParser as GpsDataParser<IN>
            ParserType.TVAS_NATIVE -> tvasNativeParser as GpsDataParser<IN>
        }
    }
}