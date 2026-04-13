package com.example.agrismart.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manages user preferences using DataStore.
 * This stores basic info like name, soil type, and login status.
 */
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferencesManager(private val context: Context) {

    companion object {
        val NAME_KEY = stringPreferencesKey("user_name")
        val SOIL_TYPE_KEY = stringPreferencesKey("soil_type")
        val LOCATION_KEY = stringPreferencesKey("location")
        val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }

    // Get the user data as a Flow
    val userFlow: Flow<User> = context.dataStore.data.map { preferences ->
        User(
            name = preferences[NAME_KEY] ?: "",
            soilType = preferences[SOIL_TYPE_KEY] ?: "Loamy",
            location = preferences[LOCATION_KEY] ?: "",
            isLoggedIn = preferences[IS_LOGGED_IN_KEY] ?: false
        )
    }

    // Save the user data
    suspend fun saveUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.name
            preferences[SOIL_TYPE_KEY] = user.soilType
            preferences[LOCATION_KEY] = user.location
            preferences[IS_LOGGED_IN_KEY] = user.isLoggedIn
        }
    }

    // Clear user data (for logout)
    suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
