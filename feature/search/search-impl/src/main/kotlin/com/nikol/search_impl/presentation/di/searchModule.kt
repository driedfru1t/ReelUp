package com.nikol.search_impl.presentation.di

import com.nikol.nav_impl.scopedNavigation.Component
import com.nikol.nav_impl.scopedNavigation.component
import com.nikol.search_impl.data.remote.service.SearchService
import com.nikol.search_impl.data.remote.service.SearchServiceImpl
import com.nikol.search_impl.data.repository.SearchRepositoryImpl
import com.nikol.search_impl.domain.repository.SearchRepository
import com.nikol.search_impl.domain.ues_case.SearchUseCase
import com.nikol.search_impl.presentation.viewModel.SearchScreenViewModel
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module


internal class SearchComponent : Component()

private val globalSearch = module {
    component { SearchComponent() }
    scope<SearchComponent> {
        viewModelOf(::SearchScreenViewModel)
        scopedOf(::SearchUseCase)
        scopedOf(::SearchRepositoryImpl) bind SearchRepository::class
        scopedOf(::SearchServiceImpl) bind SearchService::class
    }
}

val searchModule = module {
    includes(globalSearch)
}