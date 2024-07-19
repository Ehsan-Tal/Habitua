package com.example.habitua

import android.app.Application
import com.example.habitua.data.AppContainer
import com.example.habitua.data.AppDataContainer
/**
 * Application class for Habitua
 */
class HabitApplication: Application() {

    // so, we instance this so that the rest can obtain dependencies
    // black box
    // I think this thing doesn't work
    // very complicated - will learn in 3-5 business months

    /**
     * Container for the app
     */
    lateinit var container: AppContainer

    /**
     * Called when the application is starting, before any other application objects have been created.
     */
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)

    }

}