package com.laurentvrevin.wheris.feature.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.laurentvrevin.wheris.core.model.PinId
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

    HomeScreen(
        uiState = uiState,
        onAddPlace = onAddPlace,
        onSavedPlaceSelected = viewModel::onSavedPlaceSelected,
        onDetailsClick = onPinClick,
        onNavigateClick = {
            uiState.selectedPin?.let { pin ->
                val uri =
                    Uri.parse(
                        "geo:${pin.position.latitude},${pin.position.longitude}?q=${pin.position.latitude},${pin.position.longitude}",
                    )
                val intent = Intent(Intent.ACTION_VIEW, uri)
                try {
                    context.startActivity(intent)
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.home_navigation_not_found),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        },
        modifier = modifier,
    )
}
