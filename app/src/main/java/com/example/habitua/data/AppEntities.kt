package com.example.habitua.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.text.intl.Locale
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.habitua.R
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.text.format


/**
 * Represents a Habit entity in the database.
 *
 * @property id The unique identifier for the habit (auto-generated).
 * @property imageURI holds empty, emoji, or image URI
 * @property name
 * @property description
 * @property isActive where the user has done it for within its frequency
 * @property complexity the quantity of steps inherent in the activity
 * @property frequency whether its daily, bi-daily, or weekly.
 * @property currentStreakOrigin origin or start date of the current
 * @property nextReviewedDate   the next date the habit must be active on
 * @property dateAcquired has been fully acquired (goal achieved) ?
 * @property daysUntilAcquisition  calculated days until habit is acquired
 */

//TODO - date convert
@Entity(tableName = "habits")
data class Habit (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var imageURI: String = "",
    var name: String,
    var description: String,
    var complexity: Complexity = Complexity.SIMPLE,
    var frequency: Int = 1,
    var isActive: Boolean = false,
    var currentStreakOrigin: Long? = null,
    var nextReviewedDate: Long? = null,
    var dateAcquired: Long? = null,
    var daysUntilAcquisition: Float? = null,
)
// I don't like this. Type conversion is nonsense

// future documentation once I've an idea of the final entity class for v2.
// @property daysTillAcquisition: float might be replaced by "repetitionsTillAcquisition"
// since frequency matters more, and it would help calculations maybe
// we currently assume it - and we can add days if missed and dropped - just solve for x

enum class Complexity {
SIMPLE, MEDIUM, COMPLEX
}

/*
Consider using OffsetDateTime for better timezone handling, especially if your app deals with dates from different timezones.
For complex date operations, consider using a dedicated date-time library like Joda-Time or ThreeTenABP.
Ensure you handle potential parsing errors in dateToTimestamp.
This approach stores dates as longs, allowing efficient date range queries.
The SimpleDateFormat used here is not thread-safe. Consider using a thread-safe alternative if your app performs date conversions on multiple threads.
 */

@Entity(tableName = "principles")
data class Principle (
    @PrimaryKey(autoGenerate = true)
    val principleId: Int = 0,
    var name: String,
    var description: String
)

//TODO - date convert
@Entity(tableName = "principles_dates")
data class PrincipleDate (
    @PrimaryKey(autoGenerate = true)
    val dateId: Int = 0,
    val principleId: Int,
    var date: Long,
    var value: Boolean = false
)