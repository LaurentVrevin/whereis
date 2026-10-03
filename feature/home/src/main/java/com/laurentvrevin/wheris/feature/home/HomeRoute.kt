package com.laurentvrevin.wheris.feature.home

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.core.navigation.AndroidExternalNavigator
import com.laurentvrevin.wheris.core.navigation.message
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute(
    onAddPlace: () -> Unit,
    onPinClick: (PinId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val navigator = remember(context) { AndroidExternalNavigator(context) }

    HomeScreen(
        uiState = uiState,
        onAddPlace = onAddPlace,
        onSavedPlaceSelected = viewModel::onSavedPlaceSelected,
        onMapClick = viewModel::onMapSelectionCleared,
        onDetailsClick = onPinClick,
        onNavigateClick = {
            uiState.selectedPin?.let { pin ->
                navigator.navigate(pin.position).message(context)?.let { message ->
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
        },
        modifier = modifier,
    )
}
