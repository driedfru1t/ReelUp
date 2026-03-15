package com.nikol.reelup.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import com.nikol.auth_api.Auth
import com.nikol.detail_api.DetailScreen
import com.nikol.detail_impl.presentation.navigation.detailEntries
import com.nikol.home_api.destination.HomeGraph
import com.nikol.home_impl.presentation.navigation.homeEntries
import com.nikol.nav_impl.commonDestination.MainGraph
import com.nikol.nav_impl.common_ui.CommonUiNavDisplay
import com.nikol.nav_impl.common_ui.rememberTopLevelBackStack
import com.nikol.search_api.SearchGraph
import com.nikol.search_impl.presentation.navigation.searchEntries
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

enum class MainTab(
    val route: NavKey,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Home(HomeGraph, "Home", Icons.Rounded.Home),
    Search(SearchGraph, "Search", Icons.Rounded.Search),
}

private val config = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(Auth::class, Auth.serializer())
        subclass(HomeGraph::class, HomeGraph.serializer())
        subclass(DetailScreen::class, DetailScreen.serializer())
        subclass(SearchGraph::class, SearchGraph.serializer())
    }
}

private const val METADATA_TAB = "tab"
private const val METADATA_RANGE = "range"
fun EntryProviderScope<NavKey>.mainGraph(
    onAuth: () -> Unit
) {
    entry<MainGraph> {
        val backStack = rememberTopLevelBackStack(config, HomeGraph)
        Scaffold(
            bottomBar = {
                MainGraphBottomBar(
                    tabs = MainTab.entries,
                    currentTab = backStack.topLevelKey,
                    onTabClick = { tab ->
                        if (tab.route == backStack.topLevelKey) {
                            backStack.resetToRoot(backStack.topLevelKey)
                        } else {
                            backStack.addTopLevel(tab.route)
                        }
                    }
                )
            }
        ) { innerPadding ->


            CommonUiNavDisplay(
                backStack = backStack,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding()),
                entryDecorator = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                transitionSpec = {
                    val initialIsTab = initialState.metadata[METADATA_TAB] as? Boolean ?: false
                    val targetIsTab = targetState.metadata[METADATA_TAB] as? Boolean ?: false

                    val initialRange = initialState.metadata[METADATA_RANGE] as? Int ?: 0
                    val targetRange = targetState.metadata[METADATA_RANGE] as? Int ?: 0

                    when {
                        initialIsTab && targetIsTab -> {
                            val direction = if (targetRange > initialRange) 1 else -1
                            slideInHorizontally(tween(400)) { it * direction } togetherWith
                                    slideOutHorizontally(tween(400)) { -it * direction }
                        }

                        !targetIsTab -> {
                            slideInHorizontally(tween(400)) { it } togetherWith
                                    slideOutHorizontally(tween(400)) { -it / 4 }
                        }


                        else -> {
                            slideInHorizontally(tween(400)) { -it / 4 } togetherWith
                                    slideOutHorizontally(tween(400)) { it }
                        }
                    }
                },

                popTransitionSpec = {
                    val initialIsTab = initialState.metadata[METADATA_TAB] as? Boolean ?: false
                    val targetIsTab = targetState.metadata[METADATA_TAB] as? Boolean ?: false

                    val initialRange = initialState.metadata[METADATA_RANGE] as? Int ?: 0
                    val targetRange = targetState.metadata[METADATA_RANGE] as? Int ?: 0

                    when {
                        initialIsTab && targetIsTab -> {
                            val direction = if (targetRange > initialRange) 1 else -1
                            slideInHorizontally(tween(400)) { it * direction } togetherWith
                                    slideOutHorizontally(tween(400)) { -it * direction }
                        }

                        else -> {
                            slideInHorizontally(tween(400)) { -it / 4 } togetherWith
                                    slideOutHorizontally(tween(400)) { it }
                        }
                    }
                },
                predictivePopTransitionSpec = {
                    slideInHorizontally(tween(400)) { -it / 4 } togetherWith
                            slideOutHorizontally(tween(400)) { it }
                }
            ) {
                homeEntries(
                    onDetail = { backStack.add(it) },
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                )
                detailEntries(
                    onBack = { backStack.removeLast() },
                    onDetail = { backStack.add(it) }
                )
                searchEntries(
                    onDetail = { backStack.add(it) },
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                )
            }
        }
    }
}

@Composable
fun MainGraphBottomBar(
    tabs: List<MainTab>,
    currentTab: NavKey,
    onTabClick: (MainTab) -> Unit
) {
    BottomAppBar {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = tab.route == currentTab,
                onClick = { onTabClick(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title
                    )
                },
                label = { Text(tab.title) }
            )
        }
    }
}
