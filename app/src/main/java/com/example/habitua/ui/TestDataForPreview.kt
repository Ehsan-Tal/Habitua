package com.example.habitua.ui

import com.example.habitua.R
import com.example.habitua.data.Habit
import com.example.habitua.data.Principle
import com.example.habitua.data.PrincipleDetails
import com.example.habitua.ui.habit.Painting
import java.time.Instant
import java.time.temporal.ChronoUnit


const val December8th2001_inMilli = 1007769600000

const val December31st2001_inMilli = 1009756800000

const val February12th2001_inMilli = 1007769600000

val todayMinus66days_inMilli = Instant.now().minus(66, ChronoUnit.DAYS).toEpochMilli()
val todayMinus24days_inMilli = Instant.now().minus(24, ChronoUnit.DAYS).toEpochMilli()
val todayMinus3day_inMilli = Instant.now().minus(3, ChronoUnit.DAYS).toEpochMilli()
val todayMinus2day_inMilli = Instant.now().minus(2, ChronoUnit.DAYS).toEpochMilli()
val todayMinus1day_inMilli = Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli()
val today_inMilli = Instant.now().toEpochMilli()
val todayPlus24days_inMilli = Instant.now().plus(24, ChronoUnit.DAYS).toEpochMilli()
val todayPlus66Days_inMilli = Instant.now().plus(66, ChronoUnit.DAYS).toEpochMilli()

val test_data_HabitList = listOf(
    Habit(
        name = "Glove Chopping",
        description = "With a snippers"
    ),
    Habit(
        name = "Pouring one Out",
        description = "sample text",
        dateCreated = todayMinus1day_inMilli,
        currentStreakOrigin = todayMinus1day_inMilli,
        nextReviewedDate = today_inMilli

    ),
    Habit(
        name = "Diisononyl phthalate",
        description = "get grapes",
        isActive = true,
        dateCreated = todayMinus3day_inMilli,
        currentStreakOrigin = todayMinus2day_inMilli,
        nextReviewedDate = today_inMilli
    ),
    Habit(
        name = "Reviewing a Movie",
        description = "Spoilers for Alien Romulus",
        isActive = true,
        dateCreated = todayMinus24days_inMilli,
        currentStreakOrigin = todayMinus24days_inMilli,
        nextReviewedDate = todayMinus1day_inMilli
    ),
    Habit(
        name = "Ghost hunting",
        description = "800 dollar ovilius",
        dateCreated = todayMinus66days_inMilli,
        dateAcquired = today_inMilli

    )
)

val test_data_iconList: List<Painting> = listOf(
    Painting(R.drawable.tal_derpy, "Tal the cat having a derp face"),
    Painting(R.drawable.ic_launcher_foreground, "Tal the cat having a derp face"),
    Painting(R.drawable.tal_derpy, "Tal the cat having a derp face"),
    Painting(R.drawable.tal_derpy, "Tal the cat having a derp face"),
)

val test_data_PrincipleList = listOf(
    Principle(
        name = "Readiness",
        description = "minus 66",
        dateCreated = todayMinus2day_inMilli,
    ),
    Principle(
        name = "Industry",
        description = "minus 24",
        dateCreated = todayMinus1day_inMilli,
    ),
    Principle(
        name = "Minus 2",
        description = "Minus 2 days",
        dateCreated = todayMinus3day_inMilli,
    ),
) // I took away the other ones, and now it doubled Industry.,..

val test_data_principleListToday = listOf(
    PrincipleDetails(
        principleId = 0,
        name = "Readiness",
        description = "",
        date = today_inMilli,
        dateCreated = todayMinus66days_inMilli,
        dateFirstActive = null,
        value = false
    ),
    PrincipleDetails(
        principleId = 1,
        name = "Industry",
        description = "",
        date = today_inMilli,
        dateCreated = todayMinus24days_inMilli,
        dateFirstActive = null,
        value = true
    ),
    PrincipleDetails(
        principleId = 2,
        name = "Temperance",
        description = "",
        date = today_inMilli,
        dateCreated = todayMinus66days_inMilli,
        dateFirstActive = null,
        value = false
    )
)

//TODO: once we can have the issue details, we cna be happy

val test_data_issueListTodo: List<PrincipleDetails> = listOf(

)