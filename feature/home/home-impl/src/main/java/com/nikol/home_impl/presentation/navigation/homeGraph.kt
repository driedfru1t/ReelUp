package com.nikol.home_impl.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import com.nikol.detail_api.DetailScreen
import com.nikol.di.scope.LinkedContext
import com.nikol.di.scope.ScopedContext
import com.nikol.home_api.destination.HomeGraph
import com.nikol.home_impl.presentation.di.HomeComponent
import com.nikol.home_impl.presentation.di.MovieComponent
import com.nikol.home_impl.presentation.di.TVComponent
import com.nikol.home_impl.presentation.ui.comonents.HomeTopBar
import com.nikol.home_impl.presentation.ui.screens.MovieScreen
import com.nikol.home_impl.presentation.ui.screens.TVScreen
import com.nikol.nav_impl.common_ui.CommonUiNavDisplay
import com.nikol.nav_impl.common_ui.rememberTopLevelBackStack
import com.nikol.ui.model.MediaType
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlin.text.get

private val config = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(MoviePage::class, MoviePage.serializer())
        subclass(TVPage::class, TVPage.serializer())
    }
}

fun EntryProviderScope<NavKey>.homeEntries(
    onDetail: (DetailScreen) -> Unit,
    modifier: Modifier
) {
    entry<HomeGraph>(
        metadata = mapOf("tab" to true, "range" to 1)
    ) {
        val backStack = rememberTopLevelBackStack(config, MoviePage)
        ScopedContext<HomeComponent> {
            Scaffold(
                modifier = modifier
                    .fillMaxSize(),
                topBar = {
                    HomeTopBar(
                        selectedType = when (backStack.topLevelKey) {
                            is MoviePage -> MediaType.MOVIE
                            is TVPage -> MediaType.TV
                            else -> MediaType.MOVIE
                        }
                    ) { type ->
                        val route = when (type) {
                            MediaType.MOVIE -> MoviePage
                            MediaType.TV -> TVPage
                            MediaType.PERSON -> TODO()
                        }
                        backStack.addTopLevel(route)
                    }
                }
            ) { innerPadding ->
                CommonUiNavDisplay(
                    backStack = backStack,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding()),
                    entryDecorator = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                    ),
                    transitionSpec = {
                        val initialIdx = initialState.metadata["index"] as? Int ?: 0
                        val targetIdx = targetState.metadata["index"] as? Int ?: 0
                        val direction = if (targetIdx > initialIdx) 1 else -1
                        slideInHorizontally(animationSpec = tween(300)) { fullWidth -> fullWidth * direction } togetherWith
                                slideOutHorizontally(animationSpec = tween(300)) { fullWidth -> -fullWidth * direction }
                    },
                    popTransitionSpec = {
                        val initialIdx = initialState.metadata["index"] as? Int ?: 0
                        val targetIdx = targetState.metadata["index"] as? Int ?: 0
                        val direction = if (targetIdx > initialIdx) 1 else -1
                        slideInHorizontally(animationSpec = tween(300)) { fullWidth -> fullWidth * direction } togetherWith
                                slideOutHorizontally(animationSpec = tween(300)) { fullWidth -> -fullWidth * direction }
                    },
                    predictivePopTransitionSpec = {
                        val initialIdx = initialState.metadata["index"] as? Int ?: 0
                        val targetIdx = targetState.metadata["index"] as? Int ?: 0

                        val direction = if (targetIdx > initialIdx) 1 else -1

                        slideInHorizontally(animationSpec = tween(300)) { fullWidth -> fullWidth * direction } togetherWith
                                slideOutHorizontally(animationSpec = tween(300)) { fullWidth -> -fullWidth * direction }
                    }
                ) {
                    entry<MoviePage>(
                        metadata = mapOf("index" to 1)
                    ) {
                        LinkedContext<MovieComponent> {
                            MovieScreen(
                                onDetail = { contentType, id ->
                                    onDetail(DetailScreen(contentType, id))
                                }
                            )
                        }
                    }
                    entry<TVPage>(
                        metadata = mapOf("index" to 2)
                    ) {
                        LinkedContext<TVComponent> {
                            TVScreen(
                                onDetail = { contentType, id ->
                                    onDetail(DetailScreen(contentType, id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}