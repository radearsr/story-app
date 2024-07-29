package com.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.storyapp.data.ResultState
import com.storyapp.data.local.StoryDatabase
import com.storyapp.data.local.model.Story
import com.storyapp.data.paging.StoryRemoteMediator
import com.storyapp.data.pref.IUserPreference
import com.storyapp.data.remote.response.CommonResponse
import com.storyapp.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository(private val userPreference: IUserPreference, private val apiService: ApiService, private val storyDatabase: StoryDatabase) {

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStories(): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
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
}