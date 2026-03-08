package com.nikol.auth_impl.presentation.mvi.state

import direct.direct_core.DirectState

data class StartPageState(
    val guestButtonState: CreateSessionState,
    val sessionState: CreateSessionState,
    val login: String,
    val password: String,
    val showPassword: Boolean
) : DirectState

sealed interface CreateSessionState {
    data object Error : CreateSessionState
    data object Loading : CreateSessionState
    data object Initial : CreateSessionState
}