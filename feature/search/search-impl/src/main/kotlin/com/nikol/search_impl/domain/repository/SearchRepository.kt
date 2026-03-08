package com.nikol.search_impl.domain.repository

import arrow.core.Either
import com.nikol.domainutil.BaseResponseError
import com.nikol.search_impl.domain.models.PagedResult
import com.nikol.search_impl.domain.models.SearchParams
import com.nikol.search_impl.domain.models.SearchResultDomain

typealias SearchResult = Either<BaseResponseError, PagedResult<SearchResultDomain>>

interface SearchRepository {
    suspend fun search(searchParams: SearchParams) : SearchResult
}