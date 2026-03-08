package com.nikol.di.scope

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.nikol.viewmodel.DirectRouter
import com.nikol.viewmodel.DirectRouterViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.ParametersDefinition

@Composable
inline fun <reified VM : DirectRouterViewModel<*, *, *, R>, reified R : DirectRouter> directViewModel(
    noinline parameters: ParametersDefinition? = null,
    crossinline routerFactory: () -> R
): VM {
    val vm: VM = koinViewModel(parameters = parameters)
    DisposableEffect(vm) {
        val router = routerFactory()
        vm.attachRouter(router)
        onDispose { vm.detachRouter() }
    }
    return vm
}
