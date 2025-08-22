package com.example.mocklocation.presentation.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MapRoute(
    viewModel: MapViewModel = hiltViewModel()
) {
    val permissionGranted by viewModel.locationPermissionGranted.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = viewModel::onPermissionResult
    )

    MapScreen(
        mapViewModel = viewModel,
        permissionGranted = permissionGranted,
        onRequestPermission = {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    )
}