package com.storyapp.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.storyapp.data.ResultState
import com.storyapp.databinding.ActivityLoginBinding
import com.storyapp.ui.ViewModelFactory
import com.storyapp.ui.auth.AuthViewModel
import com.storyapp.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private val authViewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.edLoginEmail.text
        val password = binding.edLoginPassword.text

        binding.btnLogin.setOnClickListener {
            Log.i(TAG, "USERNAME = $username || PASSWORD = $password")
            loginSetup(username.toString(), password.toString())
        }
    }

    private fun loginSetup(email: String, password: String) {
        authViewModel.loginAction(email, password).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        Log.d(TAG, "Loading State")
                    }
                    is ResultState.Success -> {
                        Log.d(TAG, "Success State ${result.data}")
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    is ResultState.Error -> {
                        Log.e(TAG, "Error State ${result.error}")
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
    }
}