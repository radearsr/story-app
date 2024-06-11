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

class UserRepository private constructor(private val userPreference: UserPreference, private val apiService: ApiService){
    fun login(email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val loginResponse = apiService.postLogin(email, password)
            val userLoggedIn = UserModel(
                userId = loginResponse.loginResult.userId,
                token = loginResponse.loginResult.token,
                name = loginResponse.loginResult.name,
                isLogin = true
            )
            userPreference.saveSession(userLoggedIn)
            emit(ResultState.Success(loginResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, CommonResponse::class.java)
            emit(ResultState.Error(errorResponse.message))
        }
    }

    fun register(name: String, email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val registerResponse = apiService.postRegister(name, email, password)
            emit(ResultState.Success(registerResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, CommonResponse::class.java)
            emit(ResultState.Error(errorResponse.message))
        }
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(userPreference: UserPreference, apiService: ApiService): UserRepository = instance ?: synchronized(this) {
            instance ?: UserRepository(userPreference, apiService)
        }.also { instance = it }
    }
}