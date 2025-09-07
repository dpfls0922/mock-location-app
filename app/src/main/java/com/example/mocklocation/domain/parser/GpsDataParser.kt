package com.example.mocklocation.domain.parser

import com.example.mocklocation.domain.entity.GpsPoint

interface GpsDataParser<T> {
    fun parse(input: T): List<GpsPoint>
}