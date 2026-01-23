package com.nikol.search_impl.presentation.viewModel

import com.nikol.detail_api.ContentType
import com.nikol.direct_core.DirectEffect
import com.nikol.search_impl.presentation.mvi.intent.SearchIntent
import com.nikol.search_impl.presentation.mvi.state.Search
import com.nikol.viewmodel.DirectRouter
import com.nikol.viewmodel.DirectRouterViewModel

interface SearchRouter : DirectRouter {
    fun toDetail(contentType: ContentType, id: Int)
    fun toFilter()
}

internal class SearchScreenViewModel :
    DirectRouterViewModel<SearchIntent, Search, DirectEffect, SearchRouter>() {
    override fun createInitialState(): Search {
        TODO("Not yet implemented")
    }

    override fun handleIntents() {
        TODO("Not yet implemented")
    }
}