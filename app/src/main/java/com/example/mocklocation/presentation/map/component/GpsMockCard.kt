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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun GPSMockCard(
    modifier: Modifier = Modifier,
    onStartMock: () -> Unit,
    onStopMock: () -> Unit
) {
    var selectedSpeed by remember { mutableStateOf("60km/h") }
    var selectedMode by remember { mutableStateOf(GpsMode.GPS) }
    var repeatMode by remember { mutableStateOf(RepeatMode.INFINITE) }
    var repeatCount by remember { mutableIntStateOf(3) }
    var isGpsEnabled by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var currentLocation by remember {
        mutableStateOf(
            MockLocationUiModel(
                "한통빌딩",
                37.5665,
                126.9780,
                "서울특별시 용산구 원효로"
            )
        )
    }

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
                MockGpsStatus(isEnabled = isGpsEnabled)
                SearchBar(
                    value = searchQuery,
                    onValueChange = { searchQuery = it }
                )

                CurrentLocation(location = currentLocation)

                GPSMode(
                    selectedMode = selectedMode,
                    onModeSelected = { selectedMode = it }
                )

                RepeatMode(
                    repeatMode = repeatMode,
                    repeatCount = repeatCount,
                    onRepeatModeChanged = { repeatMode = it },
                    onRepeatCountChanged = { repeatCount = it }
                )

                SpeedSection(
                    selectedSpeed = selectedSpeed,
                    onSpeedSelected = { selectedSpeed = it }
                )

                MockGpsButton(
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
        onStartMock = { },
        onStopMock = { }
    )
}