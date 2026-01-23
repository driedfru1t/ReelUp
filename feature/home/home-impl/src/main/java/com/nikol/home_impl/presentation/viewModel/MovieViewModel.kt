package com.nikol.home_impl.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.nikol.direct_core.DirectEffect
import com.nikol.direct_core.filter
import com.nikol.direct_core.onLatest
import com.nikol.direct_core.onSingle
import com.nikol.home_impl.domain.parameters.ContentParameter
import com.nikol.home_impl.domain.parameters.Period
import com.nikol.home_impl.domain.useCase.GetNowPlayingMoviesUseCase
import com.nikol.home_impl.domain.useCase.GetTrendMoviesUseCase
import com.nikol.home_impl.presentation.mvi.intent.MovieIntent
import com.nikol.home_impl.presentation.mvi.state.MovieState
import com.nikol.home_impl.presentation.mvi.state.TrendContent
import com.nikol.home_impl.presentation.ui.ext.toUi
import com.nikol.ui.model.MediaType
import com.nikol.ui.state.ListState
import com.nikol.viewmodel.DirectRouter
import com.nikol.viewmodel.DirectRouterViewModel
import com.nikol.viewmodel.asyncWithoutOld
import com.nikol.viewmodel.cancelAllJobs
import com.nikol.viewmodel.launchWithoutOld
import kotlinx.collections.immutable.toImmutableList
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

    init {
        setIntent(MovieIntent.LoadAllData)
    }

    override fun handleIntents() = intents {
        onSingle<MovieIntent.LoadAllData> {
            loadAll()
        }

        setup<MovieIntent.ChangePeriodForTrend> {
            filter { intent ->
                val currentBlock = uiState.value.trend
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
                loadTrending(
                    ContentParameter(
                        mediaType = MediaType.MOVIE,
                        period = intent.period
                    )
                )
            }
        }
        onSingle<MovieIntent.NavigateToDetail> { intent ->
            navigate { toDetail(intent.id) }
        }
        onLatest<MovieIntent.RefreshData> {
            setState { copy(isLoading = true) }
            val timer = viewModelScope.launch {
                delay(1.seconds)
            }
            val trendResult = asyncWithoutOld(LOAD_POPULAR) {
                getTrendMoviesUseCase(
                    ContentParameter(
                        mediaType = MediaType.MOVIE,
                        period = uiState.value.trend.period
                    )
                )
            }
            val nowPlayingResult = asyncWithoutOld(LOAD_NOW_PLAYING) {
                getNowPlayingMoviesUseCase(
                    ContentParameter(
                        mediaType = MediaType.MOVIE,
                        period = uiState.value.trend.period
                    )
                )
            }
            val movieTrend = trendResult.await()
            val movieNowPlaying = nowPlayingResult.await()
            timer.join()
            setState {
                copy(
                    isLoading = false,
                    trend = trend.copy(
                        state = movieTrend.fold(
                            ifLeft = { ListState.Error },
                            ifRight = { content ->
                                val uiList = content.map { it.toUi() }.toImmutableList()
                                ListState.Success(uiList)
                            }
                        )
                    ),
                    nowPlaying = movieNowPlaying.fold(
                        ifLeft = { ListState.Error },
                        ifRight = { content ->
                            val uiList = content.map { it.toUi() }.toImmutableList()
                            ListState.Success(uiList)
                        }
                    )
                )
            }
        }
    }

    private fun loadAll() {
        loadTrending(
            ContentParameter(
                mediaType = MediaType.MOVIE, period = Period.Day
            )
        )
        loadNowPlaying(
            ContentParameter(
                mediaType = MediaType.MOVIE, period = Period.Day
            )
        )
    }

    private fun loadTrending(contentParameter: ContentParameter) =
        launchWithoutOld(LOAD_POPULAR) {
            getTrendMoviesUseCase(contentParameter).fold(
                ifLeft = {
                    setState { copy(trend = trend.copy(state = ListState.Error)) }
                },
                ifRight = { content ->
                    val movie = content.map { it.toUi() }.toImmutableList()
                    setState { copy(trend = trend.copy(state = ListState.Success(movie))) }
                }
            )
        }

    private fun loadNowPlaying(contentParameter: ContentParameter) =
        launchWithoutOld(LOAD_NOW_PLAYING) {
            getNowPlayingMoviesUseCase(contentParameter).fold(
                ifLeft = {
                    setState { copy(nowPlaying = ListState.Loading) }
                },
                ifRight = { content ->
                    val movie = content.map { it.toUi() }.toImmutableList()
                    setState { copy(nowPlaying = ListState.Success(movie)) }
                }
            )
        }


    companion object {
        const val LOAD_POPULAR = "load_popular_job"
        const val LOAD_NOW_PLAYING = "load_now_playing_job"
    }

    override fun onCleared() {
        cancelAllJobs()
        super.onCleared()
    }
}