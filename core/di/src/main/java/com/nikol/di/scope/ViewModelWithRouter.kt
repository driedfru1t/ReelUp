package com.nikol.di.scope

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.nikol.viewmodel.DirectRouter
import com.nikol.viewmodel.DirectRouterViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
inline fun <reified VM : DirectRouterViewModel<*, *, *, R>, reified R : DirectRouter> directViewModel(
    crossinline routerFactory: () -> R
): VM {
    val vm: VM = koinViewModel()
    DisposableEffect(vm) {
        val router = routerFactory()
        vm.attachRouter(router)
        onDispose { vm.detachRouter() }
    }
    return vm
}
