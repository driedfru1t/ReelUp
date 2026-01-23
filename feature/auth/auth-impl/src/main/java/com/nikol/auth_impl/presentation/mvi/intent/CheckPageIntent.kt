package com.nikol.auth_impl.presentation.mvi.intent

import com.nikol.direct_core.DirectIntent

sealed interface CheckPageIntent : DirectIntent{
    data object StartCheck : CheckPageIntent
    data object NavigateToSart : CheckPageIntent
    data object NavigateToHome : CheckPageIntent
}