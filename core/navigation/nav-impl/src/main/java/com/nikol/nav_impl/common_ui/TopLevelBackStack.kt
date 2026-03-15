package com.nikol.nav_impl.common_ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import kotlin.collections.set

class TopLevelBackStack<T : NavKey>(
    val startKey: T,
    initialActiveKey: T? = null,
    initialStacks: Map<T, List<T>>? = null
) {
    val topLevelStacks = mutableStateMapOf<T, SnapshotStateList<T>>()

    var topLevelKey by mutableStateOf(initialActiveKey ?: startKey)
        private set

    init {
        if (!initialStacks.isNullOrEmpty()) {
            initialStacks.forEach { (key, list) ->
                topLevelStacks[key] = mutableStateListOf<T>().apply { addAll(list) }
            }
        } else {
            topLevelStacks[startKey] = mutableStateListOf(startKey)
        }
    }

    fun addTopLevel(key: T) {
        if (topLevelStacks[key] == null) {
            topLevelStacks[key] = mutableStateListOf(key)
        }
        topLevelKey = key
    }

    fun add(key: T) {
        topLevelStacks[topLevelKey]?.add(key)
    }

    fun removeLast() {
        val currentStack = topLevelStacks[topLevelKey] ?: return
        if (currentStack.size > 1) {
            currentStack.removeLast()
        } else if (topLevelKey != startKey) {
            topLevelKey = startKey
        }
    }

    fun resetToRoot(key: T) {
        val stack = topLevelStacks[key]
        if (stack != null && stack.size > 1) {
            val root = stack.first()
            stack.clear()
            stack.add(root)
        }
        topLevelKey = key
    }

    fun getStacksData(): Map<T, List<T>> = topLevelStacks.mapValues { it.value.toList() }
}