package com.example.mocklocation.domain.usecase

import java.io.InputStream

interface GpsParserAbstractFactory {
    fun createCsvParser(): GpsDataParser<InputStream>
}