package com.nikol.search_impl.presentation.mvi.intent

import com.nikol.detail_api.ContentType
import com.nikol.direct_core.DirectIntent

sealed interface SearchIntent : DirectIntent {
    data class Search(val text: String) : SearchIntent
    data class GoToDetail(val contentType: ContentType, val id: Int) : SearchIntent
    data object DeleteHistory : SearchIntent
    data object ClearText : SearchIntent
}