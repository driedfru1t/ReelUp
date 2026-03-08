package com.nikol.detail_impl.presentation.di

import com.nikol.detail_impl.data.remote.service.DetailService
import com.nikol.detail_impl.data.remote.service.DetailServiceImpl
import com.nikol.detail_impl.data.repository.DetailRepositoryImpl
import com.nikol.detail_impl.domain.repository.DetailRepository
import com.nikol.detail_impl.domain.use_case.GetDetailInfoUseCase
import com.nikol.detail_impl.presentation.viewModel.DetailViewModel
import com.nikol.nav_impl.scopedNavigation.Component
import com.nikol.nav_impl.scopedNavigation.component
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module


internal class DetailComponent : Component()
internal class ContentComponent : Component()
internal class PersonComponent : Component()


private val contentModule = module {
    component { ContentComponent() }
    scope<ContentComponent> {
        viewModelOf(::DetailViewModel)
    }
}

private val personComponent = module {
    component { PersonComponent() }
    scope<PersonComponent> {

    }
}

val detailModule = module {

    includes(contentModule, personComponent)
    component { DetailComponent() }
    scope<DetailComponent> {
        scopedOf(::DetailServiceImpl) bind DetailService::class
        scopedOf(::DetailRepositoryImpl) bind DetailRepository::class
        scopedOf(::GetDetailInfoUseCase)
    }
}