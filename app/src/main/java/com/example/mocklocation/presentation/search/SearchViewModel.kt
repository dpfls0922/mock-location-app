package com.example.mocklocation.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.domain.entity.RouteGuidance
import com.example.mocklocation.domain.usecase.GetAutoCompleteSuggestionsUseCase
import com.example.mocklocation.domain.usecase.GetCurrentLocationUseCase
import com.example.mocklocation.domain.usecase.GetRouteGuidanceUseCase
import com.example.mocklocation.domain.usecase.SearchPoiUseCase
import com.example.mocklocation.presentation.map.component.RoutePoint
import com.example.mocklocation.presentation.map.component.RoutePointType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val results: List<SearchResultUiModel>) : SearchUiState()
    data class SuccessWithGuidance(
        val guidanceData: RouteGuidance
    ) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getAutoCompleteUseCase: GetAutoCompleteSuggestionsUseCase,
    private val searchPoisUseCase: SearchPoiUseCase,
    private val getRouteGuidanceUseCase: GetRouteGuidanceUseCase
) : ViewModel() {
    private val _currentLocation = MutableStateFlow(
        CurrentLocation(0, 0.0, 0.0, null, null)
    )
    private val currentLocation: StateFlow<CurrentLocation> = _currentLocation

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _autoSuggestions = MutableStateFlow<List<AutoCompleteUiModel>>(emptyList())
    val autoSuggestions: StateFlow<List<AutoCompleteUiModel>> = _autoSuggestions

    private val _routePoints = MutableStateFlow<List<RoutePoint>>(emptyList())
    val routePoints: StateFlow<List<RoutePoint>> = _routePoints

    private val _showRouteDialog = MutableStateFlow(false)
    val showRouteDialog: StateFlow<Boolean> = _showRouteDialog

    private val _selectedLocationForRoute = MutableStateFlow<SearchResultUiModel?>(null)
    val selectedLocationForRoute: StateFlow<SearchResultUiModel?> = _selectedLocationForRoute

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState

    init {
        observeCurrentLocation()
    }

    /**
     * 위치 정보 Flow 관찰
     */
    private fun observeCurrentLocation() {
        viewModelScope.launch {
            getCurrentLocationUseCase().collectLatest { location ->
                Timber.d("현재 위치 업데이트: $location")
                _currentLocation.value = location
            }
        }
    }

    /**
     * 검색창 입력 시 호출
     */
    fun onSearchQueryChanged(query: String) {
        Timber.d("검색창 입력: $query")
        _searchQuery.value = query
        if (query.isNotBlank()) {
            fetchAutoSuggestions(query)
        } else {
            _autoSuggestions.value = emptyList()
        }
    }

    /**
     * 자동완성 API 호출
     */
    private fun fetchAutoSuggestions(keyword: String) {
        viewModelScope.launch {
            getAutoCompleteUseCase(keyword)
                .onSuccess { list ->
                    val uiModels = list.map { it.toUiModel() }
                    _autoSuggestions.value = uiModels
                }.onFailure {
                    _autoSuggestions.value = emptyList()
                }
        }
    }

    /**
     * 자동완성 아이템 클릭
     */
    fun onAutoCompleteItemClick(autoComplete: AutoCompleteUiModel) {
        _searchQuery.value = autoComplete.keyword
        requestSearch()
    }

    /**
     * 검색 요청
     */
    private fun requestSearch() {
        val keyword = searchQuery.value
        val location = currentLocation.value
        searchPoi(keyword, location)
    }

    /**
     * POI 검색
     */
    private fun searchPoi(keyword: String, currentLocation: CurrentLocation) = viewModelScope.launch {
        if (keyword.isBlank()) {
            Timber.w("검색어가 비어있음")
            return@launch
        }
        if (currentLocation.latitude == 0.0 && currentLocation.longitude == 0.0) {
            Timber.w("위치 정보가 아직 준비되지 않음")
            return@launch
        }

        _uiState.value = SearchUiState.Loading
        searchPoisUseCase("A", keyword, currentLocation)
            .onSuccess { pois ->
                val results = pois.map { it.toUiModel() }
                _uiState.value = SearchUiState.Success(results)
                if (results.isNotEmpty()) {
                    showRouteSelectionDialog(results.first())
                }
            }
            .onFailure { e ->
                Timber.e(e, "POI 검색 실패")
                _uiState.value = SearchUiState.Error("검색에 실패했습니다.")
            }
    }

    /**
     * 경로 선택 다이얼로그 표시
     */
    private fun showRouteSelectionDialog(location: SearchResultUiModel) {
        _selectedLocationForRoute.value = location
        _showRouteDialog.value = true
    }

    /**
     * 경로 선택 다이얼로그 닫기
     */
    fun dismissRouteDialog() {
        _showRouteDialog.value = false
        _selectedLocationForRoute.value = null
    }

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
        dismissRouteDialog()
    }

    /**
     * 경로 포인트 제거
     */
    fun removeRoutePoint(routePoint: RoutePoint) {
        val currentPoints = _routePoints.value.toMutableList()
        currentPoints.remove(routePoint)
        _routePoints.value = currentPoints
    }

    fun startGuidance() {
        val currentPoints = _routePoints.value

        val startPoint = currentPoints.find { it.type == RoutePointType.START }?.location
        val endPoint = currentPoints.find { it.type == RoutePointType.END }?.location
        val wayPoints = currentPoints.filter { it.type == RoutePointType.WAYPOINT }.map { it.location }

        if (startPoint == null || endPoint == null) {
            Timber.w("시작점 또는 도착점이 설정되지 않음")
            return
        }

        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading

            getRouteGuidanceUseCase(
                start = startPoint.toDomain(),
                dest = endPoint.toDomain(),
                wayPoints = wayPoints.map { it.toDomain() }
            ).onSuccess { guidance ->
                _uiState.value = SearchUiState.SuccessWithGuidance(
                    guidanceData = guidance
                )
            }.onFailure { e ->
                Timber.e(e, "경로 안내 실패")
                _uiState.value = SearchUiState.Error("경로 안내에 실패했습니다.")
            }
        }
    }
}
