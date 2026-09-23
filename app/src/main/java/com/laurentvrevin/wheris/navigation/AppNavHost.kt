package com.laurentvrevin.wheris.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.laurentvrevin.wheris.feature.addpin.AddPinRoute
import com.laurentvrevin.wheris.ui.StartScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = "add_pin",
        modifier = modifier,
    ) {
        composable("start") {
            StartScreen()
        }
        composable("add_pin") {
            AddPinRoute()
        }
    }
}
