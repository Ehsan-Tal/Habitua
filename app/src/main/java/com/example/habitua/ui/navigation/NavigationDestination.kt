package com.example.habitua.ui.navigation

import com.example.habitua.R


/**
 * A navigation destination.
 * route is a String
 * title is a temp String
 */
interface NavigationDestination {
    val route: String
    val title: Int
}

object PrincipleDestination: NavigationDestination {
    override val route = "principle"
    override val title = R.string.principle_title
    val navTitle = R.string.principle_nav_title
}
object HabitDestination : NavigationDestination {
    override val route = "home"
    override val title = R.string.habit_title
    val navTitle = R.string.habit_nav_title
}
object IssueDestination: NavigationDestination {
    override val route = "issue"
    override val title = R.string.issue_title
    val navTitle = R.string.issue_nav_title
}
object SettingDestination: NavigationDestination {
    override val route = "settings"
    override val title = R.string.setting_title
    val navTitle = R.string.setting_nav_title
}


object HabitEntryDestination : NavigationDestination {
    override val route = "habit_entry"
    override val title =  R.string.habit_entry_title
} //TODO: delete this
object HabitEditDestination: NavigationDestination{
    override val route = "habit_edit"
    override val title = R.string.habit_edit_title
    const val ID_ARG = "habitId"
    val routeWithArgs = "$route/{$ID_ARG}"
}
object PrincipleEditDestination: NavigationDestination{
    override val route = "principle_edit"
    override val title = R.string.principle_edit_title
    const val ID_ARG = "principleId"
    val routeWithArgs = "$route/{$ID_ARG}"
}

