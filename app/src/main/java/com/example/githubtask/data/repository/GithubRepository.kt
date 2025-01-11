package com.example.githubtask.data.repository

import com.example.githubtask.data.network.ApiState

interface GithubRepository {
    suspend fun getPublicRepositories(since: Int, perPage: Int): ApiState
    suspend fun searchRepositories(query: String, perPage: Int, page: Int): ApiState
}