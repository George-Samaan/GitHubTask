package com.example.githubtask.data.network

sealed class ApiState {
    class Success(val data: Any) : ApiState()
    class Failure(val message: String) : ApiState()
    object Loading : ApiState()
}