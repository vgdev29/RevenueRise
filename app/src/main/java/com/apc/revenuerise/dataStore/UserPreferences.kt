package com.apc.revenuerise.dataStore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension to create a DataStore
val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        private val USER_KEY = stringPreferencesKey("user_name")
    }

    // Save user
    suspend fun saveUser(user: String) {
        Log.d("UserPreferences", "Saving user: $user")

        context.dataStore.edit { preferences ->
            preferences[USER_KEY] = user
        }
    }

    // Retrieve user
    val user: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_KEY]
    }

    // Clear user
    suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_KEY)
        }
    }
}
