package com.nikol.auth_impl.presentation.nav

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nikol.auth_api.Auth
import com.nikol.auth_impl.presentation.di.AuthComponent
import com.nikol.auth_impl.presentation.di.CheckPageComponent
import com.nikol.auth_impl.presentation.di.StartPageComponent
import com.nikol.auth_impl.presentation.ui.screen.CheckScreen
import com.nikol.auth_impl.presentation.ui.screen.StartScreen
import com.nikol.di.scope.LinkedContext
import com.nikol.di.scope.ScopedContext

fun EntryProviderScope<NavKey>.auth(
    onAuthSuccess: () -> Unit
) {
    entry<Auth> {
        val backStack = rememberNavBackStack(CheckPage)
        ScopedContext<AuthComponent> {
            NavDisplay(
                backStack = backStack,
                modifier = Modifier.fillMaxSize(),
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    entry<StartPage> {
                        LinkedContext<StartPageComponent> {
                            StartScreen(
                                onMain = onAuthSuccess,
                            )
                        }
                    }
                    entry<CheckPage> {
                        LinkedContext<CheckPageComponent> {
                            CheckScreen(
                                navToStart = {
                                    backStack.apply {
                                        clear()
                                        add(StartPage)
                                    }
                                },
                                navToHome = onAuthSuccess
                            )
                        }
                    }
                }
            )
        }
    }
}