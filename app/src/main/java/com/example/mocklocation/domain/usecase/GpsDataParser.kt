package com.example.mocklocation.domain.usecase

import com.example.mocklocation.domain.entity.GpsPoint

interface GpsDataParser<T> {
    fun parse(input: T): List<GpsPoint>
}