package com.storyapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository): ViewModel() {
    fun getStories() = repository.getAllStories()
    fun logoutSession() = viewModelScope.launch {
        repository.logout()
    }
}