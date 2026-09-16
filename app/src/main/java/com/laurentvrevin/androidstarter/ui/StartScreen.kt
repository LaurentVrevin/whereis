package com.laurentvrevin.androidstarter.ui

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
import com.laurentvrevin.androidstarter.R
import com.laurentvrevin.androidstarter.designsystem.components.button.AppPrimaryButton
import com.laurentvrevin.androidstarter.designsystem.components.button.AppSecondaryButton
import com.laurentvrevin.androidstarter.designsystem.theme.AppTheme

@Composable
fun StartScreen(
    onNavigateToShowcase: () -> Unit,
    onNavigateToTemplate: () -> Unit,
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

            AppPrimaryButton(
                text = stringResource(R.string.start_btn_template),
                onClick = onNavigateToTemplate,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.standard))

            AppSecondaryButton(
                text = stringResource(R.string.start_btn_showcase),
                onClick = onNavigateToShowcase,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
