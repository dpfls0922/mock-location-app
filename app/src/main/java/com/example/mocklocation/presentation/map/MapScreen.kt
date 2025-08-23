package com.example.mocklocation.presentation.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    permissionGranted: Boolean,
    onRequestPermission: () -> Unit,
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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onClickSelectCsvFile) {
                Icon(Icons.Default.Upload, contentDescription = "CSV 파일 업로드")
            }
        }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding).fillMaxSize()) {
            AndroidView(
                factory = { context -> mapViewModel.createMapView(context) },
                modifier = Modifier.fillMaxSize()
            )
            MapLifecycleHandler(lifecycleOwner, mapViewModel)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onClickStartMockSend) { Text("Mock 위치 전송 시작") }
                Button(onClick = onClickStopMockSend) { Text("Mock 위치 전송 중지") }
            }
        }
    }
}