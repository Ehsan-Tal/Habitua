package com.example.habitua.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.habitua.R
import java.util.Date

/**
 * A data class to represent the information presented in the habit card
 */
data class Habit(
    val id: Int = 0, // need some way to auto increment
    @DrawableRes val imageResourceId: Int = R.drawable.tal_derpy,
    val name: String = "",
    val description: String = "",
    var currentStreakOrigin: Long? = null,
    var isActive: Boolean = false,
    var isExpanded: Boolean = false,
    var hasMissableOpportunity: Boolean = true,
    var isAcquired: Boolean = false
)

/**
 *
 * this should be converted into a UIState item, this file is for the Entities now.
 * data class Habit(
 *     val id: Int = 0, // need some way to auto increment
 *     @DrawableRes val imageResourceId: Int = R.drawable.tal_derpy,
 *     val name: String = "",
 *     val description: String = "",
 *     var currentStreakOrigin: Long? = null,
 *     var isActive: Boolean = false,
 *     var isExpanded: Boolean = false,
 *     var hasMissableOpportunity: Boolean = true,
 *     var isAcquired: Boolean = false
 * )
 *
 *
 */