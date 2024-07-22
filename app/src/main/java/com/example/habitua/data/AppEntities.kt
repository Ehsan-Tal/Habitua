package com.example.habitua.data

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.habitua.R


/**
 * Represents a Habit entity in the database.
 *
 * @property id The unique identifier for the habit (auto-generated).
 * @property imageResId The resource ID of the image associated with the habit.
 * @property name The name of the habit.
 * @property description A description of the habit.
 * @property isActive Whether the habit is currently active (being tracked).
 * @property hasMissedOpportunity Indicates if the user has missed an opportunity to perform the
 * habit.
 * @property hasBeenAcquired Whether the habit has been fully acquired (goal achieved).
 * @property currentStreakOrigin A string representing the origin or start date of the current
 * streak (optional). Is in the form `yyyy-MM-dd`
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
    var currentStreakOrigin: String? = null,
)
//TODO: currentAcquiredOrigin: String? = null

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

