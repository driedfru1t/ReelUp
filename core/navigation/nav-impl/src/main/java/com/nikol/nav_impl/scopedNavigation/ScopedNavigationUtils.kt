package com.nikol.nav_impl.scopedNavigation

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.scope.Scope

inline fun <reified C : Component> Module.component(
    crossinline constructor: Scope.() -> C,
) = viewModel { this.constructor() }
