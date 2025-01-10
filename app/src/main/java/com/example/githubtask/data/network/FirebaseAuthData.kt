package com.example.githubtask.data.network

interface FirebaseAuthData {
    suspend fun loginWithEmail(email: String, password: String): ApiState
    suspend fun loginWithGoogle(idToken: String): ApiState
    suspend fun registerUser(email: String, password: String): ApiState
}