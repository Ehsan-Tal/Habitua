package com.example.habitua

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.habitua.ui.theme.HabituaTheme
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    // we need to declare this in the mainActivity
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        //TODO: keep this empty and this should instead be somewhere else
        //E.g., in a settings screen button
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // requesting notification permission
        // TODO: maybe keep this enablement in a button in the settings screen
        //this and private val exist to request notification privileges.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        enableEdgeToEdge()
        setContent {
            HabituaTheme {
                Surface (
                    modifier = Modifier
                        .fillMaxSize()
                ){
                    HabitApp()
                }
            }
        }
    }

}

/// this is kept empty - the navigation is in HabitApp (HabitApp.kt)
// the individual pages are kept in .ui/





/*
// these localisation implementations keep forcing the SDK upwards.
//For setOverrideLocaleConfig
val localeManager = applicationContext
    .getSystemService(LocaleManager::class.java)
localeManager.overrideLocaleConfig = LocaleConfig(
    LocaleList.forLanguageTags("en-US,ja-JP,zh-Hans-SG")
)

//For getOverrideLocaleConfig
// The app calls the API to get the override LocaleConfig
val overrideLocaleConfig = localeManager.overrideLocaleConfig
// If the returned overrideLocaleConfig isn't equal to NULL, then the app calls the API to get the supported Locales
val supportedLocales = overrideLocaleConfig.supportedLocales()

// now we need the current locale of our app
*/
