package com.example.githubtask.feature.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubtask.data.network.ApiState
import com.example.githubtask.data.repository.GithubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val gitHubRepo: GithubRepository) : ViewModel() {
    private val _repositories = MutableStateFlow<ApiState>(ApiState.Loading)
    val repositories: MutableStateFlow<ApiState> = _repositories

    private var currentPage = 0

    fun fetchRepositories(since: Int = currentPage, perPage: Int) {
        viewModelScope.launch {
            _repositories.value = ApiState.Loading
            when (val result = gitHubRepo.getPublicRepositories(since, perPage)) {
                is ApiState.Success<*> -> {
                    _repositories.value = ApiState.Success(result.data)
                    currentPage = since + perPage
                }

                is ApiState.Failure -> {
                    _repositories.value = ApiState.Failure(result.message)
                }

                else -> {}
            }
        }
    }
}