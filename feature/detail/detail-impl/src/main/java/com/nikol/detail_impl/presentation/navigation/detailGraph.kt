package com.nikol.detail_impl.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nikol.detail_api.ContentType
import com.nikol.detail_api.DetailScreen
import com.nikol.detail_impl.presentation.di.ContentComponent
import com.nikol.detail_impl.presentation.di.DetailComponent
import com.nikol.detail_impl.presentation.di.PersonComponent
import com.nikol.detail_impl.presentation.ui.screens.DetailScreenUi
import com.nikol.di.scope.LinkedContext
import com.nikol.di.scope.ScopedContext


fun EntryProviderScope<NavKey>.detailEntries(
    onBack: () -> Unit,
    onDetail: (DetailScreen) -> Unit
) {
    entry<DetailScreen> { key ->
        ScopedContext<DetailComponent> {
            val type = key.contentType
            when (type) {
                ContentType.MOVIE,
                ContentType.TV -> {
                    LinkedContext<ContentComponent> {
                        DetailScreenUi(
                            onBack = { onBack() },
                            onDetail = { detailScreen ->
                                onDetail(detailScreen)
                            },
                            screen = key
                        )
                    }
                }

                ContentType.PERSON -> {
                    LinkedContext<PersonComponent> { }
                }
            }
        }
    }
}