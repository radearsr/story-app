package com.storyapp.di

import android.content.Context
import com.storyapp.data.pref.UserPreference
import com.storyapp.data.pref.dataStore
import com.storyapp.data.remote.retrofit.ApiConfig
import com.storyapp.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getUser().first() }
        val apiService = ApiConfig.getApiService(user)
        return UserRepository.getInstance(pref, apiService)
    }
}