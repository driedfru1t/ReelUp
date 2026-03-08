package com.nikol.search_impl.data.remote.service

import arrow.core.Either
import com.nikol.domainutil.BaseResponseError
import com.nikol.search_impl.data.remote.models.PagedResponseDTO
import com.nikol.search_impl.data.remote.models.SearchItemDTO
import com.nikol.search_impl.domain.models.SearchParams

typealias SearchResponse = Either<BaseResponseError, PagedResponseDTO<SearchItemDTO>>

interface SearchService {
    suspend fun multiSearch(searchParams: SearchParams): SearchResponse
    suspend fun tvSearch(): SearchResponse
    suspend fun movieSearch(): SearchResponse
    suspend fun personSearch(): SearchResponse
}