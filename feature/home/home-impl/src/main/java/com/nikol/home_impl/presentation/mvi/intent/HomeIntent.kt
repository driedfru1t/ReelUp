package com.nikol.home_impl.presentation.mvi.intent

import com.nikol.direct_core.DirectIntent
import com.nikol.ui.model.MediaType


sealed interface HomeIntent : DirectIntent {
    data class ChangeTypeContent(val mediaType: MediaType) : HomeIntent
}