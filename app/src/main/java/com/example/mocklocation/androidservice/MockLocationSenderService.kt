package com.example.mocklocation.androidservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.presentation.mock.component.RepeatMode
import com.example.mocklocation.presentation.mock.MockLocationHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

/**
 * 백그라운드에서 모의 위치(가짜 GPS 정보)를 보내는 역할을 수행
 */
@AndroidEntryPoint
class MockLocationSenderService : Service() {
    @Inject
    lateinit var mockLocationHandler: MockLocationHandler

    companion object {
        const val ACTION_START_MOCKING = "com.example.mocklocation.action.START_MOCKING"
        const val ACTION_RESUME_MOCKING = "com.example.mocklocation.action.RESUME_MOCKING"
        const val EXTRA_GPS_POINTS_FILE = "gps_points_file"
        const val EXTRA_REPEAT_MODE = "com.example.mocklocation.extra.REPEAT_MODE"
        const val EXTRA_REPEAT_COUNT = "com.example.mocklocation.extra.REPEAT_COUNT"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_MOCKING -> {
                val filePath = intent.getStringExtra(EXTRA_GPS_POINTS_FILE)
                val gpsPoints: List<GpsPoint> = if (filePath != null) parseGpsPointsFile(filePath) else emptyList()

                val repeatModeStr = intent.getStringExtra(EXTRA_REPEAT_MODE) ?: RepeatMode.INFINITE.name
                val repeatMode = RepeatMode.valueOf(repeatModeStr)
                val repeatCount = intent.getIntExtra(EXTRA_REPEAT_COUNT, 0)

                if (gpsPoints.isNotEmpty()) {
                    mockLocationHandler.startSending(gpsPoints, repeatMode, repeatCount)
                }
            }
            ACTION_RESUME_MOCKING -> {
                mockLocationHandler.resumeSending()
            }
        }
        return START_STICKY
    }

    private fun parseGpsPointsFile(filePath: String): List<GpsPoint> {
        val file = File(filePath)
        if (!file.exists()) return emptyList()
        val json = file.readText()
        val type = object : TypeToken<List<GpsPoint>>() {}.type
        return Gson().fromJson<List<GpsPoint>>(json, type)
    }

    override fun onDestroy() {
        mockLocationHandler.stopSending()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}