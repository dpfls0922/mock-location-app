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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mocklocation.presentation.draw.DrawViewModel
import com.example.mocklocation.presentation.file.FileUploadViewModel
import com.example.mocklocation.presentation.map.MapViewModel
import com.example.mocklocation.presentation.mock.MockLocationViewModel
import com.example.mocklocation.presentation.route.RouteGuidanceViewModel
import com.example.mocklocation.presentation.search.SearchViewModel

@Composable
fun GPSMockCard(
    modifier: Modifier = Modifier,
    drawViewModel: DrawViewModel,
    mockViewModel: MockLocationViewModel,
    searchViewModel: SearchViewModel,
    routeGuidanceViewModel: RouteGuidanceViewModel,
    fileUploadViewModel: FileUploadViewModel,
    mapViewModel: MapViewModel
) {
    val gpsPoints by mockViewModel.gpsPoints.collectAsState()
    val mockingState by mockViewModel.mockingState.collectAsState()
    val currentLocationUiModel by mapViewModel.currentLocationUiModel.collectAsState()
    val repeatMode by mockViewModel.repeatMode.collectAsState()
    val repeatCount by mockViewModel.repeatCount.collectAsState()
    val speed by mockViewModel.speed.collectAsState()
    val gpsMode by mockViewModel.gpsMode.collectAsState()
    val searchQuery by searchViewModel.searchQuery.collectAsState()
    val autoSuggestions by searchViewModel.autoSuggestions.collectAsState()
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
                    onValueChange = { query -> searchViewModel.onSearchQueryChanged(query) }
                )

                if (autoSuggestions.isNotEmpty()) {
                    AutoCompleteList(
                        autoSuggestions = autoSuggestions,
                        onItemClick = { autoComplete ->
                            searchViewModel.onAutoCompleteItemClick(autoComplete)
                        }
                    )
                }

                if (routePoints.isNotEmpty()) {
                    RoutePointsList(
                        routePoints = routePoints,
                        onRemovePoint = { routePoint ->
                            routeGuidanceViewModel.removeRoutePoint(routePoint)
                        },
                        onReset = {
                            routeGuidanceViewModel.resetRoutePoints()
                            mockViewModel.resetMocking()
                        },
                        onRouteGuidance = { routeGuidanceViewModel.startGuidance() }
                    )
                }

                CurrentLocation(location = currentLocationUiModel)

                GPSMode(
                    gpsMode = gpsMode,
                    onGpsModeChanged = { mode -> mockViewModel.updateGpsMode(mode = mode) }
                )

                RepeatMode(
                    repeatMode = repeatMode,
                    repeatCount = repeatCount,
                    onRepeatModeChanged = { newMode ->
                        mockViewModel.updateRepeatSettings(
                            mode = newMode,
                            count = repeatCount
                        )
                    },
                    onRepeatCountChanged = { count ->
                        mockViewModel.updateRepeatSettings(
                            mode = repeatMode,
                            count = count
                        )
                    }
                )

                SpeedSection(
                    speed = speed,
                    onSpeedChanged = { newSpeed -> mockViewModel.updateSpeed(newSpeed = newSpeed) }
                )

                MockGpsButton(
                    mockingState = mockingState,
                    gpsPoints = gpsPoints,
                    onStartMock = {
                        mockViewModel.startOrResumeMocking()
                        drawViewModel.disableDrawingMode()
                    },
                    onStopMock =  { mockViewModel.stopMocking() },
                    onResetMock = {
                        mockViewModel.resetMocking()
                        fileUploadViewModel.resetFileName()
                    }
                )
            }
        }
    }

    if (showRouteDialog && selectedLocationForRoute != null) {
        RouteSelectionDialog(
            selectedLocation = selectedLocationForRoute!!,
            onDismiss = { searchViewModel.dismissRouteDialog() },
            onRouteTypeSelected = { routeType ->
                selectedLocationForRoute?.let { location ->
                    routeGuidanceViewModel.addRoutePoint(routeType, location)
                }
            }
        )
    }
}