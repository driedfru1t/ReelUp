package com.nikol.search_impl.presentation.mvi.state

import com.nikol.direct_core.DirectState
import com.nikol.ui.model.Content
import kotlinx.collections.immutable.ImmutableList

data class Search(
    val state: SearchState,
    val searchField: String,

) : DirectState

sealed interface SearchState {
    data object Loading : SearchState
    data object Error : SearchState
    data object EmptyResult : SearchState
    data class Success(val list: ImmutableList<Content>)
    data object Initial : SearchState
}