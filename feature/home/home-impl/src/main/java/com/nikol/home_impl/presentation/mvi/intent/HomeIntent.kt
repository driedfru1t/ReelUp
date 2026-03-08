package com.nikol.home_impl.presentation.mvi.intent

import com.nikol.ui.model.MediaType
import direct.direct_core.DirectIntent


sealed interface HomeIntent : DirectIntent {
    data class ChangeTypeContent(val mediaType: MediaType) : HomeIntent
}