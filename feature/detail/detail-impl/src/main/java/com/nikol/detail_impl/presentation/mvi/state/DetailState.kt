package com.nikol.detail_impl.presentation.mvi.state

import androidx.compose.runtime.Immutable
import com.nikol.detail_api.ContentType
import com.nikol.detail_impl.presentation.ui.model.DetailContent
import com.nikol.ui.state.SingleState
import direct.direct_core.DirectState

@Immutable
data class DetailState(
    val contentType: ContentType,
    val state: SingleState<DetailContent>,
    val isLoading: Boolean,
    val showBottomSheet: Boolean
) : DirectState
