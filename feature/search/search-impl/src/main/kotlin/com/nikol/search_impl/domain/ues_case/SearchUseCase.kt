package com.nikol.search_impl.domain.ues_case

import com.nikol.domainutil.UseCase
import com.nikol.search_impl.domain.models.SearchParams
import com.nikol.search_impl.domain.repository.SearchRepository
import com.nikol.search_impl.domain.repository.SearchResult
import kotlinx.coroutines.Dispatchers

class SearchUseCase(
    private val searchRepository: SearchRepository
) : UseCase<SearchParams, SearchResult>(Dispatchers.IO) {
    override suspend fun run(params: SearchParams): SearchResult {
        return searchRepository.search(params)
    }
}