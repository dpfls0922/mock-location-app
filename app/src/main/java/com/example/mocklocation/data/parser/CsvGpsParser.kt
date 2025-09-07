package com.example.mocklocation.data.parser

import com.example.mocklocation.di.IoDispatcher
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.parser.GpsDataParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.InputStream

class CsvGpsParser(
    private val gpsRegex: Regex = DEFAULT_REGEX,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GpsDataParser<InputStream> {
    override suspend fun parse(input: InputStream): List<GpsPoint> = withContext(ioDispatcher) {
        val result = mutableListOf<GpsPoint>()
        input.bufferedReader().useLines { lines ->
            lines.forEachIndexed { idx, line ->
                if (!line.contains("#onLocationChanged")) return@forEachIndexed
                parseLine(line)?.let { result.add(it) }
            }
        }
        result
    }

    fun parseLine(line: String): GpsPoint? {
        val values = findValues(line, gpsRegex) ?: return null
        if (values.size != 14) return null

        return runCatching {
            GpsPoint(
                provider = values[0],
                timestamp = values[1].toLong(),
                latitude = values[2].toDouble(),
                longitude = values[3].toDouble(),
                hAcc = values[4].toFloat(),
                altitude = values[5].toDouble(),
                vAcc = values[6].toFloat(),
                speed = values[7].toFloat(),
                sAcc = values[8].toFloat(),
                bearing = values[9].toFloat(),
                bAcc = values[10].toFloat(),
                satellites = values[11].toInt(),
                maxCn0 = values[12].toInt(),
                meanCn0 = values[13].toInt()
            )
        }.onFailure {
            Timber.e(it, "로그 데이터 변환 실패: $line")
        }.getOrNull()
    }

    fun findValues(line: String, regex: Regex): List<String>? {
        return regex.find(line)?.destructured?.toList()
    }

    companion object {
        val DEFAULT_REGEX = Regex(
            """provider:(gps|dr_gps),\s*time:\s*(\d+).*?Location\[(?:gps|dr_gps)\s*([\d.-]+),([\d.-]+).*?hAcc=([\d.]+).*?alt=([\d.]+).*?vAcc=([\d.]+).*?vel=([\d.]+).*?sAcc=([\d.]+).*?bear=([\d.]+).*?bAcc=([\d.]+).*?satellites=(\d+),\s*maxCn0=([\d.]+),\s*meanCn0=([\d.]+)""",
            RegexOption.DOT_MATCHES_ALL
        )
    }
}