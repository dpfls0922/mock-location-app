package com.example.mocklocation.presentation.file

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mocklocation.domain.entity.CsvFile
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.usecase.UploadCsvFileUseCase
import com.skt.tmap.vsm.data.VSMMapPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UploadUiState {
    data object Idle : UploadUiState()
    data object Loading : UploadUiState()
    data class Success(val file: CsvFile, val gpsPoints: List<GpsPoint>, val vsmPoints: List<VSMMapPoint>) : UploadUiState()
    data class Error(val message: String) : UploadUiState()
}

sealed class UiEvent {
    data class ShowToast(val message: String): UiEvent()
}

@HiltViewModel
class FileUploadViewModel @Inject constructor(
    private val uploadCsvFileUseCase: UploadCsvFileUseCase
) : ViewModel() {
    private val _currentFileName = MutableStateFlow<String?>(null)
    val currentFileName: StateFlow<String?> = _currentFileName

    private val _uiState = MutableStateFlow<UploadUiState>(UploadUiState.Idle)
    val uiState: StateFlow<UploadUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun uploadFile(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = UploadUiState.Loading
            uploadCsvFileUseCase.execute(uri)
                .onSuccess { uploadedFile ->
                    val gpsPoints = uploadedFile.gpsPoints
                    val vsmPoints = uploadedFile.gpsPoints.map { gps ->
                        VSMMapPoint(gps.longitude, gps.latitude)
                    }
                    _uiState.value = UploadUiState.Success(uploadedFile, gpsPoints, vsmPoints)
                    _currentFileName.value = uploadedFile.name
                    _uiEvent.emit(UiEvent.ShowToast("주행 로그 파싱 완료"))
                }.onFailure { e ->
                    _uiState.value = UploadUiState.Error(e.localizedMessage ?: "업로드 실패")
                    _uiEvent.emit(UiEvent.ShowToast("파일 업로드 실패"))
                }
        }
    }

    fun resetState() {
        _uiState.value = UploadUiState.Idle
    }

    fun resetFileName() {
        _currentFileName.value = null
    }
}
