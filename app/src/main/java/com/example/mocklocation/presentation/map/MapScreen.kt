package com.example.mocklocation.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.mocklocation.presentation.map.component.ExpandableGpsMockCard
import com.example.mocklocation.presentation.map.component.FloatingButtons
import com.example.mocklocation.presentation.mock.MockLocationViewModel
import com.example.mocklocation.presentation.search.RouteGuidanceState
import com.example.mocklocation.presentation.search.RouteGuidanceViewModel
import com.example.mocklocation.presentation.search.SearchViewModel
import com.example.mocklocation.presentation.ui.theme.MapTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    mockViewModel: MockLocationViewModel,
    searchViewModel: SearchViewModel,
    routeGuidanceViewModel: RouteGuidanceViewModel,
    permissionGranted: Boolean,
    onRequestPermission: () -> Unit,
    onClickMyLocation: () -> Unit,
    onClickSelectCsvFile: () -> Unit,
    onClickStartMockSend: () -> Unit,
    onClickStopMockSend: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val gpsPoints by mockViewModel.gpsPoints.collectAsState()
    val mockingState by mockViewModel.mockingState.collectAsState()
    val currentLocationUiModel by mapViewModel.currentLocationUiModel.collectAsState()
    val repeatMode by mockViewModel.repeatMode.collectAsState()
    val repeatCount by mockViewModel.repeatCount.collectAsState()
    val speed by mockViewModel.speed.collectAsState()
    val gpsMode by mockViewModel.gpsMode.collectAsState()
    val searchQuery by searchViewModel.searchQuery.collectAsState()
    val autoSuggestions by searchViewModel.autoSuggestions.collectAsState()
    val selectedLocationForRoute by searchViewModel.selectedLocationForRoute.collectAsState()

    LaunchedEffect(Unit) {
        routeGuidanceViewModel.uiState.collectLatest { state ->
            if (state is RouteGuidanceState.Success) {
                val vsmPoints = state.routeData.vsmPoints
                if (vsmPoints.isNotEmpty()) {
                    mapViewModel.drawRouteLine(vsmPoints)
                }
            }
        }
    }

    LaunchedEffect(permissionGranted) {
        if (!permissionGranted) {
            onRequestPermission()
        } else {
            mapViewModel.checkAndFetchInitialLocation()
        }
    }

    MapTheme {
        Scaffold(
            floatingActionButton = {
                FloatingButtons(
                    onClickMyLocation = onClickMyLocation,
                    onClickSelectCsvFile = onClickSelectCsvFile
                )
            }
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding).fillMaxSize()) {
                AndroidView(
                    factory = { context -> mapViewModel.createMapView(context) },
                    modifier = Modifier.fillMaxSize()
                )
                MapLifecycleHandler(lifecycleOwner, mapViewModel)
                ExpandableGpsMockCard(
                    mockingState = mockingState,
                    searchViewModel = searchViewModel,
                    routeGuidanceViewModel = routeGuidanceViewModel,
                    gpsPoints = gpsPoints,
                    currentLocationUiModel = currentLocationUiModel,
                    repeatMode = repeatMode,
                    repeatCount = repeatCount,
                    speed = speed,
                    gpsMode = gpsMode,
                    searchQuery = searchQuery,
                    autoSuggestions = autoSuggestions,
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
                    },
                    onSpeedChanged = { speed -> mockViewModel.updateSpeed(newSpeed = speed) },
                    onGpsModeChanged = { mode -> mockViewModel.updateGpsMode(mode = mode) },
                    onAutoCompleteItemClick = { autoComplete ->
                        searchViewModel.onAutoCompleteItemClick(
                            autoComplete
                        )
                    },
                    onQueryChange = { query -> searchViewModel.onSearchQueryChanged(query) },
                    onRouteTypeSelected = { routeType ->
                        selectedLocationForRoute?.let { location ->
                            routeGuidanceViewModel.addRoutePoint(routeType, location)
                        }
                    },
                    onRouteGuidance = { routeGuidanceViewModel.startGuidance() },
                    onRouteDialogDismiss = { searchViewModel.dismissRouteDialog() },
                    onRemoveRoutePoint = { routePoint ->
                        routeGuidanceViewModel.removeRoutePoint(
                            routePoint
                        )
                    },
                    onStartMock = onClickStartMockSend,
                    onStopMock = onClickStopMockSend
                )
            }
        }
    }
}