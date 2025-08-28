package com.example.mocklocation.data.parser

import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.usecase.GpsDataParser
import java.io.InputStream

class CsvGpsParser(
    private val gpsRegex: Regex = DEFAULT_REGEX
) : GpsDataParser<InputStream> {
    override fun parse(input: InputStream): List<GpsPoint> {
        val result = mutableListOf<GpsPoint>()
        input.bufferedReader().useLines { lines ->
            lines.forEachIndexed { idx, line ->
                if (!line.contains("#onLocationChanged")) return@forEachIndexed
                parseLine(line)?.let { result.add(it) }
            }
        }
        return result
    }

    fun parseLine(line: String): GpsPoint? {
        val values = findValues(line, gpsRegex) ?: return null
        if (values.size != 7) return null

        val timestamp = values.getOrNull(0)?.toLongOrNull() ?: return null
        val latitude = values.getOrNull(1)?.toDoubleOrNull() ?: return null
        val longitude = values.getOrNull(2)?.toDoubleOrNull() ?: return null
        val accuracy = values.getOrNull(3)?.toFloatOrNull() ?: return null
        val speed = values.getOrNull(4)?.toFloatOrNull() ?: return null
        val bearing = values.getOrNull(5)?.toFloatOrNull() ?: return null
        val satellites = values.getOrNull(6)?.toIntOrNull() ?: return null

        return GpsPoint(timestamp, latitude, longitude, accuracy, speed, bearing, satellites)
    }

    fun findValues(line: String, regex: Regex): List<String>? {
        return regex.find(line)?.destructured?.toList()
    }

    companion object {
        val DEFAULT_REGEX = Regex(
            """time:\s*(\d+).*?Location\[(?:gps|dr_gps)\s*([\d.-]+),([\d.-]+).*?hAcc=([\d.]+).*?vel=([\d.]+).*?bear=([\d.]+).*?Bundle\[\{satellites=(\d+)""",
            RegexOption.DOT_MATCHES_ALL
        )
    }
}