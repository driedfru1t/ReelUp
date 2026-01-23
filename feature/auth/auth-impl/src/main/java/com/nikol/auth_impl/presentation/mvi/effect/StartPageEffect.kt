package com.nikol.auth_impl.presentation.mvi.effect

import com.nikol.direct_core.DirectEffect

sealed interface StartPageEffect : DirectEffect {
    data object GoToBrowser : StartPageEffect
}