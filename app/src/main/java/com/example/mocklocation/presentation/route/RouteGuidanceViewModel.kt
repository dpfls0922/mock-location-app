package com.example.mocklocation.presentation.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mocklocation.domain.entity.RouteGuidanceData
import com.example.mocklocation.domain.usecase.GetRouteGuidanceUseCase
import com.example.mocklocation.presentation.map.component.RoutePoint
import com.example.mocklocation.presentation.map.component.RoutePointType
import com.example.mocklocation.presentation.search.SearchResultUiModel
import com.example.mocklocation.presentation.search.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class RouteGuidanceState {
    data object Idle : RouteGuidanceState()
    data object Loading : RouteGuidanceState()
    data class Success(val routeData: RouteGuidanceData, val wayPoints: List<RoutePoint>) : RouteGuidanceState()
    data class Error(val message: String) : RouteGuidanceState()
}

@HiltViewModel
class RouteGuidanceViewModel @Inject constructor(
    private val getRouteGuidanceUseCase: GetRouteGuidanceUseCase
) : ViewModel() {

    private val _routePoints = MutableStateFlow<List<RoutePoint>>(emptyList())
    val routePoints: StateFlow<List<RoutePoint>> = _routePoints.asStateFlow()

    private val _closeRouteDialogEvent = MutableStateFlow(false)
    val closeRouteDialogEvent: StateFlow<Boolean> = _closeRouteDialogEvent.asStateFlow()

    private val _uiState = MutableStateFlow<RouteGuidanceState>(RouteGuidanceState.Idle)
    val uiState: StateFlow<RouteGuidanceState> = _uiState.asStateFlow()

    /**
     * 경로 포인트 추가
     */
    fun addRoutePoint(routeType: RoutePointType, location: SearchResultUiModel) {
        val currentPoints = _routePoints.value.toMutableList()

        when (routeType) {
            RoutePointType.START -> {
                currentPoints.removeAll { it.type == RoutePointType.START }
                currentPoints.add(0, RoutePoint(routeType, location))
            }
            RoutePointType.END -> {
                currentPoints.removeAll { it.type == RoutePointType.END }
                currentPoints.add(RoutePoint(routeType, location))
            }
            RoutePointType.WAYPOINT -> {
                val waypointCount = currentPoints.count { it.type == RoutePointType.WAYPOINT }
                if (waypointCount < 2) {
                    val insertIndex = currentPoints.indexOfLast { it.type == RoutePointType.START } + 1
                    currentPoints.add(insertIndex, RoutePoint(routeType, location))
                } else {
                    Timber.w("경유지는 최대 2개까지만 추가할 수 있습니다.")
                    return
                }
            }
        }
        _routePoints.value = currentPoints
        _closeRouteDialogEvent.value = true
        Timber.d("[ROUTE] 경로 포인트 추가: ${routeType.label} - ${location.name}")
    }

    /**
     * 경로 포인트 제거
     */
    fun removeRoutePoint(routePoint: RoutePoint) {
        val currentPoints = _routePoints.value.toMutableList()
        currentPoints.remove(routePoint)
        _routePoints.value = currentPoints
        Timber.d("[ROUTE] 경로 포인트 제거: ${routePoint.type.label} - ${routePoint.location.name}")
    }

    /**
     * 경로 안내
     */
    fun startGuidance() {
        val startPoint = _routePoints.value.find { it.type == RoutePointType.START }
        val endPoint = _routePoints.value.find { it.type == RoutePointType.END }
        val wayPoints = _routePoints.value.filter { it.type == RoutePointType.WAYPOINT }

        if (startPoint == null || endPoint == null) {
            _uiState.value = RouteGuidanceState.Error("출발지와 도착지를 모두 설정해주세요.")
            return
        }

        viewModelScope.launch {
            _uiState.value = RouteGuidanceState.Loading

            val startPoi = startPoint.location.toDomain()
            val endPoi = endPoint.location.toDomain()
            val wayPointPois = wayPoints.map { it.location.toDomain() }

            getRouteGuidanceUseCase(startPoi, endPoi, wayPointPois)
                .onSuccess { routeData ->
                    if (routeData != null) {
                        _uiState.value = RouteGuidanceState.Success(routeData, wayPoints)
                        Timber.d("[ROUTE_GUIDANCE] 경로 안내 성공")
                    } else {
                        _uiState.value = RouteGuidanceState.Error("경로를 찾을 수 없습니다.")
                    }
                }
                .onFailure { exception ->
                    _uiState.value = RouteGuidanceState.Error("경로 검색 실패: ${exception.message}")
                    Timber.e(exception, "[ROUTE_GUIDANCE] 경로 안내 실패")
                }
        }
    }

    fun resetCloseDialogEvent() {
        _closeRouteDialogEvent.value = false
    }
}