package com.storyapp.di

import android.content.Context
import android.util.Log
import com.storyapp.data.pref.UserPreference
import com.storyapp.data.pref.dataStore
import com.storyapp.data.remote.retrofit.ApiConfig
import com.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getUser().first() }
        Log.i("Injection", "USER TOKEN = $user")
        val apiService = ApiConfig.getApiService(user)
        return StoryRepository.getInstance(pref, apiService)
    }
}