package com.apc.revenuerise.dataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension to create a DataStore
val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        private val USER_KEY = intPreferencesKey("user_id")
    }

    // Save user
    suspend fun saveUser(user: Int) {
        context.dataStore.edit { preferences ->
            preferences[USER_KEY] = user
        }
    }

    // Retrieve user
    val user: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[USER_KEY]
    }

    // Clear user
    suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_KEY)
        }
    }
}
