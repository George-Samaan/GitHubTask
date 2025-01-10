@file:Suppress("DEPRECATION")

package com.example.githubtask.feature.login.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.githubtask.R
import com.example.githubtask.data.network.ApiState
import com.example.githubtask.data.network.FirebaseAuthDataImpl
import com.example.githubtask.data.repository.AuthRepositoryImpl
import com.example.githubtask.databinding.ActivityLoginBinding
import com.example.githubtask.feature.login.viewModel.LoginViewModel
import com.example.githubtask.feature.register.view.RegisterActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private val rcSignIn = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel =
            LoginViewModel(
                AuthRepositoryImpl(
                    FirebaseAuthDataImpl(
                        FirebaseAuth.getInstance()
                    )
                )
            )
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        observeLoginState()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnSignIn.setOnClickListener {
            val email = binding.emailLoginEditTetx.text.toString()
            val password = binding.passwordLoginEditText.text.toString()
            viewModel.login(email, password)
        }

        binding.txtRegister.setOnClickListener {
            navigateToRegisterScreen()
        }

        binding.signInGoogleButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, rcSignIn)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rcSignIn) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                viewModel.googleLogin(idToken)
                navigateToHomeScreen()
            } else {
                showError("Google Sign-In failed: ID Token is null")
            }
        } catch (e: ApiException) {
            showError("Google Sign-In failed: ${e.message}")
        }
    }

    private fun navigateToRegisterScreen() {
        // Implement navigation to register screen
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun observeLoginState() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is ApiState.Loading -> {}
                    is ApiState.Success -> navigateToHomeScreen()
                    is ApiState.Failure -> showError(state.message)
                }
            }
        }
    }

    private fun showLoading() {
        binding.btnSignIn.startAnimation()
    }

    private fun navigateToHomeScreen() {
        binding.btnSignIn.stopAnimation()
    }

    private fun showError(message: String) {
        binding.btnSignIn.revertAnimation()
        binding.signInAlert.text = message
    }
}