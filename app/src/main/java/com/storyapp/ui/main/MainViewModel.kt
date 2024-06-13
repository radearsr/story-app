package com.storyapp.ui.main

import androidx.lifecycle.ViewModel
import com.storyapp.data.repository.StoryRepository

class MainViewModel(private val repository: StoryRepository): ViewModel() {
    fun getStories() = repository.getAllStories()
    fun getDetailStory(storyId: String) = repository.getDetailStory(storyId)
}