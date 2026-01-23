package com.nikol.home_impl.presentation.ui.screens

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
import com.nikol.home_impl.presentation.mvi.intent.MovieIntent
import com.nikol.home_impl.presentation.mvi.state.MovieState
import com.nikol.home_impl.presentation.ui.comonents.PeriodSelector
import com.nikol.home_impl.presentation.viewModel.MovieRouter
import com.nikol.home_impl.presentation.viewModel.MovieViewModel
import com.nikol.ui.component.ContentSection
import com.nikol.ui.model.Content
import com.nikol.ui.model.MediaType

@Composable
internal fun MovieScreen(
    onDetail: (ContentType, Int) -> Unit
) {

    val viewModel = directViewModel <MovieViewModel, MovieRouter> {
        MovieRouter { }
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MovieScreenContent(
        state = state,
        onPeriodChanged = { period -> viewModel.setIntent(MovieIntent.ChangePeriodForTrend(period)) },
        onMovieClick = { movie ->
            onDetail(
                when (movie.type) {
                    MediaType.MOVIE -> ContentType.MOVIE
                    MediaType.TV -> ContentType.TV
                    MediaType.PERSON -> ContentType.PERSON
                },
                movie.id
            )
        },
        onRefresh = { viewModel.setIntent(MovieIntent.RefreshData) }
    )
}

@Composable
internal fun MovieScreenContent(
    state: MovieState,
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
            item {
                ContentSection(
                    title = "Now playing",
                    state = state.nowPlaying,
                    onItemClick = onMovieClick
                )
            }
        }
    }
}
