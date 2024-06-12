package com.storyapp.ui.auth

import androidx.lifecycle.ViewModel
import com.storyapp.data.repository.StoryRepository
import com.storyapp.data.repository.UserRepository

class AuthViewModel(private val userRepository: UserRepository): ViewModel() {
    fun loginAction(email: String, password: String) = userRepository.login(email, password)
    fun registerAction(name: String, email: String, password: String) = userRepository.register(name, email, password)
}