package com.nikol.search_impl.data.remote.ext

import com.nikol.detail_api.ContentType
import com.nikol.search_impl.data.remote.models.PagedResponseDTO
import com.nikol.search_impl.data.remote.models.SearchItemDTO
import com.nikol.search_impl.domain.models.PagedResult
import com.nikol.search_impl.domain.models.SearchResultDomain

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

fun PagedResponseDTO<SearchItemDTO>.toDomain(): PagedResult<SearchResultDomain> {
    return PagedResult(
        items = this.results.mapNotNull { it.toDomain() },
        currentPage = this.page,
        totalPages = this.totalPages
    )
}

fun SearchItemDTO.toDomain(): SearchResultDomain? {
    val type = when (this.mediaType) {
        "movie" -> ContentType.MOVIE
        "tv" -> ContentType.TV
        "person" -> ContentType.PERSON
        else -> return null
    }

    val displayTitle = this.title ?: this.name ?: return null

    val imagePath = this.posterPath ?: this.profilePath
    val fullImageUrl = imagePath?.let { "$IMAGE_BASE_URL$it" }

    val dateString = this.releaseDate ?: this.firstAirDate
    val year = dateString?.take(4) ?: ""

    return SearchResultDomain(
        id = this.id,
        type = type,
        title = displayTitle,
        description = this.overview ?: "",
        imageUrl = fullImageUrl,
        rating = this.voteAverage ?: 0.0,
        releaseYear = year
    )
}