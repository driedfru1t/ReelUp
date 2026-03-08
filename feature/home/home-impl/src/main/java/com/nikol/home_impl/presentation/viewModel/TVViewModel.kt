package com.nikol.home_impl.presentation.viewModel

import androidx.lifecycle.viewModelScope
import com.nikol.home_impl.domain.parameters.ContentParameter
import com.nikol.home_impl.domain.parameters.Period
import com.nikol.home_impl.domain.useCase.GetTrendTvUseCase
import com.nikol.home_impl.presentation.mvi.intent.ListIntent
import com.nikol.home_impl.presentation.mvi.intent.MovieIntent
import com.nikol.home_impl.presentation.mvi.intent.TVIntent
import com.nikol.home_impl.presentation.mvi.state.TVState
import com.nikol.home_impl.presentation.mvi.state.TrendContent
import com.nikol.home_impl.presentation.mvi.store.ContentListFeature
import com.nikol.home_impl.presentation.ui.ext.toUi
import com.nikol.ui.model.MediaType
import com.nikol.ui.state.ListState
import com.nikol.viewmodel.DirectRouter
import com.nikol.viewmodel.DirectRouterViewModel
import com.nikol.viewmodel.asyncWithoutOld
import com.nikol.viewmodel.cancelAllJobs
import com.nikol.viewmodel.launchWithoutOld
import direct.direct_core.DirectEffect
import direct.direct_core.awaitState
import direct.direct_core.filter
import direct.direct_core.onLatest
import direct.direct_core.onSingle
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

fun interface TVRouter : DirectRouter {
    fun toDetail(id: String)
}

class TVViewModel(
    private val getTrendTvUseCase: GetTrendTvUseCase
) : DirectRouterViewModel<TVIntent, TVState, DirectEffect, TVRouter>() {

    override fun createInitialState() =
        TVState(
            isLoading = false,
            trend = TrendContent(
                state = ListState.Loading,
                period = Period.Day
            )
        )

    private val trendStore =
        ContentListFeature(viewModelScope) { params -> getTrendTvUseCase(params) }

    override fun handleIntents() = intents {

        feature(
            store = trendStore,
            state = { childState ->
                setState { copy(trend = trend.copy(state = childState)) }
            },
            intents = { parent ->
                when (parent) {
                    is TVIntent.LoadAllData -> ListIntent.Load(
                        ContentParameter(MediaType.TV, state.value.trend.period)
                    )

                    is TVIntent.ChangePeriodForTrend -> ListIntent.Load(
                        ContentParameter(MediaType.TV, parent.period)
                    )

                    is TVIntent.RefreshData -> ListIntent.Load(
                        ContentParameter(MediaType.TV, state.value.trend.period)
                    )

                    else -> null
                }
            }
        )

        setup<TVIntent.ChangePeriodForTrend> {
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

        onSingle<TVIntent.NavigateToDetail> { intent ->
            navigate { toDetail(intent.id) }
        }

        onLatest<TVIntent.RefreshData> {
            setState { copy(isLoading = true) }
            val minDelayJob = viewModelScope.launch {
                delay(1.seconds)
            }
            store.awaitState(10_000L) {
                val trendReady = state.value.trend.state !is ListState.Loading
                trendReady
            }
            minDelayJob.join()
            setState { copy(isLoading = false) }
        }
    }

    init {
        setIntent(TVIntent.LoadAllData)
    }
}