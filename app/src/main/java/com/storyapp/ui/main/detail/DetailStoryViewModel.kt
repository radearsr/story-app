package com.storyapp.ui.main.detail

import androidx.lifecycle.ViewModel
import com.storyapp.data.repository.StoryRepository

class DetailStoryViewModel(private val repository: StoryRepository): ViewModel() {
    fun getDetailStory(storyId: String) = repository.getDetailStory(storyId)
}