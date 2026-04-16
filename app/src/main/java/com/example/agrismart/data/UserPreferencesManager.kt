package com.example.agrismart.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manages user preferences using DataStore.
 * Enhanced for Precision Farming features.
 */
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferencesManager(private val context: Context) {

    companion object {
        val NAME_KEY = stringPreferencesKey("user_name")
        val SOIL_TYPE_KEY = stringPreferencesKey("soil_type")
        val LOCATION_KEY = stringPreferencesKey("location")
        val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val LAND_SIZE_KEY = doublePreferencesKey("land_size")
        val CURRENT_CROP_KEY = stringPreferencesKey("current_crop")
        val SOWING_DATE_KEY = stringPreferencesKey("sowing_date")
    }

    // Get the user data as a Flow
    val userFlow: Flow<User> = context.dataStore.data.map { preferences ->
        User(
            name = preferences[NAME_KEY] ?: "",
            soilType = preferences[SOIL_TYPE_KEY] ?: "Loamy",
            location = preferences[LOCATION_KEY] ?: "",
            isLoggedIn = preferences[IS_LOGGED_IN_KEY] ?: false,
            language = preferences[LANGUAGE_KEY] ?: "en",
            landSize = preferences[LAND_SIZE_KEY] ?: 0.0,
            currentCrop = preferences[CURRENT_CROP_KEY] ?: "None",
            sowingDate = preferences[SOWING_DATE_KEY] ?: ""
        )
    }

    // Save the user data
    suspend fun saveUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.name
            preferences[SOIL_TYPE_KEY] = user.soilType
            preferences[LOCATION_KEY] = user.location
            preferences[IS_LOGGED_IN_KEY] = user.isLoggedIn
            preferences[LANGUAGE_KEY] = user.language
            preferences[LAND_SIZE_KEY] = user.landSize
            preferences[CURRENT_CROP_KEY] = user.currentCrop
            preferences[SOWING_DATE_KEY] = user.sowingDate
        }
    }

    // Clear user data (for logout)
    suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
