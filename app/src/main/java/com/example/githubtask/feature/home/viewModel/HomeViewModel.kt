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

    fun fetchRepositories(since: Int, perPage: Int) {
        viewModelScope.launch {
            _repositories.value = ApiState.Loading
            _repositories.value = gitHubRepo.getPublicRepositories(since, perPage)
        }
    }
}