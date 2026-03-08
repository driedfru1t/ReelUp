package com.nikol.search_impl.presentation.viewModel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nikol.detail_api.ContentType
import com.nikol.search_impl.domain.models.PagedResult
import com.nikol.search_impl.domain.models.SearchResultDomain
import com.nikol.search_impl.domain.paging.SearchPagingSource
import com.nikol.search_impl.domain.ues_case.SearchUseCase
import com.nikol.search_impl.presentation.mvi.intent.SearchIntent
import com.nikol.search_impl.presentation.mvi.state.Search
import com.nikol.search_impl.presentation.mvi.state.SearchState
import com.nikol.viewmodel.DirectRouter
import com.nikol.viewmodel.DirectRouterViewModel
import direct.direct_core.DirectEffect
import direct.direct_core.debounce
import direct.direct_core.distinct
import direct.direct_core.filter
import direct.direct_core.on
import direct.direct_core.onLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

interface SearchRouter : DirectRouter {
    fun toDetail(contentType: ContentType, id: Int)
    fun toFilter()
}

internal class SearchScreenViewModel(
    private val searchUseCase: SearchUseCase
) :
    DirectRouterViewModel<SearchIntent, Search, DirectEffect, SearchRouter>() {
    override fun createInitialState() =
        Search(inputText = "", searchQuery = "")

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedContent: Flow<PagingData<SearchResultDomain>> = state
        .map { it.searchQuery }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(PagingData.empty())
            } else {
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false,
                        initialLoadSize = 20
                    ),
                    pagingSourceFactory = { SearchPagingSource(query, searchUseCase) }
                ).flow
            }
        }
        .cachedIn(viewModelScope)

    override fun handleIntents() = intents {
        setup<SearchIntent.Search> {
            debounce(0.7.seconds)
            distinct()
            latest { intent ->
                val text = intent.text.trim()
                if (text.length >= 2) {
                    setState {
                        if (inputText.isNotBlank()) {
                            copy(searchQuery = text)
                        } else {
                            this
                        }
                    }
                }
            }
        }
        on<SearchIntent.Search> { intent ->
            setState { copy(inputText = intent.text) }
            if (intent.text.isBlank()) {
                setState { copy(searchQuery = "") }
            }
        }

        on<SearchIntent.ClearText> {
            setState { copy(inputText = "", searchQuery = "") }
        }

        onNavigate<SearchIntent.GoToDetail> { intent ->
            toDetail(contentType = intent.contentType, id = intent.id)
        }
    }
}