package com.storyapp.data.repository

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.storyapp.data.ResultState
import com.storyapp.data.pref.UserModel
import com.storyapp.data.pref.UserPreference
import com.storyapp.data.remote.response.CommonResponse
import com.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class StoryRepository private constructor(private val userPreference: UserPreference, private val apiService: ApiService){


    fun getAllStories() = liveData {
        emit(ResultState.Loading)
        try {
            val storiesResponse = apiService.getStories()
            emit(ResultState.Success(storiesResponse.listStory))
        } catch (e: HttpException) {
            val errorResponse = parsingErrorBody(e)
            emit(ResultState.Error(errorResponse.message))
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