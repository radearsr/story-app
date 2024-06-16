package com.storyapp.di

import android.content.Context
import android.util.Log
import com.storyapp.data.pref.UserPreference
import com.storyapp.data.pref.dataStore
import com.storyapp.data.remote.retrofit.ApiConfig
import com.storyapp.data.repository.StoryRepository
import com.storyapp.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val userToken = runBlocking { pref.getUser().first() }
        Log.i("Injection", "USER TOKEN = $userToken")
        val apiService = ApiConfig.getApiService(userToken)
        return StoryRepository.getInstance(pref, apiService)
    }

    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService("")
        return UserRepository.getInstance(pref, apiService)
    }
}