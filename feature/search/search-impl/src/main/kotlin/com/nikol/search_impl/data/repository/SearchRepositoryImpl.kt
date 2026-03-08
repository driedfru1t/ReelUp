package com.nikol.search_impl.data.repository

import com.nikol.search_impl.data.remote.ext.toDomain
import com.nikol.search_impl.data.remote.service.SearchService
import com.nikol.search_impl.domain.models.SearchParams
import com.nikol.search_impl.domain.repository.SearchRepository
import com.nikol.search_impl.domain.repository.SearchResult

class SearchRepositoryImpl(
    private val searchService: SearchService
) : SearchRepository {
    override suspend fun search(searchParams: SearchParams): SearchResult {
        return searchService.multiSearch(searchParams).map { it.toDomain() }
    }
}