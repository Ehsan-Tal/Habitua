package com.example.habitua.data

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.habitua.R


@Entity(tableName = "habits")
data class Habit (
    @PrimaryKey(autoGenerate = true)
    val habitId: Int = 0,
    @DrawableRes val imageResId : Int = R.drawable.tal_derpy,
    val dateCreated: Long = 0,
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

// future documentation once I've an idea of the final entity class for v2.
// @property daysTillAcquisition: float might be replaced by "repetitionsTillAcquisition"
// since frequency matters more, and it would help calculations maybe
// we currently assume it - and we can add days if missed and dropped - just solve for x

enum class Complexity { SIMPLE, MEDIUM, COMPLEX }

@Entity( tableName = "habits_dates")
data class HabitDate (
    @PrimaryKey(autoGenerate = true)
    val dateId: Int = 0,
    val date: Long,
    val habitId: Int,
    var value: Boolean = false
)

data class HabitDetails (
    @DrawableRes val imageResId : Int = R.drawable.tal_derpy,
    val dateCreated: Long = 0,
    var name: String,
    var description: String,
    var complexity: Complexity = Complexity.SIMPLE,
    var frequency: Int = 1,
    var isActive: Boolean = false,
    var currentStreakOrigin: Long? = null,
    var nextReviewedDate: Long? = null,
    var dateAcquired: Long? = null,
    var daysUntilAcquisition: Float? = null,
    val date: Long,
    val habitId: Int,
    var value: Boolean = false
) //TODO: do we copy Principles or just ughhhhhhhhhhh
// we ought to complete convert this, I do hate it.

@Entity(tableName = "principles")
data class Principle (
    @PrimaryKey(autoGenerate = true)
    val principleId: Int = 0,
    val dateCreated: Long = 0,
    var dateFirstActive: Long? = null,
    var name: String,
    var description: String
)

@Entity( tableName = "principles_dates" )
data class PrincipleDate (
    @PrimaryKey(autoGenerate = true)
    val dateId: Int = 0,
    val date: Long,
    val principleId: Int,
    var value: Boolean = false
)

data class PrincipleDetails (
    val principleId: Int,
    val name: String,
    val description: String,
    val date: Long,
    val dateCreated: Long = 0,
    var dateFirstActive: Long? = null,
    val value: Boolean
)//TODO: change the PrincipleDetails so that it's val principle and val value

fun PrincipleDetails.toPrinciple(): Principle {
    return Principle(
        principleId = principleId,
        name = name,
        dateCreated = dateCreated,
        description = description,
        dateFirstActive = dateFirstActive
    )
}