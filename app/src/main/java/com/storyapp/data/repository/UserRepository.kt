package com.storyapp.data.repository

import android.content.Context
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.storyapp.R
import com.storyapp.data.ResultState
import com.storyapp.data.pref.IUserPreference
import com.storyapp.data.pref.UserModel
import com.storyapp.data.remote.response.CommonResponse
import com.storyapp.data.remote.retrofit.ApiService
import com.storyapp.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class UserRepository(private val userPreference: IUserPreference, private val apiService: ApiService, private val context: Context) {
    fun login(email: String, password: String) = liveData {
        emit(ResultState.Loading)
        wrapEspressoIdlingResource {
            try {
                val loginResponse = apiService.postLogin(email, password)
                val (name, userId, token) = loginResponse.loginResult
                val userLoggedIn = UserModel(
                    userId = userId,
                    token = token,
                    name = name,
                    isLogin = true
                )
                userPreference.saveSession(userLoggedIn)
                emit(ResultState.Success(loginResponse))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, CommonResponse::class.java)
                emit(ResultState.Error(errorResponse.message))
            } catch (e: SocketTimeoutException) {
                emit(ResultState.Error(context.getString(R.string.txt_network_timeout)))
            } catch (e: IOException) {
                emit(ResultState.Error(context.getString(R.string.txt_network_error)))
            } catch (e: Exception) {
                emit(ResultState.Error(context.getString(R.string.txt_unexpected_error)))
            }
        }
    }

    fun register(name: String, email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val registerResponse = apiService.postRegister(name, email, password)
            emit(ResultState.Success(registerResponse))
        } catch (e: HttpException) {
            val errorResponse = parsingErrorBody(e)
            emit(ResultState.Error(errorResponse.message))
        }
    }

    private fun parsingErrorBody(e: HttpException): CommonResponse {
        val errorBody = e.response()?.errorBody()?.string()
        return Gson().fromJson(errorBody, CommonResponse::class.java)
    }


    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }
}