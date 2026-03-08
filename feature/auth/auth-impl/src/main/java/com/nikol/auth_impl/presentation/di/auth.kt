package com.nikol.auth_impl.presentation.di

import com.nikol.auth_impl.data.remote.service.AuthService
import com.nikol.auth_impl.data.remote.service.AuthServiceImpl
import com.nikol.auth_impl.data.repository.AuthRepositoryImpl
import com.nikol.auth_impl.domain.repository.AuthRepository
import com.nikol.auth_impl.domain.useCase.CheckLogInUseCase
import com.nikol.auth_impl.domain.useCase.CreateGuestSessionUseCase
import com.nikol.auth_impl.domain.useCase.CreateSessionUseCase
import com.nikol.auth_impl.presentation.viewModel.CheckLogInPageViewModel
import com.nikol.auth_impl.presentation.viewModel.StartPageViewModel
import com.nikol.nav_impl.scopedNavigation.Component
import com.nikol.nav_impl.scopedNavigation.component
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal class AuthComponent : Component()
internal class StartPageComponent : Component()
internal class CheckPageComponent : Component()

val startPageModule = module {
    component { StartPageComponent() }
    scope<StartPageComponent> {
        factoryOf(::CreateGuestSessionUseCase)
        factoryOf(::CreateSessionUseCase)
        viewModelOf(::StartPageViewModel)
    }
}

val checkPageModule = module {
    component { CheckPageComponent() }
    scope<CheckPageComponent> {
        scopedOf(::CheckLogInUseCase)
        viewModelOf(::CheckLogInPageViewModel)
    }
}

val authModule = module {

    includes(startPageModule, checkPageModule)
    component { AuthComponent() }
    scope<AuthComponent> {
        scopedOf(::AuthServiceImpl) bind AuthService::class
        scopedOf(::AuthRepositoryImpl) bind AuthRepository::class

    }
}