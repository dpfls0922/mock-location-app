package com.example.mocklocation.presentation.draw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mocklocation.data.dto.toGpsPoint
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.service.MapService
import com.skt.tmap.vsm.data.VSMMapPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DrawUiState {
    data object Idle : DrawUiState()
    data object Drawing : DrawUiState()
    data class Success(val gpsPoints: List<GpsPoint>) : DrawUiState()
    data class Error(val message: String) : DrawUiState()
}

@HiltViewModel
class DrawViewModel @Inject constructor(
    private val mapService: MapService
): ViewModel() {
    private val _isDrawingMode = MutableStateFlow(false)
    val isDrawingMode: StateFlow<Boolean> = _isDrawingMode

    private val _uiState = MutableStateFlow<DrawUiState>(DrawUiState.Idle)
    val uiState: StateFlow<DrawUiState> = _uiState.asStateFlow()

    init {
        mapService.setDrawingPointListener { finishedPoints ->
            viewModelScope.launch {
                val gpsPoints = finishedPoints.map { it.toGpsPoint() }
                _uiState.value = DrawUiState.Success(gpsPoints)
            }
        }
    }

    fun toggleDrawingMode() {
        val newMode = !_isDrawingMode.value
        _isDrawingMode.value = newMode
        mapService.enableDrawingMode(newMode)
    }

    fun cancelDrawing() {
        _uiState.value = DrawUiState.Idle
        _isDrawingMode.value = false
        mapService.removeDrawingMarker()
        mapService.clearDrawingPoints()
        mapService.enableDrawingMode(false)
    }

    fun drawRouteLine(vsmPoints: List<VSMMapPoint>) {
        mapService.drawRouteLine(vsmPoints)
    }
}