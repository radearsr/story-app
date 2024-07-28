package com.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.storyapp.data.local.model.Story
import com.storyapp.data.remote.response.StoryItemResponse
import com.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository): ViewModel() {
    val stories: LiveData<PagingData<Story>> = repository.getAllStories().cachedIn(viewModelScope)
    fun logoutSession() = viewModelScope.launch {
        repository.logout()
    }
}