package com.example.mocklocation.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mocklocation.domain.usecase.GetAutoCompleteSuggestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getAutoCompleteUseCase: GetAutoCompleteSuggestionsUseCase
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _autoSuggestions = MutableStateFlow<List<AutoCompleteUiModel>>(emptyList())
    val autoSuggestions: StateFlow<List<AutoCompleteUiModel>> = _autoSuggestions

    private val _searchResults = MutableStateFlow<List<SearchResultUiModel>>(emptyList())
    val searchResults: StateFlow<List<SearchResultUiModel>> = _searchResults

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
            _searchResults.value = emptyList()
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
}
