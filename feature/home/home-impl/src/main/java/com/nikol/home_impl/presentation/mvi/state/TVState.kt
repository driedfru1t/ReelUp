package com.nikol.home_impl.presentation.mvi.state

import androidx.compose.runtime.Immutable
import direct.direct_core.DirectState

@Immutable
data class TVState(
    val isLoading: Boolean,
    val trend: TrendContent
) : DirectState