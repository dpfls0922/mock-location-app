package com.example.mocklocation.presentation.map

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mocklocation.presentation.draw.DrawStatusHandler
import com.example.mocklocation.presentation.draw.DrawViewModel
import com.example.mocklocation.presentation.file.FileUploadStatusHandler
import com.example.mocklocation.presentation.file.FileUploadViewModel
import com.example.mocklocation.presentation.map.MapRouteConstants.MIME_TYPE_TEXT
import com.example.mocklocation.presentation.mock.MockLocationViewModel
import com.example.mocklocation.presentation.route.RouteGuidanceStatusHandler
import com.example.mocklocation.presentation.route.RouteGuidanceViewModel
import com.example.mocklocation.presentation.search.SearchStatusHandler
import com.example.mocklocation.presentation.search.SearchViewModel
import com.example.mocklocation.presentation.ui.common.rememberFileSelectorLauncher
import com.example.mocklocation.presentation.ui.common.rememberPermissionLauncher
import com.example.mocklocation.presentation.ui.theme.MapTheme

@Composable
fun MapRoute(
    mapViewModel: MapViewModel = hiltViewModel(),
    mockViewModel: MockLocationViewModel = hiltViewModel(),
    drawViewModel: DrawViewModel = hiltViewModel(),
    uploadViewModel: FileUploadViewModel = hiltViewModel(),
    routeGuidanceViewModel: RouteGuidanceViewModel = hiltViewModel(),
    fileUploadViewModel: FileUploadViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val permissionGranted by mapViewModel.locationPermissionGranted.collectAsState()
    val permissionLauncher = rememberPermissionLauncher(mapViewModel::onPermissionResult)

    val coroutineScope = rememberCoroutineScope()
    val fileSelectorLauncher = rememberFileSelectorLauncher(coroutineScope = coroutineScope) { uri ->
        uploadViewModel.uploadFile(uri)
    }

    MapTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MapScreen(
                mapViewModel = mapViewModel,
                mockViewModel = mockViewModel,
                searchViewModel = searchViewModel,
                drawViewModel = drawViewModel,
                routeGuidanceViewModel = routeGuidanceViewModel,
                fileUploadViewModel = fileUploadViewModel,
                permissionGranted = permissionGranted,
                onRequestPermission = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                onClickSelectCsvFile = { fileSelectorLauncher.launch(MIME_TYPE_TEXT) }
            )

            FileUploadStatusHandler()
            RouteGuidanceStatusHandler()
            DrawStatusHandler()
            SearchStatusHandler()
        }
    }
}

private object MapRouteConstants {
    const val MIME_TYPE_TEXT = "*/*"
}