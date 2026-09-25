package com.laurentvrevin.wheris.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.laurentvrevin.wheris.R
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.feature.addpin.AddPinRoute
import com.laurentvrevin.wheris.feature.home.HomeRoute
import com.laurentvrevin.wheris.feature.pindetail.PinDetailRoute
import com.laurentvrevin.wheris.feature.pins.PinsRoute

private const val ROUTE_HOME = "home"
private const val ROUTE_PINS = "pins"
private const val ROUTE_ADD_PIN = "add_pin"
private const val ROUTE_PIN_DETAIL = "pin_detail"
private const val ARG_PIN_ID = "pinId"
private const val ROUTE_PIN_DETAIL_PATTERN = "$ROUTE_PIN_DETAIL/{$ARG_PIN_ID}"

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showPrimaryNavigation = currentRoute == ROUTE_HOME || currentRoute == ROUTE_PINS

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showPrimaryNavigation) {
                PrimaryNavigationBar(
                    currentRoute = currentRoute,
                    onMap = { navController.navigateTopLevel(ROUTE_HOME) },
                    onPlaces = { navController.navigateTopLevel(ROUTE_PINS) },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(ROUTE_HOME) {
                HomeRoute(
                    onAddPlace = { navController.navigate(ROUTE_ADD_PIN) },
                )
            }

            composable(ROUTE_PINS) {
                PinsRoute(
                    onPinClick = { pinId ->
                        navController.navigate("$ROUTE_PIN_DETAIL/${pinId.value}")
                    },
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

            composable(
                route = ROUTE_PIN_DETAIL_PATTERN,
                arguments = listOf(navArgument(ARG_PIN_ID) { type = NavType.StringType }),
            ) { entry ->
                val pinIdValue = requireNotNull(entry.arguments?.getString(ARG_PIN_ID))
                PinDetailRoute(
                    pinId = PinId(pinIdValue),
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun PrimaryNavigationBar(
    currentRoute: String?,
    onMap: () -> Unit,
    onPlaces: () -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == ROUTE_HOME,
            onClick = onMap,
            icon = {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                )
            },
            label = { Text(stringResource(R.string.nav_map)) },
        )
        NavigationBarItem(
            selected = currentRoute == ROUTE_PINS,
            onClick = onPlaces,
            icon = {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                )
            },
            label = { Text(stringResource(R.string.nav_places)) },
        )
    }
}

private fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(ROUTE_HOME) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
