package com.example.mocklocation.androidservice

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.example.mocklocation.data.dto.GpsPointParcelable
import com.example.mocklocation.data.dto.toDomainModel
import com.example.mocklocation.presentation.mock.component.RepeatMode
import com.example.mocklocation.presentation.mock.MockLocationHandler
import dagger.hilt.android.AndroidEntryPoint
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
        const val EXTRA_GPS_POINTS = "com.example.mocklocation.extra.GPS_POINTS"
        const val EXTRA_REPEAT_MODE = "com.example.mocklocation.extra.REPEAT_MODE"
        const val EXTRA_REPEAT_COUNT = "com.example.mocklocation.extra.REPEAT_COUNT"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_MOCKING -> {
                val parcelableList = getGpsPointParcelableFromIntent(intent)
                val repeatMode = intent.getStringExtra(EXTRA_REPEAT_MODE) ?: RepeatMode.INFINITE.name
                val repeatCount = intent.getIntExtra(EXTRA_REPEAT_COUNT, 0)

                parcelableList?.let {
                    val domainGpsPoints = it.map { p -> p.toDomainModel() }
                    mockLocationHandler.startSending(domainGpsPoints, RepeatMode.valueOf(repeatMode), repeatCount)
                }
            }
            ACTION_RESUME_MOCKING -> {
                mockLocationHandler.resumeSending()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        mockLocationHandler.stopSending()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun getGpsPointParcelableFromIntent(intent: Intent): List<GpsPointParcelable>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(EXTRA_GPS_POINTS, GpsPointParcelable::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra(EXTRA_GPS_POINTS)
        }
    }
}