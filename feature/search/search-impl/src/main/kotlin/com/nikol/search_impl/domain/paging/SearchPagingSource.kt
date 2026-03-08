package com.nikol.search_impl.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nikol.search_impl.domain.models.SearchParams
import com.nikol.search_impl.domain.models.SearchResultDomain
import com.nikol.search_impl.domain.ues_case.SearchUseCase

class SearchPagingSource(
    private val query: String,
    private val searchUseCase: SearchUseCase
) : PagingSource<Int, SearchResultDomain>() {
    override fun getRefreshKey(state: PagingState<Int, SearchResultDomain>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchResultDomain> {
        val page = params.key ?: 1
        val result = searchUseCase(SearchParams(query = query, page = page))

        return result.fold(
            ifLeft = { error ->
                LoadResult.Error(PagingException(error))
            },
            ifRight = { domainModel ->
                val nextKey = if (page < domainModel.totalPages && domainModel.items.isNotEmpty()) {
                    page + 1
                } else {
                    null
                }

                LoadResult.Page(
                    data = domainModel.items,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = nextKey
                )
            }
        )
    }
}