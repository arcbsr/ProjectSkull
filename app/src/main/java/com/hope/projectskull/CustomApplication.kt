package com.hope.projectskull
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Automatically switch between Light and Dark Mode based on the system's preference
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
