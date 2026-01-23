package com.nikol.auth_impl.presentation.viewModel

import com.nikol.auth_impl.domain.model.UserLogin
import com.nikol.auth_impl.domain.model.UserPassword
import com.nikol.auth_impl.domain.parameters.UserCredential
import com.nikol.auth_impl.domain.useCase.CreateGuestSessionUseCase
import com.nikol.auth_impl.domain.useCase.CreateSessionUseCase
import com.nikol.auth_impl.presentation.mvi.effect.StartPageEffect
import com.nikol.auth_impl.presentation.mvi.intent.StartPageIntent
import com.nikol.auth_impl.presentation.mvi.state.CreateSessionState
import com.nikol.auth_impl.presentation.mvi.state.StartPageState
import com.nikol.direct_android.middleware.LogMiddleware
import com.nikol.direct_core.on
import com.nikol.direct_core.onLatest
import com.nikol.direct_core.onSingle
import com.nikol.viewmodel.DirectRouter
import com.nikol.viewmodel.DirectRouterViewModel

interface StartPageRouter : DirectRouter {
    fun main()
}

typealias StartPageComponent = DirectRouterViewModel<StartPageIntent, StartPageState, StartPageEffect, StartPageRouter>

class StartPageViewModel(
    private val createGuestSessionUseCase: CreateGuestSessionUseCase,
    private val createSessionUseCase: CreateSessionUseCase
) :
    StartPageComponent() {
    override fun createInitialState() = StartPageState(
        guestButtonState = CreateSessionState.Initial,
        sessionState = CreateSessionState.Initial,
        login = "",
        password = "",
        showPassword = false
    )

    override fun handleIntents() = intents {
        install(LogMiddleware("StartPage"))
        onSingle<StartPageIntent.ContinueWithGuestAccount> {
            setState { copy(guestButtonState = CreateSessionState.Loading) }
            createGuestSessionUseCase(Unit).fold(
                ifLeft = {
                    setState { copy(guestButtonState = CreateSessionState.Error) }
                },
                ifRight = {
                    setState { copy(guestButtonState = CreateSessionState.Initial) }
                    navigate { main() }
                }
            )
        }

        onSingle<StartPageIntent.LogIn> {
            setState { copy(sessionState = CreateSessionState.Loading) }
            val userCredential = UserCredential(
                login = UserLogin(uiState.value.login),
                password = UserPassword(uiState.value.password)
            )
            createSessionUseCase(userCredential).fold(
                ifLeft = {
                    setState { copy(sessionState = CreateSessionState.Error) }
                },
                ifRight = {
                    setState { copy(sessionState = CreateSessionState.Initial) }
                    navigate { main() }
                }
            )
        }

        onLatest<StartPageIntent.CreateAccount> {
            setEffect { StartPageEffect.GoToBrowser }
        }
        on<StartPageIntent.ChangeLogin> { intent ->
            setState { copy(login = intent.login) }

        }
        on<StartPageIntent.ChangePassword> { intent ->
            setState { copy(password = intent.password) }
        }

        on<StartPageIntent.SwitchPasswordVisibility> {
            setState { copy(showPassword = !showPassword) }
        }
    }
}