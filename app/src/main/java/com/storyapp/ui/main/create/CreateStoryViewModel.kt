package com.storyapp.ui.main.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.storyapp.data.ResultState
import com.storyapp.data.remote.response.CommonResponse
import com.storyapp.data.repository.StoryRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CreateStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _currentImage = MutableLiveData<Uri>()
    val currentImage: LiveData<Uri> = _currentImage

    fun setCurrentImage(image: Uri) {
        _currentImage.value = image
    }

    fun getCurrentImage(): Uri? {
        return _currentImage.value
    }

    fun uploadStory(
        imageFile: File,
        description: String,
        isGuestMode: Boolean
    ): LiveData<ResultState<CommonResponse>> {
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody =
            MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)
        return if (isGuestMode) {
            repository.uploadStoryGuest(multipartBody, requestBody)
        } else {
            repository.uploadStory(multipartBody, requestBody)
        }
    }
}