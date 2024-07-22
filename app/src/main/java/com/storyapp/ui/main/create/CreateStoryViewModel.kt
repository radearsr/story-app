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
        latitude: Double?,
        longitude: Double?
    ): LiveData<ResultState<CommonResponse>> {
        val reqBodyDescription = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val reqBodyLatitude = latitude.toString().toRequestBody("text/plain".toMediaType())
        val reqBodyLongitude = longitude.toString().toRequestBody("text/plain".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)
        return if (latitude == null && longitude == null) {
            repository.uploadStory(multipartBody, reqBodyDescription)
        } else {
            repository.uploadStoryWithLocation(multipartBody, reqBodyDescription, reqBodyLatitude, reqBodyLongitude)
        }
    }
}