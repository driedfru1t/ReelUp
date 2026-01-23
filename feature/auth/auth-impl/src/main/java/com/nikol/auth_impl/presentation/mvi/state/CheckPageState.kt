package com.nikol.auth_impl.presentation.mvi.state

import com.nikol.direct_core.DirectState

sealed interface CheckPageState : DirectState {
    data object Loading : CheckPageState
}