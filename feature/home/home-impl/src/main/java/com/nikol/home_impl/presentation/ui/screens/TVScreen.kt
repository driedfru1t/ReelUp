package com.nikol.home_impl.presentation.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikol.detail_api.ContentType
import com.nikol.di.scope.directViewModel
import com.nikol.home_impl.domain.parameters.Period
import com.nikol.home_impl.presentation.mvi.intent.TVIntent
import com.nikol.home_impl.presentation.mvi.state.TVState
import com.nikol.home_impl.presentation.ui.comonents.PeriodSelector
import com.nikol.home_impl.presentation.viewModel.TVRouter
import com.nikol.home_impl.presentation.viewModel.TVViewModel
import com.nikol.ui.component.ContentSection
import com.nikol.ui.model.Content
import com.nikol.ui.model.MediaType

@Composable
internal fun TVScreen(
    onBackPressed: () -> Unit = {},
    onDetail: (ContentType, Int) -> Unit
) {
    val viewModel = directViewModel<TVViewModel, TVRouter> {
        TVRouter { }
    }



    val state by viewModel.state.collectAsStateWithLifecycle()
    TvScreenContent(
        state = state,
        onPeriodChanged = { period -> viewModel.setIntent(TVIntent.ChangePeriodForTrend(period)) },
        onMovieClick = { tv ->
            onDetail(
                when (tv.type) {
                    MediaType.MOVIE -> ContentType.MOVIE
                    MediaType.TV -> ContentType.TV
                    MediaType.PERSON -> ContentType.PERSON
                },
                tv.id
            )
        },
        onRefresh = { viewModel.setIntent(TVIntent.RefreshData) }
    )
}

@Composable
internal fun TvScreenContent(
    state: TVState,
    onPeriodChanged: (Period) -> Unit,
    onMovieClick: (Content) -> Unit,
    onRefresh: () -> Unit
) {
    val refreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize(),
        isRefreshing = state.isLoading,
        state = refreshState,
        onRefresh = onRefresh
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                ContentSection(
                    title = "Trending",
                    state = state.trend.state,
                    onItemClick = onMovieClick,
                    headerAction = {
                        PeriodSelector(
                            currentPeriod = state.trend.period,
                            onPeriodChanged = onPeriodChanged
                        )
                    }
                )
            }
        }
    }
}