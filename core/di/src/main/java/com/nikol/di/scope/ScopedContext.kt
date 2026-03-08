package com.nikol.di.scope

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.nikol.nav_impl.scopedNavigation.Component
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.ComposeContextWrapper
import org.koin.compose.LocalKoinApplication
import org.koin.compose.LocalKoinScope
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.Scope


@OptIn(KoinInternalApi::class)
@Composable
inline fun <reified C : Component> ScopedContext(
    noinline parameters: ParametersDefinition? = null,
    crossinline content: @Composable Scope.() -> Unit
) {
    val component: C = koinViewModel(parameters = parameters)
    CompositionLocalProvider(
        LocalKoinApplication provides ComposeContextWrapper(component.scope.getKoin()),
        LocalKoinScope provides ComposeContextWrapper(component.scope),
    ) {
        with(component.scope) {
            content()
        }
    }
}


@OptIn(KoinInternalApi::class)
@Composable
inline fun <reified C : Component> Scope.LinkedContext(
    crossinline content: @Composable Scope.() -> Unit
) {
    val component: C = koinViewModel()
    component.scope.linkTo(this)

    CompositionLocalProvider(
        LocalKoinApplication provides ComposeContextWrapper(component.scope.getKoin()),
        LocalKoinScope provides ComposeContextWrapper(component.scope),
    ) {
        with(component.scope) {
            content()
        }
    }
}