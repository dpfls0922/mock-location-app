package com.example.mocklocation.data.repository

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.annotation.RequiresApi
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.repository.MockLocationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockLocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MockLocationRepository {
    companion object {
        private const val PROVIDER_NAME = LocationManager.GPS_PROVIDER
        private const val EXTRA_SATELLITES = "satellites"
        private const val EXTRA_MAX_CN0 = "maxCn0"
        private const val EXTRA_MEAN_CN0 = "meanCn0"
    }

    private val locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun startMocking() {
        runCatching {
            removeTestProvider()

            locationManager.addTestProvider(
                PROVIDER_NAME,
                false, false, false, false, true, true, true,
                ProviderProperties.POWER_USAGE_LOW,
                ProviderProperties.ACCURACY_FINE
            )
            locationManager.setTestProviderEnabled(PROVIDER_NAME, true)
        }.onFailure {
            Timber.e(it, "Mock GPS 전송 시작 실패")
        }
    }

    override fun stopMocking() {
        runCatching {
            removeTestProvider()
        }.onFailure {
            Timber.e(it, "Mock GPS 전송 종료 실패")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun sendLocation(point: GpsPoint): Result<Unit> {
        val mockLocation = createMockLocation(point)
        return runCatching {
            locationManager.setTestProviderLocation(PROVIDER_NAME, mockLocation)
            Timber.d("➡️ Mock GPS 전송: $mockLocation")
            Timber.d("➡️ Mock GPS 전송: (위도=${point.latitude}, 경도=${point.longitude}), 속도=${point.speed}m/s, 시간=${point.timestamp}, 정확도=${point.hAcc}m, 방위=${point.bearing}°, 위성=${point.satellites}개")
        }.onFailure {
            if (it is SecurityException) {
                Timber.e(it, "개발자 옵션에서 '모의 위치 앱'을 활성화해주세요")
            } else {
                Timber.e(it, "Mock 위치 전송 실패: ${it.message})")
            }
        }
    }

    private fun removeTestProvider() {
        runCatching {
            locationManager.removeTestProvider(PROVIDER_NAME)
        }.onFailure {
            Timber.w(it, "Mock GPS 제공자 제거 실패: $PROVIDER_NAME")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMockLocation(gpsPoint: GpsPoint): Location {
        return Location(gpsPoint.provider).apply {
            latitude = gpsPoint.latitude
            longitude = gpsPoint.longitude
            time = gpsPoint.timestamp
            elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()

            accuracy = gpsPoint.hAcc
            speed = gpsPoint.speed
            bearing = gpsPoint.bearing
            altitude = gpsPoint.altitude

            verticalAccuracyMeters = gpsPoint.vAcc
            speedAccuracyMetersPerSecond = gpsPoint.sAcc
            bearingAccuracyDegrees = gpsPoint.bAcc

            val extras = Bundle().apply {
                putInt(EXTRA_SATELLITES, gpsPoint.satellites)
                putInt(EXTRA_MAX_CN0, gpsPoint.maxCn0)
                putInt(EXTRA_MEAN_CN0, gpsPoint.meanCn0)
            }
            this.extras = extras
        }
    }
}