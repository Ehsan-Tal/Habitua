package com.example.habitua.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val appRepository: AppRepository
}

/**
 * [AppContainer] implementation that provides instance of [AppRepositoryImplementation]
 */
class AppDataContainer(private val context: Context): AppContainer {

    // according to the google app dev training course,
    // [AppContainer] implementation provides the instance
    // of the implementation of AppRepository
    // so... the constructor cum provider ?
    // I'm continuing to see this as a blackbox until it makes sense
    // for now, it's use case stair case
    // apparently this is enough for the DB ? Okay.
    override val appRepository: AppRepository by lazy {
        AppRepositoryImplementation(HabitDatabase.getDatabase(context).habitDao())
    }
}