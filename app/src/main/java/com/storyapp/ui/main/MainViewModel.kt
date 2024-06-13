package com.storyapp.ui.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.storyapp.data.repository.StoryRepository

class MainViewModel(private val repository: StoryRepository): ViewModel() {
    fun getStories() = repository.getAllStories()
}