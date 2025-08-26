package com.example.mocklocation.data.repository

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.SystemClock
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
        private const val DEFAULT_SPEED = 5f
        private const val DEFAULT_BEARING = 90f
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
            Timber.d("✅ Mock GPS 전송 시작")
        }.onFailure {
            Timber.e(it, "Mock GPS 전송 시작 실패")
        }
    }

    override fun stopMocking() {
        removeTestProvider()
        runCatching {
            removeTestProvider()
            Timber.d("✅ Mock GPS 전송 종료")
        }.onFailure {
            Timber.e(it, "Mock GPS 전송 종료 실패")
        }
    }

    override fun sendLocation(point: GpsPoint): Result<Unit> {
        val mockLocation = createMockLocation(point)
        return runCatching {
            locationManager.setTestProviderLocation(PROVIDER_NAME, mockLocation)
            Timber.d("➡️ Mock GPS 전송: (위도=${point.latitude}, 경도=${point.longitude}), 속도=${point.speed}m/s, 시간=${point.timestamp}, 정확도=${point.accuracy}m, 방위=${point.bearing}°, 위성=${point.satellites}개")
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
        }
    }

    private fun createMockLocation(point: GpsPoint): Location {
        return Location(PROVIDER_NAME).apply {
            latitude = point.latitude
            longitude = point.longitude
            accuracy = point.accuracy
            time = point.timestamp
            speed = point.speed.takeIf { it > 0 } ?: DEFAULT_SPEED
            bearing = point.bearing.takeIf { it > 0 } ?: DEFAULT_BEARING
            elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        }
    }
}