package com.nikol.detail_impl.presentation.viewModel

import androidx.lifecycle.SavedStateHandle
import com.nikol.detail_api.ContentType
import com.nikol.detail_api.DetailScreen
import com.nikol.detail_impl.domain.errors.DetailsError
import com.nikol.detail_impl.domain.parameter.DetailParameter
import com.nikol.detail_impl.domain.use_case.GetDetailInfoUseCase
import com.nikol.detail_impl.presentation.mvi.effect.DetailEffect
import com.nikol.detail_impl.presentation.mvi.intent.DetailIntent
import com.nikol.detail_impl.presentation.mvi.state.DetailState
import com.nikol.detail_impl.presentation.ui.ext.toUi
import com.nikol.detail_impl.presentation.ui.model.DetailContent
import com.nikol.ui.state.SingleState
import com.nikol.viewmodel.DirectRouter
import com.nikol.viewmodel.DirectRouterViewModel
import direct.direct_core.filter
import direct.direct_core.on
import direct.direct_core.onLatest


interface DetailRouter : DirectRouter {
    fun onBack()
    fun toContent(detailScreen: DetailScreen)
}

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val detailScreen: DetailScreen,
    private val getDetailInfoUseCase: GetDetailInfoUseCase
) : DirectRouterViewModel<DetailIntent, DetailState, DetailEffect, DetailRouter>() {
    private val type: ContentType = detailScreen.contentType
    private val id: Int = detailScreen.id


    init {
        setIntent(DetailIntent.LoadAllData(type))
    }

    override fun createInitialState() = DetailState(
        contentType = type,
        state = SingleState.Loading,
        isLoading = false,
        showBottomSheet = false
    )

    override fun handleIntents() = intents {
        onNavigate<DetailIntent.NavigateBack>(isFinal = true) {
            onBack()
        }

        onLatest<DetailIntent.LoadAllData> { intent ->
            getDetailInfoUseCase(
                params = DetailParameter(contentType = intent.contentType, id = id)
            ).fold(ifLeft = { error ->
                when (error) {
                    DetailsError.Network,
                    DetailsError.NotFound,
                    DetailsError.ServerError,
                    is DetailsError.Unknown -> setState { copy(state = SingleState.Error) }

                    DetailsError.UserNotAuth -> TODO()
                }
            }, ifRight = {
                val model = it.toUi()
                setState { copy(state = SingleState.Success(model)) }
            })
        }

        onNavigate<DetailIntent.NavigateOtherDetail> { toContent(it.contentType) }

        setup<DetailIntent.ToggleDescription> {
            filter { state.value.state is SingleState.Success }
            serial {
                val state = state.value.state as SingleState.Success<DetailContent>
                setState {
                    copy(
                        state = SingleState.Success(
                            content = state.content.copy(
                                showAllDescription = !state.content.showAllDescription
                            )
                        )
                    )
                }
            }
        }
        on<DetailIntent.Update> {
            setState { copy(isLoading = true) }
            getDetailInfoUseCase(
                params = DetailParameter(contentType = type, id = id)
            ).fold(ifLeft = {
                setState { copy(state = SingleState.Error) }
            }, ifRight = {
                val model = it.toUi()
                setState { copy(state = SingleState.Success(model), isLoading = false) }
            }
            )
        }

        setup<DetailIntent.ToggleAction> {
            filter { state.value.state is SingleState.Success }
            serial {
                setState { copy(showBottomSheet = !showBottomSheet) }
            }
        }

        setup<DetailIntent.SeeTriller> {
            filter { state.value.state is SingleState.Success }
            serial {
                val content = (state.value.state as SingleState.Success).content
                content.trailers.firstOrNull()?.let { video ->
                    setEffect { DetailEffect.SeeTrailerOnYouTube(video.key) }
                }
            }
        }
    }
}