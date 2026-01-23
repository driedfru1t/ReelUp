package com.nikol.auth_impl.presentation.viewModel

import android.util.Log
import com.nikol.auth_impl.domain.useCase.CheckLogInUseCase
import com.nikol.auth_impl.presentation.mvi.intent.CheckPageIntent
import com.nikol.auth_impl.presentation.mvi.state.CheckPageState
import com.nikol.direct_core.DirectEffect
import com.nikol.direct_core.debounce
import com.nikol.direct_core.on
import com.nikol.viewmodel.DirectRouter
import com.nikol.viewmodel.DirectRouterViewModel


interface CheckRouter : DirectRouter {
    fun toStart()
    fun toHome()
}

typealias CheckStore = DirectRouterViewModel<CheckPageIntent, CheckPageState, DirectEffect, CheckRouter>

class CheckLogInPageViewModel(
    private val checkLogInUseCase: CheckLogInUseCase
) : CheckStore() {
    override fun createInitialState() = CheckPageState.Loading

    init {
        setIntent(CheckPageIntent.StartCheck)
    }

    override fun handleIntents() = intents {
        setup<CheckPageIntent.StartCheck> {
            debounce(600)
            dropping {
                checkLogInUseCase(Unit).fold(
                    ifLeft = {
                        navigate { toStart() }
                    },
                    ifRight = {
                        Log.d("Auth", it.toString())
                        navigate { toHome() }
                    }
                )
            }
            catch {
                Log.d("Auth", "error")
                navigate { toStart() }
            }
        }
        on<CheckPageIntent.NavigateToSart> {
            navigate { toStart() }
        }
        on<CheckPageIntent.NavigateToHome> {
            navigate { toStart() }
        }
    }
}