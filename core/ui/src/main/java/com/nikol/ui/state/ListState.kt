package com.nikol.ui.state

import androidx.compose.runtime.Immutable
import direct.direct_core.DirectState
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface ListState<out T> : DirectState {
    data object Loading : ListState<Nothing>
    data object Error : ListState<Nothing>
    data class Success<T>(val content: ImmutableList<T>) : ListState<T>
}