package com.nikol.auth_impl.presentation.mvi.state

import direct.direct_core.DirectState

sealed interface CheckPageState : DirectState {
    data object Loading : CheckPageState
}