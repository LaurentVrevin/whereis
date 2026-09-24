package com.laurentvrevin.wheris.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.laurentvrevin.wheris.feature.addpin.AddPinRoute
import com.laurentvrevin.wheris.feature.home.HomeRoute

private const val ROUTE_HOME = "home"
private const val ROUTE_ADD_PIN = "add_pin"

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_HOME,
        modifier = modifier,
    ) {
        composable(ROUTE_HOME) {
            HomeRoute(
                onAddPlace = { navController.navigate(ROUTE_ADD_PIN) },
            )
        }
        composable(ROUTE_ADD_PIN) {
            AddPinRoute(
                onFinished = {
                    navController.popBackStack(
                        route = ROUTE_HOME,
                        inclusive = false,
                    )
                },
            )
        }
    }
}
