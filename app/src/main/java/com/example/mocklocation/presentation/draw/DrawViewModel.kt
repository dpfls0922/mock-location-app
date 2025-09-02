package com.example.mocklocation.presentation.draw

import androidx.lifecycle.ViewModel
import com.example.mocklocation.data.service.MapServiceImpl
import com.example.mocklocation.domain.service.MapService
import com.skt.tmap.vsm.data.VSMMapPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DrawViewModel @Inject constructor(
    private val mapService: MapService
): ViewModel() {
    private val _isDrawingMode = MutableStateFlow(false)
    val isDrawingMode: StateFlow<Boolean> = _isDrawingMode

    fun toggleDrawingMode() {
        val newMode = !_isDrawingMode.value
        _isDrawingMode.value = newMode
        if (!newMode)  mapService.removeDrawingMarker()
        (mapService as MapServiceImpl).enableDrawingMode(newMode)
    }

    fun stopDrawingMode() {
        _isDrawingMode.value = false
        mapService.removeDrawingMarker()
        (mapService as MapServiceImpl).enableDrawingMode(false)
    }

    fun drawRouteLine(vsmPoints: List<VSMMapPoint>) {
        mapService.drawRouteLine(vsmPoints)
    }
}