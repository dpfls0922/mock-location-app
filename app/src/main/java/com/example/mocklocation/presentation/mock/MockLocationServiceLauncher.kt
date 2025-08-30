package com.example.mocklocation.presentation.mock

import android.content.Context
import android.content.Intent
import com.example.mocklocation.data.dto.toParcelable
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.presentation.map.component.RepeatMode
import com.example.mocklocation.service.MockLocationSenderService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MockLocationServiceLauncher @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun startMocking(
        gpsPoints: List<GpsPoint>,
        repeatMode: RepeatMode,
        repeatCount: Int
    ) {
        if (gpsPoints.isEmpty()) return

        val intent = Intent(context, MockLocationSenderService::class.java).apply {
            action = MockLocationSenderService.ACTION_START_MOCKING
            putParcelableArrayListExtra(
                MockLocationSenderService.EXTRA_GPS_POINTS,
                ArrayList(gpsPoints.map { it.toParcelable() })
            )
            putExtra(MockLocationSenderService.EXTRA_REPEAT_MODE, repeatMode.name)
            putExtra(MockLocationSenderService.EXTRA_REPEAT_COUNT, repeatCount)
        }
        context.startService(intent)
    }

    fun resumeMocking() {
        val intent = Intent(context, MockLocationSenderService::class.java).apply {
            action = MockLocationSenderService.ACTION_RESUME_MOCKING
        }
        context.startService(intent)
    }

    fun stopMocking() {
        val intent = Intent(context, MockLocationSenderService::class.java)
        context.stopService(intent)
    }
}