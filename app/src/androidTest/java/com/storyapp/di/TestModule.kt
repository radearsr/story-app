package com.storyapp.di

import com.storyapp.FakeApiService
import com.storyapp.FakeUserPreference
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
    single { UserRepository(get(), get()) }
    single { StoryRepository(get(), get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { MainViewModel(get()) }
}