package com.example.githubtask.feature.home.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubtask.data.model.Repository
import com.example.githubtask.data.network.ApiState
import com.example.githubtask.data.network.RetrofitClient
import com.example.githubtask.data.repository.GithubRepositoryImpl
import com.example.githubtask.databinding.ActivityHomeBinding
import com.example.githubtask.feature.home.viewModel.HomeViewModel
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var repositoryAdapter: RepositoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeViewModel = HomeViewModel(
            GithubRepositoryImpl(RetrofitClient.api)
        )

        binding.rvRepos.layoutManager = LinearLayoutManager(this)


        lifecycleScope.launch {
            homeViewModel.repositories.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        Log.d("HomeActivity", "Loading...")
                    }

                    is ApiState.Success -> {
                        Log.d("HomeActivity", "Success: ${state.data}")
                        setupRecyclerView(state.data as List<Repository>)
                    }

                    is ApiState.Failure -> {
                        Log.d("HomeActivity", "Failure: ${state.message}")
                    }
                }

            }
        }
        homeViewModel.fetchRepositories(
            since = 0,
            perPage = 10
        )
    }

    private fun setupRecyclerView(repositories: List<Repository>) {
        repositoryAdapter = RepositoryAdapter(repositories)
        binding.rvRepos.adapter = repositoryAdapter
    }
}