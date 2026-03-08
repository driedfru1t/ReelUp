package com.nikol.search_impl.data.remote.route

import io.ktor.resources.Resource

@Resource("search")
class Search {
    @Resource("multi")
    class Multi(
        val parent: Search = Search(),
        val query: String,
        val include_adult: Boolean = true,
        val page: Int
    )
}