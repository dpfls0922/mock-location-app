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

    LaunchedEffect(drawState) {
        when (drawState) {
            is DrawUiState.Success -> {
                Toast.makeText(context, "드로잉 경로선 파싱 성공", Toast.LENGTH_SHORT).show()
                mockViewModel.updateGpsPoints((drawState as DrawUiState.Success).gpsPoints)
            }
            is DrawUiState.Error -> {
                Toast.makeText(context, "드로잉 경로선 파싱 실패", Toast.LENGTH_SHORT).show()
            }
            is DrawUiState.Drawing -> {
                Toast.makeText(context, "드로잉 경로선 파싱 중", Toast.LENGTH_SHORT).show()
            }
            is DrawUiState.Idle -> {
                mockViewModel.updateGpsPoints(emptyList())
            }
            else -> Unit
        }
    }
}