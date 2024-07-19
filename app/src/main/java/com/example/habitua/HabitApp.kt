package com.example.habitua

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.habitua.ui.navigation.HabitNavHost


/**
 * Main composable for the app
 */
@Composable
fun HabitApp(navController: NavHostController = rememberNavController()){
    HabitNavHost(navController = navController)
}

//TODO: change to a top bar
@Composable
fun HabitBottomAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {}
    // is there a scrollBehavior for bottom App bar ?
){
    BottomAppBar{
        if(canNavigateBack) {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.content_description_navigate_back)
                )
                Text(
                    title
                )
            }
        }
    }

}


// I think it'd be fun to share habits, there should be a button to take all the habits and share.
private fun shareHabitTracking(context: Context, subject: String, habits: String) {
    // createHabitsString() <- requires this

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        // we need to make a string of it
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, habits)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.new_habit_receipt)
        )
    )
}
