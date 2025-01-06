package com.example.containertracker.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.preference.PreferenceManager
import java.util.*

object LocaleHelper {
    private const val LOCALE_HELPER_SELECTED_LANGUAGE = "LOCALE_HELPER_SELECTED_LANGUAGE"
    const val IN = "in"
    const val ISO_3166_ID = "id"
    const val EN = "en"

    fun onAttach(context: Context): Context {
        val systemLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0).language
        } else context.resources.configuration.locale.language
        val appLanguage = retrieveAppLanguage(context, systemLanguage)
        return setLocale(context, appLanguage)
    }

    fun retrieveAppLanguage(context: Context, defaultLanguage: String): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(LOCALE_HELPER_SELECTED_LANGUAGE, defaultLanguage)
            ?: defaultLanguage
    }

    fun setLocale(context: Context, language: String): Context {
        storeAppLanguage(context, language)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else {
            updateResourcesLegacy(context, language)
        }
    }

    private fun storeAppLanguage(context: Context, language: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString(LOCALE_HELPER_SELECTED_LANGUAGE, language)
        editor.apply()
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)

        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        configuration.setLocales(localeList)

        return context.createConfigurationContext(configuration)
    }

    @Suppress("DEPRECATION")
    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources

        val configuration = resources.configuration
        configuration.locale = locale
        configuration.setLayoutDirection(locale)

        resources.updateConfiguration(configuration, resources.displayMetrics)

        return context
    }
}