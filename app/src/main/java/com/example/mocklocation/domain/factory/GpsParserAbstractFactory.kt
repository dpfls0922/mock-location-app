package com.example.mocklocation.domain.factory

import com.example.mocklocation.domain.parser.GpsDataParser
import java.io.InputStream

interface GpsParserAbstractFactory {
    fun createCsvParser(): GpsDataParser<InputStream>
    fun createTvasKotlinParser(): GpsDataParser<String>
    fun createTvasNativeParser(): GpsDataParser<String>
}