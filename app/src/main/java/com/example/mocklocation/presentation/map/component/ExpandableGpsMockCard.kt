package com.example.mocklocation.presentation.map.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@Composable
fun ExpandableGpsMockCard(
    mockingState: MockingState,
    gpsPoints: List<GpsPoint>,
    currentLocationUiModel: MockLocationUiModel?,
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
                    .fillMaxWidth(0.3f)
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                mockingState = mockingState,
                gpsPoints = gpsPoints,
                currentLocationUiModel = currentLocationUiModel,
                onStartMock,
                onStopMock
            )
        }
    }
}