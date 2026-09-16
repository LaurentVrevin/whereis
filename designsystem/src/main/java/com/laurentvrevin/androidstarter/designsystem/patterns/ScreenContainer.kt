package com.laurentvrevin.androidstarter.designsystem.patterns

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.laurentvrevin.androidstarter.designsystem.theme.AppTheme

/**
 * Conteneur de base pour tous les écrans.
 * Garantit :
 * - padding cohérent
 * - spacing vertical homogène
 * - fond correct
 */
@Composable
fun ScreenContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = AppTheme.spacing

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = spacing.standard, vertical = spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.large),
        content = content,
    )
}
