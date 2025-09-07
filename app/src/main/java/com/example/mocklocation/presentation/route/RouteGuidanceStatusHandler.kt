package com.example.mocklocation.presentation.route

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mocklocation.presentation.mock.MockLocationViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RouteGuidanceStatusHandler(
    routeGuidanceViewModel: RouteGuidanceViewModel = hiltViewModel(),
    mockViewModel: MockLocationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by routeGuidanceViewModel.uiState.collectAsState()
    val uiEvent = routeGuidanceViewModel.uiEvent

    LaunchedEffect(uiState) {
        when (uiState) {
            is RouteGuidanceState.Success -> {
                val routeData = (uiState as RouteGuidanceState.Success).routeData
                routeData.gpsPoints.let { mockViewModel.updateGpsPoints(it) }
            }
            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
            routeGuidanceViewModel.resetCloseDialogEvent()
        }
    }
}