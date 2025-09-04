package com.example.mocklocation.presentation.file

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mocklocation.domain.entity.CsvFile
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.usecase.UploadCsvFileUseCase
import com.skt.tmap.vsm.data.VSMMapPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UploadUiState {
    data object Idle : UploadUiState()
    data object Loading : UploadUiState()
    data class Success(val file: CsvFile, val gpsPoints: List<GpsPoint>, val vsmPoints: List<VSMMapPoint>) : UploadUiState()
    data class Error(val message: String) : UploadUiState()
}

@HiltViewModel
class FileUploadViewModel @Inject constructor(
    private val uploadCsvFileUseCase: UploadCsvFileUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<UploadUiState>(UploadUiState.Idle)
    val uiState: StateFlow<UploadUiState> = _uiState

    fun uploadFile(uri: Uri, name: String) {
        _uiState.value = UploadUiState.Loading
        viewModelScope.launch {
            try {
                val uploadedFile = uploadCsvFileUseCase.execute(uri, name)
                val gpsPoints = uploadedFile.gpsPoints
                val vsmPoints = uploadedFile.gpsPoints.map { gps ->
                    VSMMapPoint(gps.longitude, gps.latitude)
                }
                _uiState.value = UploadUiState.Success(uploadedFile, gpsPoints, vsmPoints)
            } catch (e: Exception) {
                _uiState.value = UploadUiState.Error(e.localizedMessage ?: "업로드 실패")
            }
        }
    }

    fun resetState() {
        _uiState.value = UploadUiState.Idle
    }
}
