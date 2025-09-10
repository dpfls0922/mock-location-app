package com.example.mocklocation.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.mocklocation.presentation.draw.DrawViewModel
import com.example.mocklocation.presentation.file.FileUploadViewModel
import com.example.mocklocation.presentation.file.UploadUiState
import com.example.mocklocation.presentation.draw.component.DrawRouteControls
import com.example.mocklocation.presentation.map.component.ExpandableGpsMockCard
import com.example.mocklocation.presentation.file.component.FileNameOverlayToastStyle
import com.example.mocklocation.presentation.map.component.FloatingButtons
import com.example.mocklocation.presentation.mock.MockLocationViewModel
import com.example.mocklocation.presentation.route.RouteGuidanceState
import com.example.mocklocation.presentation.route.RouteGuidanceViewModel
import com.example.mocklocation.presentation.search.SearchViewModel
import com.skt.tmap.vsm.map.VSMMapView
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    mockViewModel: MockLocationViewModel,
    searchViewModel: SearchViewModel,
    routeGuidanceViewModel: RouteGuidanceViewModel,
    fileUploadViewModel: FileUploadViewModel,
    drawViewModel: DrawViewModel,
    permissionGranted: Boolean,
    onRequestPermission: () -> Unit,
    onClickSelectCsvFile: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val touchedLocation by mapViewModel.touchedLocation.collectAsState()
    val isDrawingMode by drawViewModel.isDrawingMode.collectAsState()
    val currentFileName by fileUploadViewModel.currentFileName.collectAsState()
    val context = LocalContext.current
    val mapView = remember {
        mapViewModel.createMapView(context)
    }

    LaunchedEffect(Unit) {
        routeGuidanceViewModel.uiState.collectLatest { state ->
            if (state is RouteGuidanceState.Success) {
                val vsmPoints = state.routeData?.vsmPoints
                val wayPoints = state.wayPoints
                if (!vsmPoints.isNullOrEmpty()) {
                    drawViewModel.drawRouteLine(vsmPoints, wayPoints)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        fileUploadViewModel.uiState.collectLatest { state ->
            if (state is UploadUiState.Success) {
                val vsmPoints = state.vsmPoints
                if (vsmPoints.isNotEmpty()) {
                    drawViewModel.drawRouteLine(vsmPoints)
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

    MapUI(
        mapView = mapView,
        lifecycleOwner = lifecycleOwner,
        mapViewModel = mapViewModel,
        drawViewModel = drawViewModel,
        mockViewModel = mockViewModel,
        searchViewModel = searchViewModel,
        routeGuidanceViewModel = routeGuidanceViewModel,
        fileUploadViewModel = fileUploadViewModel,
        currentFileName = currentFileName,
        isDrawingMode = isDrawingMode,
        onClickSelectCsvFile = onClickSelectCsvFile
    )
}

@Composable
private fun MapUI(
    mapView: VSMMapView,
    lifecycleOwner: LifecycleOwner,
    mapViewModel: MapViewModel,
    drawViewModel: DrawViewModel,
    mockViewModel: MockLocationViewModel,
    searchViewModel: SearchViewModel,
    routeGuidanceViewModel: RouteGuidanceViewModel,
    fileUploadViewModel: FileUploadViewModel,
    currentFileName: String?,
    isDrawingMode: Boolean,
    onClickSelectCsvFile: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingButtons(
                onClickMyLocation = { mapViewModel.setCurrentLocation() },
                onClickSelectCsvFile = onClickSelectCsvFile,
                onClickDrawRoute = {
                    drawViewModel.toggleDrawingMode()
                    if (drawViewModel.isDrawingMode.value) {
                        fileUploadViewModel.resetFileName()
                        mockViewModel.resetMocking()
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding).fillMaxSize()) {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize()
            )
            MapLifecycleHandler(lifecycleOwner, mapViewModel)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 28.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                currentFileName?.let { name ->
                    FileNameOverlayToastStyle(fileName = name)
                }
            }

            Box (
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                DrawRouteControls(
                    isDrawingMode = isDrawingMode,
                    onCancel = {
                        drawViewModel.cancelDrawing()
                        mockViewModel.resetMocking()
                    }
                )
            }

            ExpandableGpsMockCard(
                drawViewModel = drawViewModel,
                mockViewModel = mockViewModel,
                searchViewModel = searchViewModel,
                routeGuidanceViewModel = routeGuidanceViewModel,
                fileUploadViewModel = fileUploadViewModel,
                mapViewModel = mapViewModel
            )
        }
    }
}