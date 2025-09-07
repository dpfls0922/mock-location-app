package com.example.mocklocation.presentation.file

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mocklocation.presentation.mock.MockLocationViewModel

@Composable
fun FileUploadStatusHandler(
    uploadViewModel: FileUploadViewModel = hiltViewModel(),
    mockViewModel: MockLocationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uploadState by uploadViewModel.uiState.collectAsState(initial = null)
    val uiEvents = uploadViewModel.uiEvent

    LaunchedEffect(uploadState) {
        when (uploadState) {
            is UploadUiState.Success -> {
                mockViewModel.updateGpsPoints((uploadState as UploadUiState.Success).gpsPoints)
                uploadViewModel.resetState()
            }
            is UploadUiState.Error -> {
                uploadViewModel.resetState()
            }
            else -> Unit
        }
    }

    LaunchedEffect(Unit){
        uiEvents.collect{ event ->
            when(event){
                is UiEvent.ShowToast -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}