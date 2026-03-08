package com.nikol.home_impl.presentation.mvi.intent

import com.nikol.home_impl.domain.parameters.ContentParameter
import direct.direct_core.DirectIntent


sealed interface ListIntent : DirectIntent {
    data class Load(val params: ContentParameter) : ListIntent
}