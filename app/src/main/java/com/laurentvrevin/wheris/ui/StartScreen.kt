package com.laurentvrevin.wheris.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.laurentvrevin.wheris.R
import com.laurentvrevin.wheris.core.designsystem.components.button.AppSecondaryButton
import com.laurentvrevin.wheris.core.designsystem.theme.AppTheme

@Composable
fun StartScreen(
    onNavigateToShowcase: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppTheme.colors.background,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(AppTheme.spacing.standard),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.start_welcome),
                style = AppTheme.typography.display,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.large))

            Text(
                text = stringResource(R.string.start_description),
                style = AppTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = AppTheme.colors.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.tripleLarge))

            AppSecondaryButton(
                text = stringResource(R.string.start_btn_showcase),
                onClick = onNavigateToShowcase,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
