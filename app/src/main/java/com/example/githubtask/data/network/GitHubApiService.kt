package com.example.githubtask.data.network

import com.example.githubtask.data.model.Repository
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApiService {
    @GET("repositories")
    suspend fun getPublicRepositories(
        @Query("since") since: Int,
        @Query("per_page") perPage: Int
    ): List<Repository>
}