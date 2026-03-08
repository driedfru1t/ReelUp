package com.nikol.home_impl.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.nikol.home_impl.domain.parameters.ContentParameter
import com.nikol.home_impl.domain.parameters.Period
import com.nikol.home_impl.domain.useCase.GetNowPlayingMoviesUseCase
import com.nikol.home_impl.domain.useCase.GetTrendMoviesUseCase
import com.nikol.home_impl.presentation.mvi.intent.ListIntent
import com.nikol.home_impl.presentation.mvi.intent.MovieIntent
import com.nikol.home_impl.presentation.mvi.state.MovieState
import com.nikol.home_impl.presentation.mvi.state.TrendContent
import com.nikol.home_impl.presentation.mvi.store.ContentListFeature
import com.nikol.ui.model.MediaType
import com.nikol.ui.state.ListState
import com.nikol.viewmodel.DirectRouter
import com.nikol.viewmodel.DirectRouterViewModel
import direct.direct_core.DirectEffect
import direct.direct_core.awaitState
import direct.direct_core.filter
import direct.direct_core.onLatest
import direct.direct_core.onSingle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

fun interface MovieRouter : DirectRouter {
    fun toDetail(id: String)
}

class MovieViewModel(
    private val getTrendMoviesUseCase: GetTrendMoviesUseCase,
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase
) : DirectRouterViewModel<MovieIntent, MovieState, DirectEffect, MovieRouter>() {
    override fun createInitialState() = MovieState(
        isLoading = false,
        trend = TrendContent(
            state = ListState.Loading,
            period = Period.Day
        ),
        nowPlaying = ListState.Loading
    )


    private val trendStore =
        ContentListFeature(viewModelScope) { params -> getTrendMoviesUseCase(params) }

    private val nowPlayingStore =
        ContentListFeature(viewModelScope) { params -> getNowPlayingMoviesUseCase(params) }

    override fun handleIntents() = intents {

        feature(
            store = trendStore,
            state = { childState ->
                setState { copy(trend = trend.copy(state = childState)) }
            },
            effects = { },
            intents = { parent ->
                when (parent) {
                    is MovieIntent.LoadAllData -> ListIntent.Load(
                        ContentParameter(MediaType.MOVIE, state.value.trend.period)
                    )

                    is MovieIntent.ChangePeriodForTrend -> ListIntent.Load(
                        ContentParameter(MediaType.MOVIE, parent.period)
                    )

                    is MovieIntent.RefreshData -> ListIntent.Load(
                        ContentParameter(MediaType.MOVIE, state.value.trend.period)
                    )

                    else -> null
                }
            }
        )

        feature(
            store = nowPlayingStore,
            state = { childState -> setState { copy(nowPlaying = childState) } },
            effects = { },
            intents = { parent ->
                when (parent) {
                    is MovieIntent.LoadAllData -> ListIntent.Load(
                        ContentParameter(MediaType.MOVIE, Period.Day)
                    )

                    is MovieIntent.RefreshData -> ListIntent.Load(
                        ContentParameter(MediaType.MOVIE, Period.Day)
                    )

                    else -> null
                }
            }
        )

        setup<MovieIntent.ChangePeriodForTrend> {
            filter { intent ->
                val currentBlock = state.value.trend
                intent.period != currentBlock.period || currentBlock.state is ListState.Error
            }
            latest { intent ->
                setState {
                    copy(
                        trend = trend.copy(
                            period = intent.period,
                            state = ListState.Loading
                        )
                    )
                }
            }
        }
        onNavigate<MovieIntent.NavigateToDetail> { intent ->
            toDetail(intent.id)
        }
        onLatest<MovieIntent.RefreshData> {
            setState { copy(isLoading = true) }
            val minDelayJob = viewModelScope.launch {
                delay(1.seconds)
            }
            store.awaitState(10_000L) {
                val trendReady = state.value.trend.state !is ListState.Loading
                val nowPlayingReady = state.value.nowPlaying !is ListState.Loading
                trendReady && nowPlayingReady
            }
            minDelayJob.join()
            setState { copy(isLoading = false) }
        }
    }

    init {
        setIntent(MovieIntent.LoadAllData)
    }
}