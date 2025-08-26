package com.example.mocklocation.domain.usecase

import com.example.mocklocation.domain.entity.AutoComplete
import com.example.mocklocation.domain.repository.TmapRepository
import javax.inject.Inject

class GetAutoCompleteSuggestionsUseCase @Inject constructor(
    private val repository: TmapRepository
) {
    suspend operator fun invoke(keyword: String): Result<List<AutoComplete>> = runCatching {
        repository.getAutoCompleteSuggestions(keyword)
    }
}