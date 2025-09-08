package com.example.mocklocation.domain.parser

import com.example.mocklocation.domain.entity.GpsPoint

interface GpsDataParser<IN> {
    suspend fun parse(input: IN): List<GpsPoint>
}