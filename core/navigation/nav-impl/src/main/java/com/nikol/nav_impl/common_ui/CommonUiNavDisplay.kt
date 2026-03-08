package com.nikol.nav_impl.common_ui


import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.defaultPopTransitionSpec
import androidx.navigation3.ui.defaultPredictivePopTransitionSpec
import androidx.navigation3.ui.defaultTransitionSpec
import androidx.navigationevent.NavigationEvent
import kotlin.jvm.JvmSuppressWildcards

@Composable
fun CommonUiNavDisplay(
    backStack: TopLevelBackStack<NavKey>,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = { backStack.removeLast() },
    transitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
        defaultTransitionSpec(),
    popTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
        defaultPopTransitionSpec(),
    predictivePopTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.(
        @NavigationEvent.SwipeEdge Int
    ) -> ContentTransform =
        defaultPredictivePopTransitionSpec(),
    entryDecorator: List<@JvmSuppressWildcards NavEntryDecorator<NavKey>> = listOf(
        rememberSaveableStateHolderNavEntryDecorator()
    ),
    entryProvider: EntryProviderScope<NavKey>.() -> Unit,
) {
    val currentEntryProvider by rememberUpdatedState(entryProvider)
    val decoratedEntriesMap = backStack.topLevelStacks.mapValues { (_, stack) ->
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = entryDecorator,
            entryProvider = entryProvider {
                currentEntryProvider()
            }
        )
    }
    val startStack = decoratedEntriesMap[backStack.startKey] ?: emptyList()
    val activeEntries = if (backStack.topLevelKey == backStack.startKey) {
        startStack
    } else {
        val currentStack = decoratedEntriesMap[backStack.topLevelKey] ?: emptyList()
        startStack + currentStack
    }

    NavDisplay(
        entries = activeEntries,
        modifier = modifier,
        onBack = onBack,
        transitionSpec = transitionSpec,
        popTransitionSpec = popTransitionSpec,
        predictivePopTransitionSpec = predictivePopTransitionSpec
    )
}
