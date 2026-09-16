package com.laurentvrevin.androidstarter.designsystem.components.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.laurentvrevin.androidstarter.designsystem.theme.AppTheme

@Composable
fun LoadingOverlay(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
) {
    if (isLoading) {
        Box(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(AppTheme.colors.scrim.copy(alpha = 0.3f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                        // Bloquer les clics
                    ),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = AppTheme.colors.primary)
        }
    }
}
