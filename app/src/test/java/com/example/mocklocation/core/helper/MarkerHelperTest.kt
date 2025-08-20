import android.content.Context
import com.example.mocklocation.core.helper.MarkerHelper
import com.example.mocklocation.core.helper.MarkerType
import com.example.mocklocation.core.marker.MarkerIconProvider
import com.skt.tmap.vsm.data.VSMMapPoint
import com.skt.tmap.vsm.map.MapEngine
import com.skt.tmap.vsm.map.marker.VSMMarkerManager
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Test

@DisplayName("MarkerHelper 테스트")
@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class MarkerHelperTest {
    private lateinit var context: Context
    private lateinit var mapEngine: MapEngine
    private lateinit var markerManager: VSMMarkerManager
    private lateinit var iconProvider: MarkerIconProvider
    private lateinit var markerHelper: MarkerHelper

    @BeforeEach
    fun setup() {
        context = mockk()
        mapEngine = mockk()
        markerManager = mockk(relaxed = true)
        iconProvider = mockk(relaxed = true)

        every { mapEngine.markerManager } returns markerManager

        markerHelper = MarkerHelper(context, mapEngine, iconProvider)
    }

    @AfterEach
    fun teardown() {
        clearAllMocks()
    }

    @Test
    @DisplayName("마커 추가 시 Marker 객체가 반환되고, MarkerManager에 추가되어야 함")
    fun testAddLocationMarker() {
        val latitude = 37.0
        val longitude = 127.0
        val type = MarkerType.DEFAULT

        val marker = markerHelper.addLocationMarker(latitude, longitude, type)

        assertNotNull(marker)
        verify { markerManager.addMarker(any()) }
    }

    @Test
    @DisplayName("drawPolyline 호출 시 모든 마커 제거 후, Polyline/Start/End 마커가 추가되어야 함")
    fun testDrawPolyline() {
        val points = listOf(
            VSMMapPoint(127.0, 37.0),
            VSMMapPoint(128.0, 38.0)
        )

        markerHelper.drawPolyline(points)

        verify { markerManager.removeMarkerAll() }
        // Start, Destination, Polyline → 최소 3개 이상 addMarker 호출되어야 함
        verify(atLeast = 3) { markerManager.addMarker(any()) }
    }
}