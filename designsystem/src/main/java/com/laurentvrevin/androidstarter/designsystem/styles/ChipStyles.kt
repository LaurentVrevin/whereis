@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.laurentvrevin.androidstarter.designsystem.styles

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.*
import androidx.compose.runtime.Composable
import com.laurentvrevin.androidstarter.designsystem.foundation.AppSize
import com.laurentvrevin.androidstarter.designsystem.theme.AppTheme

/**
 * Interface définissant les styles disponibles pour les chips.
 */
interface ChipStyles {
    @Composable fun default(size: AppSize): Style
}

/**
 * Implémentation par défaut des styles de chips.
 */
class DefaultChipStyles : ChipStyles {
    @Composable
    override fun default(size: AppSize): Style {
        val colors = AppTheme.colors
        val spacing = AppTheme.spacing
        val dimensions = AppTheme.dimensions
        val typography = AppTheme.typography

        val height =
            when (size) {
                AppSize.Small -> dimensions.chip.heightSmall
                AppSize.Medium, AppSize.Large -> dimensions.chip.heightMedium
            }

        return Style {
            shape(CircleShape)
            height(height)
            background(colors.surfaceVariant)
            contentPadding(horizontal = spacing.medium, vertical = spacing.extraSmall)
            textStyle(typography.labelMedium)

            selected {
                background(colors.primaryContainer)
            }
        }
    }
}
