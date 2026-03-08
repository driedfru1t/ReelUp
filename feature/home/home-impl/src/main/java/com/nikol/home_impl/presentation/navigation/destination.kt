package com.nikol.home_impl.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object MoviePage : NavKey

@Serializable
data object TVPage : NavKey

@Serializable
data object Home : NavKey
