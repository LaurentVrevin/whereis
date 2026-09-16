@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.laurentvrevin.androidstarter.designsystem.styles

import androidx.compose.foundation.style.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.laurentvrevin.androidstarter.designsystem.theme.AppTheme

/**
 * Interface définissant les styles pour le top bar.
 */
interface TopBarStyles {
    @Composable fun default(): Style
}

/**
 * Implémentation par défaut des styles de top bar.
 */
class DefaultTopBarStyles : TopBarStyles {
    @Composable
    override fun default(): Style {
        val spacing = AppTheme.spacing
        val colors = AppTheme.colors

        return Style {
            background(colors.surface)
            contentPadding(horizontal = 0.dp, vertical = spacing.medium)
            fillWidth()
        }
    }
}
