package com.storyapp.ui.auth.register

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.storyapp.data.ResultState
import com.storyapp.databinding.ActivityRegisterBinding
import com.storyapp.ui.UserViewModelFactory
import com.storyapp.ui.auth.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel by viewModels<AuthViewModel> {
        UserViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {

            btnRegister.setOnClickListener {
                val name = edRegisterName.text.toString()
                val email = edLoginEmail.text.toString()
                val password = edLoginPassword.text.toString()
                Log.i(TAG, "NAME = $name || EMAIL = $email || PASSWORD = $password")
                registerSetup(name, email, password)
            }
        }
    }

    private fun registerSetup(name: String, email: String, password: String) {
        authViewModel.registerAction(name, email, password).observe(this) { result ->
            if (result != null) {
                when(result) {
                    is ResultState.Loading -> {
                        Log.d(TAG, "Register Loading State")
                    }
                    is ResultState.Success -> {
                        Log.d(TAG, "Register Success State")
                    }
                    is ResultState.Error -> {
                        Log.d(TAG, "Register Error State")
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = RegisterActivity::class.java.simpleName
    }
}