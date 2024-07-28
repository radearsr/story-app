package com.storyapp.data.pref

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update


class FakeUserPreference: IUserPreference {
    private val dataStore = MutableStateFlow(emptyPreferences())

    override suspend fun saveSession(user: UserModel) {
        dataStore.update { preferences ->
            preferences.toMutablePreferences().apply {
                this[USER_ID_KEY] = user.userId
                this[NAME_KEY] = user.name
                this[TOKEN_KEY] = user.token
                this[IS_LOGIN_KEY] = true
            }.toPreferences()
        }
    }

    override fun getSession(): Flow<UserModel> {
        return dataStore.map { preferences ->
            UserModel(
                userId = preferences[USER_ID_KEY] ?: "",
                name = preferences[NAME_KEY] ?: "",
                token = preferences[TOKEN_KEY] ?: "",
                isLogin = preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }

    override fun getUser(): Flow<String> {
        return dataStore.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    override suspend fun logout() {
        dataStore.update { emptyPreferences() }
    }

    companion object {
        private val USER_ID_KEY = stringPreferencesKey("userId")
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
    }
}