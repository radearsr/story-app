package com.storyapp.di

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.storyapp.data.local.StoryDatabase
import com.storyapp.data.remote.FakeApiService
import com.storyapp.data.pref.FakeUserPreference
import com.storyapp.data.pref.IUserPreference
import com.storyapp.data.remote.retrofit.ApiService
import com.storyapp.data.repository.StoryRepository
import com.storyapp.data.repository.UserRepository
import com.storyapp.ui.auth.AuthViewModel
import com.storyapp.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val testModule = module {
    single<IUserPreference> { FakeUserPreference() }
    single<ApiService> { FakeApiService() }
    single<StoryDatabase> { Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build() }
    single { UserRepository(get(), get()) }
    single { StoryRepository(get(), get(), get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { MainViewModel(get()) }
}