package com.storyapp.data.repository

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.storyapp.data.ResultState
import com.storyapp.data.pref.UserPreference
import com.storyapp.data.remote.response.CommonResponse
import com.storyapp.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository private constructor(private val userPreference: UserPreference, private val apiService: ApiService){

    fun getAllStories() = liveData {
        emit(ResultState.Loading)
        try {
            val storiesResponse = apiService.getStories()
            emit(ResultState.Success(storiesResponse.listStory))
        } catch (e: HttpException) {
            emit(ResultState.Error(e.code().toString()))
        }
    }

    fun getStoriesWithLocation() = liveData {
        emit(ResultState.Loading)
        try {
            val storiesResponse = apiService.getStoriesWithLocation()
            emit(ResultState.Success(storiesResponse.listStory))
        } catch (e: HttpException) {
            emit(ResultState.Error(e.code().toString()))
        }
    }

    fun getDetailStory(storyId: String) = liveData {
        emit(ResultState.Loading)
        try {
            val detailStory = apiService.getDetailStory(storyId)
            emit(ResultState.Success(detailStory.story))
        } catch (e: HttpException) {
            val errorResponse = parsingErrorBody(e)
            emit(ResultState.Error(errorResponse.message))
        }
    }

    fun uploadStory(fileUpload: MultipartBody.Part, description: RequestBody) = liveData {
        emit(ResultState.Loading)
        try {
            val uploadedStory = apiService.uploadImage(fileUpload, description)
            emit(ResultState.Success(uploadedStory))
        } catch (e: HttpException) {
            val errorResponse = parsingErrorBody(e)
            emit(ResultState.Error(errorResponse.message))
        }
    }

    fun uploadStoryWithLocation(fileUpload: MultipartBody.Part, description: RequestBody, latitude: RequestBody, longitude: RequestBody) = liveData {
        emit(ResultState.Loading)
        try {
            val uploadedStory = apiService.uploadImageWithLocation(fileUpload, description, latitude, longitude)
            emit(ResultState.Success(uploadedStory))
        } catch (e: HttpException) {
            val errorResponse = parsingErrorBody(e)
            emit(ResultState.Error(errorResponse.message))
        }
    }

    suspend fun logout() {
        userPreference.logout()
    }


    private fun parsingErrorBody(e: HttpException): CommonResponse {
        val errorBody = e.response()?.errorBody()?.string()
        return Gson().fromJson(errorBody, CommonResponse::class.java)
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(userPreference: UserPreference, apiService: ApiService): StoryRepository = instance ?: synchronized(this) {
            instance ?: StoryRepository(userPreference, apiService)
        }.also { instance = it }
    }
}