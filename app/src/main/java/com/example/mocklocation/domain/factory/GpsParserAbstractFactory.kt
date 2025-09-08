package com.example.mocklocation.domain.factory

import com.example.mocklocation.domain.parser.GpsDataParser

enum class ParserType {
    CSV, TVAS_KOTLIN, TVAS_NATIVE
}

interface GpsParserAbstractFactory {
    fun <IN> createParser(type: ParserType): GpsDataParser<IN>
}