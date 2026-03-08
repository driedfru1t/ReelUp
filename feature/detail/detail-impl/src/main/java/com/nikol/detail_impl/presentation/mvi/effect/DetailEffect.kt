package com.nikol.detail_impl.presentation.mvi.effect

import direct.direct_core.DirectEffect

sealed interface DetailEffect : DirectEffect {
    data class SeeTrailerOnYouTube(val key: String) : DetailEffect
}