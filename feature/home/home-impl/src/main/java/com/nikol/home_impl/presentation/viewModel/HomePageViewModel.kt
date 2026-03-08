package com.nikol.home_impl.presentation.viewModel

import com.nikol.home_impl.presentation.mvi.effect.HomeEffect
import com.nikol.home_impl.presentation.mvi.intent.HomeIntent
import com.nikol.home_impl.presentation.mvi.state.HomeState
import com.nikol.ui.model.MediaType
import com.nikol.viewmodel.DirectRouter
import com.nikol.viewmodel.DirectRouterViewModel
import direct.direct_core.filter

interface TypeContentRouter : DirectRouter {
    fun navigateToMovie()
    fun navigateToTV()
}

class HomePageViewModel :
    DirectRouterViewModel<HomeIntent, HomeState, HomeEffect, TypeContentRouter>() {
    override fun createInitialState() = HomeState(
        mediaType = MediaType.MOVIE
    )

    override fun handleIntents() = intents {
        setup<HomeIntent.ChangeTypeContent> {
            filter { intent -> intent.mediaType != state.value.mediaType }
            serial { intent ->
                setState { copy(mediaType = intent.mediaType) }
                navigate {
                    when (intent.mediaType) {
                        MediaType.MOVIE -> navigateToMovie()
                        MediaType.TV -> navigateToTV()
                        else -> {}
                    }
                }
            }
        }
    }
}