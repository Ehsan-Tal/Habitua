package com.example.habitua.data

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.habitua.R


/**
 * A data class to represent the information presented in the habit card
 */

@Entity(tableName = "habits")
data class Habit (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @DrawableRes var imageResId: Int = R.drawable.tal_derpy,
    var name: String,
    var description: String,
    var isActive: Boolean = false,
    var hasMissedOpportunity: Boolean = true,
    var hasBeenAcquired: Boolean = false,
    var currentStreakOrigin: Long? = null, // only changes in the review Habit
    // var currentStreakOrigin exists in the ViewModel
    //      and from the Streaks query
    // var isExpanded exists in the ViewModel
)
//TODO: Streak entities

/**
 *
 *
 * @Entity(tableName = "",
 *         foreignKeys = arrayOf(
 *             ForeignKey(entity = Habit::class,
 *                 parentColumns = arrayOf("id"),
 *                 childColumns = arrayOf("habitId"))))
 *                 #CASCADE
 * data class Streaks (
 *     @PrimaryKey(autoGenerate = true)
 *     val id: Int = 0,
 *     val habitId:Int,
 *     val origin: Long,
 *     val end: Long? = null,
 * )
 */

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