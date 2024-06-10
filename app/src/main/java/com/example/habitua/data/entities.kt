package com.example.habitua.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Habit (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateCreated: Long,
    var name: String = "",
    var condition: String = "",
    var currentStreakOrigin: Long? = null,
    var isActive: Boolean = false,
    var hasMissedOpportunity: Boolean = false,
    var isAcquired: Boolean = false
)

@Entity(foreignKeys = [ForeignKey(
    entity = Habit::class,
    parentColumns = ["id"],
    childColumns = ["habitId"],
    onDelete = ForeignKey.CASCADE
)])
data class StreakLogs (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: Int,
    var startDate: Long,
    var endDate: Long? = null
)

@Entity(foreignKeys = [ForeignKey(
    entity = Habit::class,
    parentColumns = ["id"],
    childColumns = ["habitId"],
    onDelete = ForeignKey.CASCADE
)])
data class ConditionLogs (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: Int,
    var condition: String = "",
    var startDate: Long
)
