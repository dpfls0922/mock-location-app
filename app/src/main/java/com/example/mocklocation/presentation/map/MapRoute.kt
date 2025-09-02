package com.example.mocklocation.presentation.map

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mocklocation.presentation.draw.DrawStatusHandler
import com.example.mocklocation.presentation.draw.DrawViewModel
import com.example.mocklocation.presentation.file.FileUploadStatusHandler
import com.example.mocklocation.presentation.file.FileUploadViewModel
import com.example.mocklocation.presentation.map.MapRouteConstants.DEFAULT_FILE_NAME
import com.example.mocklocation.presentation.map.MapRouteConstants.MIME_TYPE_TEXT
import com.example.mocklocation.presentation.mock.MockLocationViewModel
import com.example.mocklocation.presentation.route.RouteGuidanceStatusHandler
import com.example.mocklocation.presentation.route.RouteGuidanceViewModel
import com.example.mocklocation.presentation.search.SearchViewModel
import com.example.mocklocation.presentation.ui.common.rememberFileSelectorLauncher
import com.example.mocklocation.presentation.ui.common.rememberPermissionLauncher

@Composable
fun MapRoute(
    mapViewModel: MapViewModel = hiltViewModel(),
    mockViewModel: MockLocationViewModel = hiltViewModel(),
    drawViewModel: DrawViewModel = hiltViewModel(),
    uploadViewModel: FileUploadViewModel = hiltViewModel(),
    routeGuidanceViewModel: RouteGuidanceViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val permissionGranted by mapViewModel.locationPermissionGranted.collectAsState()
    val permissionLauncher = rememberPermissionLauncher(mapViewModel::onPermissionResult)

    val coroutineScope = rememberCoroutineScope()
    val fileSelectorLauncher = rememberFileSelectorLauncher(coroutineScope = coroutineScope) { uri ->
        uploadViewModel.uploadFile(uri, uri.lastPathSegment ?: DEFAULT_FILE_NAME)
    }

    FileUploadStatusHandler()
    RouteGuidanceStatusHandler()
    DrawStatusHandler()

    MapScreen(
        mapViewModel = mapViewModel,
        mockViewModel = mockViewModel,
        searchViewModel = searchViewModel,
        drawViewModel = drawViewModel,
        routeGuidanceViewModel = routeGuidanceViewModel,
        permissionGranted = permissionGranted,
        onRequestPermission = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
        onClickMyLocation = { },
        onClickSelectCsvFile = { fileSelectorLauncher.launch(MIME_TYPE_TEXT) },
        onClickStartMockSend = { mockViewModel.startOrResumeMocking() },
        onClickStopMockSend = { mockViewModel.stopMocking() }
    )
}

private object MapRouteConstants {
    const val DEFAULT_FILE_NAME = "unknown.csv"
    const val MIME_TYPE_TEXT = "*/*"
}