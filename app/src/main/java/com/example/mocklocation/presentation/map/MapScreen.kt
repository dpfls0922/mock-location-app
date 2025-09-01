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
    val touchedLocation by mapViewModel.touchedLocation.collectAsState()

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

    LaunchedEffect(touchedLocation) {
        touchedLocation?.let {
            mapViewModel.onTouchedLocationChanged(it)
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
                    mockViewModel = mockViewModel,
                    searchViewModel = searchViewModel,
                    routeGuidanceViewModel = routeGuidanceViewModel,
                    mapViewModel = mapViewModel,
                    onStartMock = onClickStartMockSend,
                    onStopMock = onClickStopMockSend
                )
            }
        }
    }
}