package com.nikol.home_impl.presentation.mvi.store

import arrow.core.Either
import com.nikol.domainutil.BaseResponseError
import com.nikol.home_impl.domain.model.MediaItem
import com.nikol.home_impl.domain.parameters.ContentParameter
import com.nikol.home_impl.presentation.mvi.intent.ListIntent
import com.nikol.home_impl.presentation.ui.ext.toUi
import com.nikol.ui.model.Content
import com.nikol.ui.state.ListState
import direct.direct_core.DirectEffect
import direct.direct_core.DirectStore
import direct.direct_core.onLatest
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope

class ContentListFeature(
    scope: CoroutineScope,
    private val loader: suspend (ContentParameter) -> Either<BaseResponseError, List<MediaItem>>,
) : DirectStore<ListIntent, ListState<Content>, DirectEffect>(scope) {

    override fun createInitialState(): ListState<Content> = ListState.Loading

    override fun handleIntents() = runDsl {
        onLatest<ListIntent.Load> { intent ->
            dispatchState { ListState.Loading }

            loader(intent.params).fold(
                ifLeft = {
                    dispatchState { ListState.Error }
                },
                ifRight = { domainList ->
                    val uiList = domainList.map { it.toUi() }.toImmutableList()
                    dispatchState { ListState.Success(uiList) }
                }
            )
        }
    }
}