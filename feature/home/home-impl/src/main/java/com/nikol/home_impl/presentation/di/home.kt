package com.nikol.home_impl.presentation.di

import com.nikol.home_impl.data.remote.service.movie.MovieService
import com.nikol.home_impl.data.remote.service.movie.MovieServiceImpl
import com.nikol.home_impl.data.remote.service.tv.TVService
import com.nikol.home_impl.data.remote.service.tv.TVServiceImpl
import com.nikol.home_impl.data.repository.MovieRepositoryImpl
import com.nikol.home_impl.data.repository.TvRepositoryImpl
import com.nikol.home_impl.domain.repository.MovieRepository
import com.nikol.home_impl.domain.repository.TvRepository
import com.nikol.home_impl.domain.useCase.GetNowPlayingMoviesUseCase
import com.nikol.home_impl.domain.useCase.GetTrendMoviesUseCase
import com.nikol.home_impl.domain.useCase.GetTrendTvUseCase
import com.nikol.home_impl.presentation.viewModel.HomePageViewModel
import com.nikol.home_impl.presentation.viewModel.MovieViewModel
import com.nikol.home_impl.presentation.viewModel.TVViewModel
import com.nikol.nav_impl.scopedNavigation.Component
import com.nikol.nav_impl.scopedNavigation.component
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal class MovieComponent : Component()
internal class TVComponent : Component()
internal class HomeComponent : Component()

private val movieModule = module {
    component { MovieComponent() }
    scope<MovieComponent> {
        viewModelOf(::MovieViewModel)
        scopedOf(::GetTrendMoviesUseCase)
        scopedOf(::GetNowPlayingMoviesUseCase)
        scopedOf(::MovieRepositoryImpl) bind MovieRepository::class
        scopedOf(::MovieServiceImpl) bind MovieService::class
    }
}

private val tvModule = module {
    component { TVComponent() }
    scope<TVComponent> {
        viewModelOf(::TVViewModel)
        scopedOf(::GetTrendTvUseCase)
        scopedOf(::TvRepositoryImpl) bind TvRepository::class
        scopedOf(::TVServiceImpl) bind TVService::class
    }
}

val homeModule = module {

    includes(tvModule, movieModule)
    component { HomeComponent() }
    scope<HomeComponent> {
        viewModelOf(::HomePageViewModel)
    }
}