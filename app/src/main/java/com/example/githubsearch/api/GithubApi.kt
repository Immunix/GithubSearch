package com.example.githubsearch.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface GithubApi {

    companion object {
        const val BASE_URL = "https://api.github.com"
    }

    @Headers("Accept: application/vnd.github.v3+json")
    @GET("/search/repositories")
    suspend fun searchProjects(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ) : GithubResponse

}