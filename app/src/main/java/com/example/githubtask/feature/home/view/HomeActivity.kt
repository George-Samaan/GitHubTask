package com.example.githubtask.feature.home.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private var isLoading = false
    private var lastVisibleItem = 0
    private val perPage = 50

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        setupRecyclerView()
        observeRepositories()

        homeViewModel.fetchRepositories(perPage = perPage)
    }

    private fun initViewModel() {
        homeViewModel = HomeViewModel(GithubRepositoryImpl(RetrofitClient.api))
    }

    private fun setupRecyclerView() {
        binding.rvRepos.layoutManager = LinearLayoutManager(this)
        repositoryAdapter = RepositoryAdapter(mutableListOf())
        binding.rvRepos.adapter = repositoryAdapter

        binding.rvRepos.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                handlePagination()
            }
        })
    }

    private fun handlePagination() {
        val layoutManager = binding.rvRepos.layoutManager as LinearLayoutManager
        val totalItemCount = layoutManager.itemCount
        val visibleItemCount = layoutManager.childCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
            isLoading = true
            if (firstVisibleItemPosition > lastVisibleItem) {
                lastVisibleItem = firstVisibleItemPosition
                homeViewModel.fetchRepositories(perPage = perPage)
            }
        }
    }

    private fun observeRepositories() {
        lifecycleScope.launch {
            homeViewModel.repositories.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        Log.d("HomeActivity", "Loading...")
                        showLoading(true)
                    }

                    is ApiState.Success<*> -> {
                        showLoading(false)
                        handleSuccess(state)
                    }

                    is ApiState.Failure -> {
                        showLoading(false)
                        Log.d("HomeActivity", "Failure: ${state.message}")
                        handleError(message = state.message)
                    }
                }
                isLoading = false
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleSuccess(state: ApiState.Success<*>) {
        Log.d("HomeActivity", "Success: ${state.data}")
        if (state.data is List<*>) {
            @Suppress("UNCHECKED_CAST")
            repositoryAdapter.addRepositories(state.data as List<Repository>)
        }
    }

    private fun handleError(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Retry") { dialog, _ ->
                homeViewModel.fetchRepositories(perPage = perPage)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}