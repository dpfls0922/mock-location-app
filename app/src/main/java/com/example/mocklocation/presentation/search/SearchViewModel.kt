package com.example.mocklocation.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.domain.usecase.GetAutoCompleteSuggestionsUseCase
import com.example.mocklocation.domain.usecase.GetCurrentLocationUseCase
import com.example.mocklocation.domain.usecase.SearchPoiUseCase
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
) : ViewModel() {
    private val _currentLocation = MutableStateFlow(
        CurrentLocation(0, 0.0, 0.0, null, null)
    )
    val currentLocation: StateFlow<CurrentLocation> = _currentLocation

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _autoSuggestions = MutableStateFlow<List<AutoCompleteUiModel>>(emptyList())
    val autoSuggestions: StateFlow<List<AutoCompleteUiModel>> = _autoSuggestions

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
            }
            .onFailure { e ->
                Timber.e(e, "POI 검색 실패")
                _uiState.value = SearchUiState.Error("검색에 실패했습니다.")
            }
    }
}
