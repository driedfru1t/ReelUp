package com.nikol.search_impl.presentation.mvi.state

import com.nikol.ui.model.Content
import direct.direct_core.DirectState
import kotlinx.collections.immutable.ImmutableList

data class Search(
    val inputText: String,
    val searchQuery: String
) : DirectState

sealed interface SearchState {
    data object Loading : SearchState
    data object Error : SearchState
    data object EmptyResult : SearchState
    data object Success : SearchState
    data object Initial : SearchState
}