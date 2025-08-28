package com.example.mocklocation.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.mocklocation.presentation.map.component.ExpandableGpsMockCard
import com.example.mocklocation.presentation.map.component.FloatingButtons
import com.example.mocklocation.presentation.ui.theme.MapTheme

@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    permissionGranted: Boolean,
    onRequestPermission: () -> Unit,
    onClickMyLocation: () -> Unit,
    onClickSelectCsvFile: () -> Unit,
    onClickStartMockSend: () -> Unit,
    onClickStopMockSend: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

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
                ExpandableGpsMockCard(onClickStartMockSend, onClickStopMockSend)
            }
        }
    }
}