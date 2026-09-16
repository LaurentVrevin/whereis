@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.laurentvrevin.androidstarter.designsystem.styles

import androidx.compose.foundation.style.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.laurentvrevin.androidstarter.designsystem.foundation.AppSize
import com.laurentvrevin.androidstarter.designsystem.theme.AppTheme

/**
 * Interface définissant les styles disponibles pour les boutons.
 */
interface ButtonStyles {
    @Composable fun primary(size: AppSize): Style

    @Composable fun secondary(size: AppSize): Style

    @Composable fun outlined(size: AppSize): Style

    @Composable fun ghost(size: AppSize): Style

    @Composable fun danger(size: AppSize): Style
}

/**
 * Implémentation par défaut des styles de boutons pour le design system.
 */
class DefaultButtonStyles : ButtonStyles {
    @Composable
    @ReadOnlyComposable
    private fun base(size: AppSize): Style {
        val shapes = AppTheme.shapes
        val spacing = AppTheme.spacing
        val dimensions = AppTheme.dimensions
        val typography = AppTheme.typography

        val height =
            when (size) {
                AppSize.Small -> dimensions.button.heightSmall
                AppSize.Medium -> dimensions.button.heightMedium
                AppSize.Large -> dimensions.button.heightLarge
            }

        val horizontalPadding =
            when (size) {
                AppSize.Small -> spacing.medium
                AppSize.Medium -> spacing.standard
                AppSize.Large -> spacing.large
            }

        val verticalPadding =
            when (size) {
                AppSize.Small -> spacing.extraSmall
                AppSize.Medium -> spacing.small
                AppSize.Large -> spacing.medium
            }

        return Style {
            shape(shapes.medium)
            height(height)
            contentPadding(horizontal = horizontalPadding, vertical = verticalPadding)
            textStyle(typography.labelMedium)
        }
    }

    @Composable
    override fun primary(size: AppSize): Style {
        val colors = AppTheme.colors
        return base(size) then
            Style {
                background(colors.primary)
                contentColor(colors.onPrimary)

                pressed {
                    animate {
                        background(colors.primary.copy(alpha = 0.8f))
                    }
                }
            }
    }

    @Composable
    override fun secondary(size: AppSize): Style {
        val colors = AppTheme.colors
        return base(size) then
            Style {
                background(colors.primaryContainer)
                contentColor(colors.primary)
            }
    }

    @Composable
    override fun outlined(size: AppSize): Style {
        val colors = AppTheme.colors
        val borders = AppTheme.borders
        return base(size) then
            Style {
                border(borders.thin, colors.outline)
                background(Color.Transparent)
                contentColor(colors.primary)
            }
    }

    @Composable
    override fun ghost(size: AppSize): Style {
        val colors = AppTheme.colors
        return base(size) then
            Style {
                background(Color.Transparent)
                contentColor(colors.primary)
            }
    }

    @Composable
    override fun danger(size: AppSize): Style {
        val colors = AppTheme.colors
        return base(size) then
            Style {
                background(colors.error)
                contentColor(colors.onError)
            }
    }
}
