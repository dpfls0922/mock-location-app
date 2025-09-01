package com.example.mocklocation.presentation.route

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.mocklocation.presentation.mock.MockLocationViewModel

@Composable
fun RouteGuidanceStatusHandler(
    routeGuidanceViewModel: RouteGuidanceViewModel,
    mockLocationViewModel: MockLocationViewModel
) {
    val context = LocalContext.current
    val uiState by routeGuidanceViewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            is RouteGuidanceState.Success -> {
                val routeData = (uiState as RouteGuidanceState.Success).routeData
                routeData.gpsPoints.let { mockLocationViewModel.updateGpsPoints(it) }
                Toast.makeText(context, "tvas 파싱 완료 (${routeData.gpsPoints.size}개)", Toast.LENGTH_SHORT).show()
            }

            is RouteGuidanceState.Error -> {
                Toast.makeText(context, "tvas 경로 안내 실패", Toast.LENGTH_SHORT).show()
            }

            is RouteGuidanceState.Loading -> {
                Toast.makeText(context, "tvas 경로 검색 중", Toast.LENGTH_SHORT).show()
            }

            is RouteGuidanceState.Idle -> {
            }
        }
    }
}