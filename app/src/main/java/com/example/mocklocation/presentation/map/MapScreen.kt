package com.example.mocklocation.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.mocklocation.presentation.draw.DrawViewModel
import com.example.mocklocation.presentation.map.component.DrawRouteControls
import com.example.mocklocation.presentation.map.component.ExpandableGpsMockCard
import com.example.mocklocation.presentation.map.component.FloatingButtons
import com.example.mocklocation.presentation.mock.MockLocationViewModel
import com.example.mocklocation.presentation.route.RouteGuidanceState
import com.example.mocklocation.presentation.route.RouteGuidanceViewModel
import com.example.mocklocation.presentation.search.SearchViewModel
import com.example.mocklocation.presentation.ui.theme.MapTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    mockViewModel: MockLocationViewModel,
    searchViewModel: SearchViewModel,
    routeGuidanceViewModel: RouteGuidanceViewModel,
    drawViewModel: DrawViewModel,
    permissionGranted: Boolean,
    onRequestPermission: () -> Unit,
    onClickSelectCsvFile: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val touchedLocation by mapViewModel.touchedLocation.collectAsState()
    val isDrawingMode by drawViewModel.isDrawingMode.collectAsState()

    LaunchedEffect(Unit) {
        routeGuidanceViewModel.uiState.collectLatest { state ->
            if (state is RouteGuidanceState.Success) {
                val vsmPoints = state.routeData.vsmPoints
                val wayPoints = state.wayPoints
                if (vsmPoints.isNotEmpty() && wayPoints.isNotEmpty()) {
                    drawViewModel.drawRouteLine(vsmPoints, wayPoints)
                }
            }
        }
    }

    LaunchedEffect(touchedLocation) {
        touchedLocation?.let {
            mapViewModel.onTouchedLocationChanged(it)
            searchViewModel.showRouteSelectionDialogForMapPoint(it)
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
                    onClickMyLocation = { mapViewModel.setCurrentLocation() },
                    onClickSelectCsvFile = onClickSelectCsvFile,
                    onClickDrawRoute = { drawViewModel.toggleDrawingMode() }
                )
            }
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding).fillMaxSize()) {
                AndroidView(
                    factory = { context -> mapViewModel.createMapView(context) },
                    modifier = Modifier.fillMaxSize()
                )
                MapLifecycleHandler(lifecycleOwner, mapViewModel)

                Box (
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    DrawRouteControls(
                        isDrawingMode = isDrawingMode,
                        onCancel = { drawViewModel.cancelDrawing() }
                    )
                }

                ExpandableGpsMockCard(
                    drawViewModel = drawViewModel,
                    mockViewModel = mockViewModel,
                    searchViewModel = searchViewModel,
                    routeGuidanceViewModel = routeGuidanceViewModel,
                    mapViewModel = mapViewModel
                )
            }
        }
    }
}