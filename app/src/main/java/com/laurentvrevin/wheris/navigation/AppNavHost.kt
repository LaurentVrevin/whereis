package com.laurentvrevin.wheris.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.laurentvrevin.wheris.designsystem.ShowcaseScreen
import com.laurentvrevin.wheris.feature.template.presentation.TemplateScreen
import com.laurentvrevin.wheris.ui.StartScreen
import kotlinx.serialization.Serializable

@Serializable
data object StartRoute

@Serializable
data object ShowcaseRoute

@Serializable
data object TemplateRoute

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = StartRoute,
        modifier = modifier,
    ) {
        composable<StartRoute> {
            StartScreen(
                onNavigateToShowcase = { navController.navigate(ShowcaseRoute) },
                onNavigateToTemplate = { navController.navigate(TemplateRoute) },
            )
        }

        composable<ShowcaseRoute> {
            ShowcaseScreen(
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onBackClick = { navController.popBackStack() },
            )
        }

        composable<TemplateRoute> {
            TemplateScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
