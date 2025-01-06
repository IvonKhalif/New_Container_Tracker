package com.example.containertracker

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.example.containertracker.utils.PreferenceUtils
import com.example.containertracker.utils.modules.NetworkModule
import com.example.containertracker.utils.modules.RepositoryModule
import com.example.containertracker.utils.modules.UseCaseModule
import com.example.containertracker.utils.modules.ViewModelModule
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@App)
            modules(
                listOf(
                    ViewModelModule,
                    UseCaseModule,
                    NetworkModule,
                    RepositoryModule
                )
            )
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        AndroidThreeTen.init(this)
        PreferenceUtils.with(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}