package com.storyapp

import android.app.Application
import com.storyapp.data.di.dataStoreModule
import com.storyapp.data.di.networkModule
import com.storyapp.data.di.repositoryModule
import com.storyapp.data.di.userPreferencesModule
import com.storyapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(
                dataStoreModule,
                userPreferencesModule,
                networkModule,
                repositoryModule,
                viewModelModule,
            )
        }
    }
}