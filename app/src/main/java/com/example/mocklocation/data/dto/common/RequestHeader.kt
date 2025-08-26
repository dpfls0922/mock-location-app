package com.example.mocklocation.data.dto.common

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data class RequestHeader(
    val reqTime: String,
    val svcType: Int = 126,
    val osType: String = "AND",
    val osVersion: String = "11",
    val deviceId: String = "2-1-b8ada897-5c27-33f7-a1c4-561ebd204e28",
    val carrier: String = "",
    val modelNo: String = "SM-T510",
    val appVersion: String = "0.2.2.08",
    val buildNo: String = "801000",
    val resolution: String = "FullHD",
    val using: String = "MAIN",
    val screenWidth: Int = 1920,
    val screenHeight: Int = 672,
    val pushDeviceKey: String = "",
    val appLaunchCount: Int = 0
)

object RequestHeaderFactory {
    fun create(): RequestHeader {
        val reqTime = SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(Date())
        return RequestHeader(reqTime = reqTime)
    }
}