package com.storyapp.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.storyapp.data.repository.StoryRepository
import com.storyapp.data.repository.UserRepository

class WelcomeViewModel(private val repository: UserRepository): ViewModel() {
    fun currentSession() = repository.getSession().asLiveData()
}