package com.nikol.search_impl.domain.models

import com.nikol.detail_api.ContentType


data class SearchResultDomain(
    val id: Int,
    val type: ContentType,

    val title: String,
    val description: String,
    val imageUrl: String?,
    val rating: Double,
    val releaseYear: String
)

data class PagedResult<T>(
    val items: List<T>,
    val currentPage: Int,
    val totalPages: Int
)