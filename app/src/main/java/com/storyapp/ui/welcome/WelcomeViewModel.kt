package com.storyapp.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.storyapp.data.repository.StoryRepository

class WelcomeViewModel(private val repository: StoryRepository): ViewModel() {
    fun currentSession() = repository.getSession().asLiveData()
}