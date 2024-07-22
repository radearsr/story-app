package com.storyapp.ui.main.maps

import androidx.lifecycle.ViewModel
import com.storyapp.data.repository.StoryRepository

class MapsViewModel(private val repository: StoryRepository): ViewModel() {
    fun getStoryWithLocation() = repository.getStoriesWithLocation()
}