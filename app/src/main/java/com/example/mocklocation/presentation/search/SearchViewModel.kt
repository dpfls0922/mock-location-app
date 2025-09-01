package com.example.mocklocation.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.domain.usecase.GetAutoCompleteSuggestionsUseCase
import com.example.mocklocation.domain.usecase.GetCurrentLocationUseCase
import com.example.mocklocation.domain.usecase.GetRoadNameUseCase
import com.example.mocklocation.domain.usecase.SearchPoiUseCase
import com.skt.tmap.vsm.data.VSMMapPoint
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
    data class Error(val message: String) : SearchUiState()
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getAutoCompleteUseCase: GetAutoCompleteSuggestionsUseCase,
    private val searchPoisUseCase: SearchPoiUseCase,
    private val getRoadNameUseCase: GetRoadNameUseCase
) : ViewModel() {
    private val _currentLocation = MutableStateFlow(CurrentLocation(0, 0.0, 0.0, null, null))
    val currentLocation: StateFlow<CurrentLocation> = _currentLocation

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _autoSuggestions = MutableStateFlow<List<AutoCompleteUiModel>>(emptyList())
    val autoSuggestions: StateFlow<List<AutoCompleteUiModel>> = _autoSuggestions

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
        if (keyword.isBlank() || (currentLocation.latitude == 0.0 && currentLocation.longitude == 0.0)) return@launch

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
                _uiState.value = SearchUiState.Error("검색에 실패했습니다.")
            }
    }

    /**
     * 지도 터치 지점을 경로 선택 다이얼로그에 표시
     */
    fun showRouteSelectionDialogForMapPoint(location: VSMMapPoint) {
        viewModelScope.launch {
            getRoadNameUseCase(location.longitude, location.latitude)
                .onSuccess { touchLocation ->
                    val resultUiModel = location.toUiModel(touchLocation)
                    showRouteSelectionDialog(resultUiModel)
                }
                .onFailure {
                }
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
}