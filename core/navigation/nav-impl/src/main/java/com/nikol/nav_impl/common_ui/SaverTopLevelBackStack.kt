package com.nikol.nav_impl.common_ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule


@Composable
fun rememberTopLevelBackStack(config: SerializersModule, init: NavKey) =
    rememberSaveable(saver = saverTopLevelBackStack(config)) {
        TopLevelBackStack(init)
    }


@Serializable
data class NavigationSaveState(
    val startKey: @Polymorphic NavKey,
    val activeKey: @Polymorphic NavKey,
    val stacksData: Map<@Polymorphic NavKey, List<@Polymorphic NavKey>>
)

fun saverTopLevelBackStack(
    config: SerializersModule
): Saver<TopLevelBackStack<NavKey>, String> {
    val navJson = Json {
        serializersModule = config
        allowStructuredMapKeys = true
    }

    return Saver(
        save = { stack ->
            val state = NavigationSaveState(
                startKey = stack.startKey,
                activeKey = stack.topLevelKey,
                stacksData = stack.getStacksData()
            )
            navJson.encodeToString(NavigationSaveState.serializer(), state)
        },
        restore = { jsonString ->
            try {
                val state = navJson.decodeFromString(
                    NavigationSaveState.serializer(),
                    jsonString
                )
                TopLevelBackStack(
                    startKey = state.startKey,
                    initialActiveKey = state.activeKey,
                    initialStacks = state.stacksData
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    )
}