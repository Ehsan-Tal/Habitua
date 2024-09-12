package com.example.habitua.data

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
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
// future documentation once I've an idea of the final entity class for v2.
// @property daysTillAcquisition: float might be replaced by "repetitionsTillAcquisition"
// since frequency matters more, and it would help calculations maybe
// we currently assume it - and we can add days if missed and dropped - just solve for x

/**
// Frequency and complexity will need type converters because they're enums
enum class Frequency {
DAILY, BIDAILY, WEEKLY
}
enum class Complexity {
SIMPLE, MEDIUM, COMPLEX
}

//TODO: ADD CONVERTERS TO THE DATABASE.
//this -> @TypeConverters(Converters::class)

class Converters {
    @TypeConverter
    fun toFrequency(value: Int) = enumValues<Frequency>()[value]

    @TypeConverter
    fun fromFrequency(value: Frequency) = value.ordinal

    @TypeConverter
    fun toComplexity(value: Int) = enumValues<Complexity>()[value]

    @TypeConverter
    fun fromComplexity(value: Complexity) = value.ordinal
}

*/

/**

*/
/**
 * Represents a Habit entity in the database.
 *
 * @property id The unique identifier for the habit (auto-generated).
 * @property imageURI holds empty, emoji, or image URI
 * @property name
 * @property description
 * @property complexity the quantity of steps inherent in the activity
 * @property frequency whether its daily, bi-daily, or weekly.
 * @property isActive where the user has done it for within its frequency
 * @property currentStreakOrigin origin or start date of the current
 * @property hasMissedOpportunity can leave inactive for next review without dropping the streak
 * @property firstMissedDate first missed date to be used for calculating streak dropping
 * @property daysTillAcquisition  calculated days until habit is acquired
 * @property hasBeenAcquired has been fully acquired (goal achieved) ?
 */

/**
@Entity(tableName = "habits")
data class Habit (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var imageURI: String = "",
    var name: String,
    var description: String,
    var complexity: Complexity,
    var frequency: Frequency,
    var isActive: Boolean = false,
    var currentStreakOrigin: String? = null,
    var hasMissedOpportunity: Boolean = true,
    var firstMissedDate: String? = null,
    var daysTillAcquisition: Float? = null,
    var hasBeenAcquired: Boolean = false,

)
 */