package com.storyapp.ui.auth

import androidx.lifecycle.ViewModel
import com.storyapp.data.repository.UserRepository

class AuthViewModel(private val repository: UserRepository): ViewModel() {
    fun loginAction(email: String, password: String) = repository.login(email, password)
    fun registerAction(name: String, email: String, password: String) = repository.register(name, email, password)
}