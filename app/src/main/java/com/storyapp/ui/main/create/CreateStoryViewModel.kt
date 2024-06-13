package com.storyapp.ui.main.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateStoryViewModel: ViewModel() {
    private val _currentImage = MutableLiveData<Uri>()
    val currentImage: LiveData<Uri> = _currentImage

    fun setCurrentImage(image: Uri) {
        _currentImage.value = image
    }
}