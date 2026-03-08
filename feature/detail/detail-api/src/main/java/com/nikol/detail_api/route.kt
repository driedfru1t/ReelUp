package com.nikol.detail_api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

enum class ContentType {
    MOVIE,
    TV,
    PERSON
}

@Serializable
data class DetailScreen(
    val contentType: ContentType,
    val id: Int
) : NavKey