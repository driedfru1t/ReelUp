package com.nikol.search_impl.data.remote.service

import com.nikol.domainutil.BaseResponseError
import com.nikol.domainutil.ErrorMessage
import com.nikol.network.extensions.catchKtor
import com.nikol.search_impl.data.remote.route.Search
import com.nikol.search_impl.domain.models.SearchParams
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.request.accept
import io.ktor.client.request.parameter
import io.ktor.http.ContentType

class SearchServiceImpl(
    private val httpClient: HttpClient
) : SearchService {
    override suspend fun multiSearch(searchParams: SearchParams): SearchResponse = catchKtor(
        block = {
            val path = Search.Multi(
                query = searchParams.query,
                page = searchParams.page
            )
            httpClient.get(path) {
                accept(ContentType.Application.Json)
                parameter("language", "ru-RU")
            }.body()
        },
        errorMapper = { errorMessage, _, _ ->
            BaseResponseError.Error(ErrorMessage(errorMessage?.statusMessage ?: ""))
        }
    )

    override suspend fun tvSearch(): SearchResponse {
        TODO("Not yet implemented")
    }

    override suspend fun movieSearch(): SearchResponse {
        TODO("Not yet implemented")
    }

    override suspend fun personSearch(): SearchResponse {
        TODO("Not yet implemented")
    }
}