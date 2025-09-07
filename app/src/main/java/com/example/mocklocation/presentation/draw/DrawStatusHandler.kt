package com.example.mocklocation.presentation.draw

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mocklocation.presentation.mock.MockLocationViewModel

@Composable
fun DrawStatusHandler(
    drawViewModel: DrawViewModel = hiltViewModel(),
    mockViewModel: MockLocationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val drawState by drawViewModel.uiState.collectAsState(initial = null)
    val uiEvents = drawViewModel.uiEvent

    LaunchedEffect(drawState) {
        when (drawState) {
            is DrawUiState.Success -> {
                mockViewModel.updateGpsPoints((drawState as DrawUiState.Success).gpsPoints)
                drawViewModel.notifySuccess()
            }
            is DrawUiState.Error -> drawViewModel.notifyError()
            is DrawUiState.Drawing -> drawViewModel.notifyLoading()
            is DrawUiState.Idle -> mockViewModel.updateGpsPoints(emptyList())
            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        uiEvents.collect { event ->
            when(event){
                is UiEvent.ShowToast -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}