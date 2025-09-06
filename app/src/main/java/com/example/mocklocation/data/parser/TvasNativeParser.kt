package com.example.mocklocation.data.parser

import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.usecase.GpsDataParser

class TvasNativeParser : GpsDataParser<String> {
    companion object {
        init {
            System.loadLibrary("tvas_parser")
        }
    }

    override fun parse(input: String): List<GpsPoint> {
        return parseNative(input).toList()
    }

    private external fun parseNative(input: String): Array<GpsPoint>
}