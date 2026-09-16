package com.laurentvrevin.androidstarter.designsystem.components.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.laurentvrevin.androidstarter.designsystem.theme.AppTheme

@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val spacing = AppTheme.spacing
    val typography = AppTheme.typography

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(spacing.doubleLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            icon()
            Spacer(Modifier.height(spacing.standard))
        }

        Text(
            text = message,
            style = typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = AppTheme.colors.onSurfaceVariant,
        )

        if (action != null) {
            Spacer(Modifier.height(spacing.standard))
            action()
        }
    }
}
