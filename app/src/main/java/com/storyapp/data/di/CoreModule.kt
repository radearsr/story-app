package com.storyapp.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.storyapp.BuildConfig
import com.storyapp.data.local.StoryDatabase
import com.storyapp.data.pref.IUserPreference
import com.storyapp.data.pref.UserPreference
import com.storyapp.data.remote.retrofit.ApiService
import com.storyapp.data.repository.StoryRepository
import com.storyapp.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

val dataStoreModule = module {
    single { get<Context>().dataStore  }
}

val userPreferencesModule = module {
    single<IUserPreference> { UserPreference(get()) }
}

val databaseModule = module {
    factory { get<StoryDatabase>().storyDao() }
    single { Room.databaseBuilder(
        androidContext(),
        StoryDatabase::class.java,
        "Story.db"
    ).fallbackToDestructiveMigration().build() }
}

val networkModule = module {
    single { provideOkHttpClient(get()) }
    single { provideRetrofit(get()) }
    single { provideApiService(get()) }
    single { provideAuthInterceptor(get()) }
}

val repositoryModule = module {
    single { UserRepository(get(), get(), androidContext()) }
    single { StoryRepository(get(), get(), get()) }
}

fun provideAuthInterceptor(userPreference: IUserPreference): Interceptor {
    return Interceptor { chain ->
        val token = runBlocking { userPreference.getUser().first() }
        val req = chain.request()
        val requestHeaders = req.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        chain.proceed(requestHeaders)
    }
}

fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().setLevel(
        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    )
    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .build()
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
}

fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)