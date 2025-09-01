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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.presentation.mock.MockingState
import com.example.mocklocation.presentation.search.AutoCompleteUiModel
import com.example.mocklocation.presentation.search.RouteGuidanceViewModel
import com.example.mocklocation.presentation.search.SearchViewModel

@Composable
fun GPSMockCard(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel,
    routeGuidanceViewModel: RouteGuidanceViewModel,
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
    onAutoCompleteItemClick: (AutoCompleteUiModel) -> Unit,
    onRouteTypeSelected: (RoutePointType) -> Unit,
    onRouteGuidance: () -> Unit,
    onRouteDialogDismiss: () -> Unit,
    onRemoveRoutePoint: (RoutePoint) -> Unit,
    onStartMock: () -> Unit,
    onStopMock: () -> Unit
) {
    var isGpsEnabled by remember { mutableStateOf(true) }
    val showRouteDialog by searchViewModel.showRouteDialog.collectAsState()
    val selectedLocationForRoute by searchViewModel.selectedLocationForRoute.collectAsState()
    val routePoints by routeGuidanceViewModel.routePoints.collectAsState()
    val closeDialogEvent by routeGuidanceViewModel.closeRouteDialogEvent.collectAsState()

    LaunchedEffect(closeDialogEvent) {
        if (closeDialogEvent) {
            searchViewModel.dismissRouteDialog()
            routeGuidanceViewModel.resetCloseDialogEvent()
        }
    }

    Column(
        modifier = modifier.padding(8.dp)
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
                    onValueChange = onQueryChange
                )

                if (autoSuggestions.isNotEmpty()) {
                    AutoCompleteList(
                        autoSuggestions = autoSuggestions,
                        onItemClick = onAutoCompleteItemClick
                    )
                }

                if (routePoints.isNotEmpty()) {
                    RoutePointsList(
                        routePoints = routePoints,
                        onRemovePoint = onRemoveRoutePoint,
                        onRouteGuidance = onRouteGuidance
                    )
                }

                CurrentLocation(location = currentLocationUiModel)

                GPSMode(
                    gpsMode = gpsMode,
                    onGpsModeChanged = onGpsModeChanged
                )

                RepeatMode(
                    repeatMode = repeatMode,
                    repeatCount = repeatCount,
                    onRepeatModeChanged = onRepeatModeChanged,
                    onRepeatCountChanged = onRepeatCountChanged
                )

                SpeedSection(
                    speed = speed,
                    onSpeedChanged = onSpeedChanged
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

    if (showRouteDialog && selectedLocationForRoute != null) {
        RouteSelectionDialog(
            selectedLocation = selectedLocationForRoute!!,
            onDismiss = onRouteDialogDismiss,
            onRouteTypeSelected = onRouteTypeSelected
        )
    }
}