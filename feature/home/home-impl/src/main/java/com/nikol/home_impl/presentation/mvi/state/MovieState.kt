package com.nikol.home_impl.presentation.mvi.state

import androidx.compose.runtime.Immutable
import com.nikol.direct_core.DirectState
import com.nikol.home_impl.domain.parameters.Period
import com.nikol.ui.model.Content
import com.nikol.ui.state.ListState

@Immutable
data class MovieState(
    val isLoading: Boolean,
    val trend: TrendContent,
    val nowPlaying: ListState<Content>
) : DirectState

@Immutable
data class TrendContent(
    val state: ListState<Content>,
    val period: Period
)
