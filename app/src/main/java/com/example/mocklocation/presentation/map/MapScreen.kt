package com.example.mocklocation.presentation.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    permissionGranted: Boolean,
    onRequestPermission: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(permissionGranted) {
        if (!permissionGranted) {
            onRequestPermission()
        } else {
            mapViewModel.checkAndFetchInitialLocation()
        }
    }

    MapLifecycleHandler(lifecycleOwner, mapViewModel)

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            mapViewModel.createMapView(ctx)
        }
    )
}