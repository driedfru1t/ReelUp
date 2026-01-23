package com.nikol.home_impl.presentation.mvi.state

import androidx.compose.runtime.Immutable
import com.nikol.direct_core.DirectState
import com.nikol.ui.model.MediaType

@Immutable
data class HomeState(
    val mediaType: MediaType
) : DirectState
