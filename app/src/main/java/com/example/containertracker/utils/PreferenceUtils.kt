package com.example.containertracker.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.containertracker.App
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object PreferenceUtils {
    private const val EDITOR_NAME = "ContainerPreferences"
    const val USER_PREFERENCE = "USER_PREFERENCE"
    const val LOCATION_LIST_PREFERENCE = "LOCATION_LIST_PREFERENCE"

    lateinit var preferences: SharedPreferences

    fun with(application: App) {
        preferences = application.getSharedPreferences(
            EDITOR_NAME, Context.MODE_PRIVATE)
    }

    fun <T> put(`object`: T, key: String) {
        //Convert object to JSON String.
        val jsonString = GsonBuilder().create().toJson(`object`)
        //Save that String in SharedPreferences
        preferences.edit().putString(key, jsonString).apply()
    }

    inline fun <reified T> get(key: String): T? {
        //We read JSON String which was saved.
        val value = preferences.getString(key, null)
        //JSON String was found which means object can be read.
        //We convert this JSON String to model object. Parameter "c" (of
        //type Class < T >" is used to cast.
        return GsonBuilder().create().fromJson(value, T::class.java)
    }
}