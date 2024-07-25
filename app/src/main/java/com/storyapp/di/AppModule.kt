package com.storyapp.di

import com.storyapp.ui.auth.AuthViewModel
import com.storyapp.ui.main.MainViewModel
import com.storyapp.ui.main.create.CreateStoryViewModel
import com.storyapp.ui.main.detail.DetailStoryViewModel
import com.storyapp.ui.main.maps.MapsViewModel
import com.storyapp.ui.welcome.SplashScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SplashScreenViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { DetailStoryViewModel(get()) }
    viewModel { CreateStoryViewModel(get()) }
    viewModel { MapsViewModel(get()) }
    viewModel { AuthViewModel(get()) }
}
