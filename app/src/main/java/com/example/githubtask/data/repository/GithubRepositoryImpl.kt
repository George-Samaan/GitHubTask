package com.example.githubtask.data.repository

import com.example.githubtask.data.network.ApiState
import com.example.githubtask.data.network.GitHubApiService

class GithubRepositoryImpl(private val gitHubApiService: GitHubApiService) : GithubRepository {
    override suspend fun getPublicRepositories(since: Int, perPage: Int): ApiState {
        return try {
            val repos = gitHubApiService.getPublicRepositories(since, perPage)
            ApiState.Success(repos)
        } catch (e: Exception) {
            ApiState.Failure(e.localizedMessage ?: "Error fetching repositories")
        }
    }
}