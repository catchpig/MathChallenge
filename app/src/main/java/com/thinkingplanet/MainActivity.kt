package com.thinkingplanet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.thinkingplanet.ui.navigation.AppNavigation
import com.thinkingplanet.ui.theme.ThinkingPlanetTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThinkingPlanetTheme {
                AppNavigation()
            }
        }
    }
}
