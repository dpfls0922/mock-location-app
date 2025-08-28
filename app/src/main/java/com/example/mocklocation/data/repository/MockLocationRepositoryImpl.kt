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
        private const val GPS_PROVIDER_NAME = LocationManager.GPS_PROVIDER
        private const val DR_GPS_PROVIDER_NAME = "dr_gps"
    }

    private val locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private var currentProviderName: String = GPS_PROVIDER_NAME

    override fun startMocking() {
        runCatching {
            removeTestProvider(GPS_PROVIDER_NAME)
            removeTestProvider(DR_GPS_PROVIDER_NAME)

            locationManager.addTestProvider(
                GPS_PROVIDER_NAME,
                false, false, false, false, true, true, true,
                ProviderProperties.POWER_USAGE_LOW,
                ProviderProperties.ACCURACY_FINE
            )
            locationManager.setTestProviderEnabled(GPS_PROVIDER_NAME, true)

            locationManager.addTestProvider(
                DR_GPS_PROVIDER_NAME,
                false, false, false, false, true, true, true,
                ProviderProperties.POWER_USAGE_LOW,
                ProviderProperties.ACCURACY_COARSE
            )
            locationManager.setTestProviderEnabled(DR_GPS_PROVIDER_NAME, true)
            Timber.d("✅ Mock GPS 전송 시작: GPS 및 DR-GPS 제공자 등록 완료")
        }.onFailure {
            Timber.e(it, "Mock GPS 전송 시작 실패")
        }
    }

    override fun stopMocking() {
        runCatching {
            removeTestProvider(GPS_PROVIDER_NAME)
            removeTestProvider(DR_GPS_PROVIDER_NAME)
            Timber.d("✅ Mock GPS 전송 종료")
        }.onFailure {
            Timber.e(it, "Mock GPS 전송 종료 실패")
        }
    }

    override fun sendLocation(point: GpsPoint): Result<Unit> {
        val provider = point.provider

        if (provider != currentProviderName) {
            Timber.d("➡️ 제공자 변경: ${currentProviderName} -> ${provider}")
            currentProviderName = provider
        }

        val mockLocation = createMockLocation(point, provider)
        return runCatching {
            locationManager.setTestProviderLocation(provider, mockLocation)
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

    private fun removeTestProvider(providerName: String) {
        runCatching {
            locationManager.removeTestProvider(providerName)
        }.onFailure {
            Timber.w(it, "Mock GPS 제공자 제거 실패: $providerName")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMockLocation(gpsPoint: GpsPoint, providerName: String): Location {
        return Location(providerName).apply {
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
                putInt("satellites", gpsPoint.satellites)
                putInt("maxCn0", gpsPoint.maxCn0)
                putInt("meanCn0", gpsPoint.meanCn0)
            }
            this.extras = extras
        }
    }
}