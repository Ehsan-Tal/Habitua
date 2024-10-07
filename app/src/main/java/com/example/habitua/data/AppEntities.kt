package com.example.habitua.data

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.habitua.R


@Entity(tableName = "habits")
data class Habit (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
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

enum class Complexity {
SIMPLE, MEDIUM, COMPLEX
}

@Entity(tableName = "principles")
data class Principle (
    @PrimaryKey(autoGenerate = true)
    val principleId: Int = 0,
    val dateCreated: Long = 0,
    var dateFirstActive: Long? = null,
    var name: String,
    var description: String
)

@Entity(
    tableName = "principles_dates",
    foreignKeys = [
        ForeignKey(
            entity = Principle::class,
            parentColumns = ["principleId"],
            childColumns = ["principleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
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
)

fun PrincipleDetails.toPrinciple(): Principle {
    return Principle(
        principleId = principleId,
        name = name,
        description = description,
        dateFirstActive = dateFirstActive
    )
}