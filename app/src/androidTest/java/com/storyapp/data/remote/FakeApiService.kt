package com.storyapp.data.remote

import android.util.Log
import com.google.gson.Gson
import com.storyapp.data.remote.response.CommonResponse
import com.storyapp.data.remote.response.DetailStoryResponse
import com.storyapp.data.remote.response.StoryItemResponse
import com.storyapp.data.remote.response.LoginResponse
import com.storyapp.data.remote.response.LoginResultResponse
import com.storyapp.data.remote.response.StoryResponse
import com.storyapp.data.remote.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response


class FakeApiService : ApiService {
    override suspend fun postRegister(name: String, email: String, password: String) {}

    override suspend fun postLogin(email: String, password: String): LoginResponse {
        if (email == "megumichan@test.kyun" && password == "iloveyou") {
            return LoginResponse(LoginResultResponse("Kato Megumi Chan", "kato-megumi-chan", "fake-token"), "success")
        } else {
            val errorResponse = CommonResponse("Invalid email or password")
            val errorResponseJson = Gson().toJson(errorResponse)
            val errorResponseBody = errorResponseJson.toResponseBody("application/json".toMediaTypeOrNull())
            throw HttpException(Response.error<LoginResponse>(401, errorResponseBody))
        }
    }

    override suspend fun getStories(page: Int, size: Int): StoryResponse {
        val items: MutableList<StoryItemResponse> = arrayListOf()
        for (i in 0..100) {
            val story = StoryItemResponse(
                i.toString(),
                "http://test.com/$i",
                name = "Story $i",
                description = "This is story description $i",
                createdAt = "2023-10-30 10:10:1$i",
                null,
                null
            )
            items.add(story)
        }
        return  StoryResponse(items.subList((page - 1) * size, (page - 1) * size + size), "Success get stories")
    }

    override suspend fun getStoriesWithLocation(location: Int): StoryResponse {
        return  StoryResponse(emptyList(), "Success get stories with location")
    }

    override suspend fun getDetailStory(storyId: String): DetailStoryResponse {
        return  DetailStoryResponse("Success get stories", StoryItemResponse(
            "story-xxx",
            "http://test.com",
            name = "Story",
            description = "This is story description",
            createdAt = "2023-10-30 10:10:10",
            null,
            null
        ))
    }

    override suspend fun uploadImage(
        file: MultipartBody.Part,
        description: RequestBody
    ): CommonResponse {
        return CommonResponse("Success upload image")
    }

    override suspend fun uploadImageWithLocation(
        file: MultipartBody.Part,
        description: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody
    ): CommonResponse {
        return CommonResponse("Success upload image with location")
    }

}