package com.storyapp.data.pref

import kotlinx.coroutines.flow.Flow

interface IUserPreference {
    suspend fun saveSession(user: UserModel)
    fun getSession(): Flow<UserModel>
    fun getUser(): Flow<String>
    suspend fun logout()
}