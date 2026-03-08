package com.nikol.search_impl.presentation.navigation

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nikol.detail_api.DetailScreen
import com.nikol.di.scope.ScopedContext
import com.nikol.search_api.SearchGraph
import com.nikol.search_impl.presentation.di.SearchComponent
import com.nikol.search_impl.presentation.ui.SearchScreen


fun EntryProviderScope<NavKey>.searchEntries(
    onDetail: (DetailScreen) -> Unit,
    modifier: Modifier
) {
    entry<SearchGraph>(
        metadata = mapOf("tab" to true, "range" to 2)
    ) {
        ScopedContext<SearchComponent> {
            SearchScreen(
                onDetail = { onDetail(it) },
                modifier = modifier
            )
        }
    }
}
