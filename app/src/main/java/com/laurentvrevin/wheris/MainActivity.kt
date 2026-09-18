package com.laurentvrevin.wheris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.laurentvrevin.wheris.data.local.ThemeMode
import com.laurentvrevin.wheris.designsystem.theme.AppTheme
import com.laurentvrevin.wheris.navigation.AppNavHost
import com.laurentvrevin.wheris.ui.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val isDarkTheme =
                when (themeMode) {
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                    ThemeMode.SYSTEM -> isSystemInDarkTheme()
                }

            AppTheme(darkTheme = isDarkTheme) {
                AppNavHost(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = {
                        val newMode = if (isDarkTheme) ThemeMode.LIGHT else ThemeMode.DARK
                        viewModel.setThemeMode(newMode)
                    },
                )
            }
        }
    }
}
