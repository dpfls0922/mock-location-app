package com.example.mocklocation.presentation.map.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.presentation.mock.MockingState
import com.example.mocklocation.presentation.search.AutoCompleteUiModel

@Composable
fun ExpandableGpsMockCard(
    mockingState: MockingState,
    gpsPoints: List<GpsPoint>,
    currentLocationUiModel: MockLocationUiModel?,
    repeatMode: RepeatMode,
    repeatCount: Int,
    speed: Int,
    gpsMode: GpsMode,
    searchQuery: String,
    autoSuggestions: List<AutoCompleteUiModel>,
    onRepeatModeChanged: (RepeatMode) -> Unit,
    onRepeatCountChanged: (Int) -> Unit,
    onSpeedChanged: (Int) -> Unit,
    onGpsModeChanged: (GpsMode) -> Unit,
    onQueryChange: (String) -> Unit,
    onStartMock: () -> Unit,
    onStopMock: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        IconButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .zIndex(1f)
        ) {
            Icon(
                if (expanded) Icons.AutoMirrored.Filled.KeyboardArrowLeft else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = if (expanded) "접기" else "펼치기",
            )
        }

        if (expanded) {
            GPSMockCard(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(400.dp)
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                mockingState = mockingState,
                gpsPoints = gpsPoints,
                currentLocationUiModel = currentLocationUiModel,
                repeatMode = repeatMode,
                repeatCount = repeatCount,
                speed = speed,
                gpsMode = gpsMode,
                searchQuery = searchQuery,
                autoSuggestions = autoSuggestions,
                onRepeatModeChanged = onRepeatModeChanged,
                onRepeatCountChanged = onRepeatCountChanged,
                onSpeedChanged = onSpeedChanged,
                onGpsModeChanged = onGpsModeChanged,
                onQueryChange = onQueryChange,
                onStartMock,
                onStopMock
            )
        }
    }
}