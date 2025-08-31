package com.example.mocklocation.presentation.map.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.presentation.mock.MockingState

@Composable
fun GPSMockCard(
    modifier: Modifier = Modifier,
    mockingState: MockingState,
    gpsPoints: List<GpsPoint>,
    currentLocationUiModel: MockLocationUiModel?,
    repeatMode: RepeatMode,
    repeatCount: Int,
    onRepeatModeChanged: (RepeatMode) -> Unit,
    onRepeatCountChanged: (Int) -> Unit,
    onStartMock: () -> Unit,
    onStopMock: () -> Unit
) {
    var selectedSpeed by remember { mutableStateOf(60) }
    var selectedMode by remember { mutableStateOf(GpsMode.GPS) }
    var isGpsEnabled by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MockGpsStatus(mockingState = mockingState)
                SearchBar(
                    value = searchQuery,
                    onValueChange = { searchQuery = it }
                )

                CurrentLocation(location = currentLocationUiModel)

                GPSMode(
                    selectedMode = selectedMode,
                    onModeSelected = { selectedMode = it }
                )
                RepeatMode(
                    repeatMode = repeatMode,
                    repeatCount = repeatCount,
                    onRepeatModeChanged = onRepeatModeChanged,
                    onRepeatCountChanged = onRepeatCountChanged
                )

                SpeedSection(
                    speed = selectedSpeed,
                    onSpeedChanged = { selectedSpeed = it }
                )

                MockGpsButton(
                    mockingState = mockingState,
                    gpsPoints = gpsPoints,
                    onStartMock = {
                        isGpsEnabled = true
                        onStartMock()
                    },
                    onStopMock = {
                        isGpsEnabled = false
                        onStopMock()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GPSMockCardPreview() {
    GPSMockCard(
        mockingState = MockingState.IDLE,
        gpsPoints = listOf(),
        currentLocationUiModel = MockLocationUiModel(37.5665, 126.9780),
        repeatMode = RepeatMode.INFINITE,
        repeatCount = 0,
        onRepeatModeChanged = { },
        onRepeatCountChanged = { },
        onStartMock = { },
        onStopMock = { }
    )
}