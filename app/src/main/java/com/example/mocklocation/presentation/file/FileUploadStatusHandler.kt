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

    LaunchedEffect(uploadState) {
        when (uploadState) {
            is UploadUiState.Loading -> {
                Toast.makeText(context, "파일 업로드 성공", Toast.LENGTH_SHORT).show()
            }
            is UploadUiState.Success -> {
                Toast.makeText(context, "주행 로그 파싱 성공", Toast.LENGTH_SHORT).show()
                mockViewModel.updateGpsPoints((uploadState as UploadUiState.Success).gpsPoints)
                uploadViewModel.resetState()
            }
            is UploadUiState.Error -> {
                Toast.makeText(context, "파일 업로드 실패", Toast.LENGTH_SHORT).show()
                uploadViewModel.resetState()
            }
            else -> Unit
        }
    }
}