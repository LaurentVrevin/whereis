@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.laurentvrevin.androidstarter.designsystem.styles

import androidx.compose.foundation.style.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.laurentvrevin.androidstarter.designsystem.foundation.appPadding
import com.laurentvrevin.androidstarter.designsystem.theme.AppTheme

/**
 * Interface définissant les styles disponibles pour les cards.
 */
interface CardStyles {
    @Composable fun default(): Style

    @Composable fun outlined(): Style

    @Composable fun elevated(): Style
}

/**
 * Implémentation par défaut des styles de cards.
 */
class DefaultCardStyles : CardStyles {
    @Composable
    override fun default(): Style {
        val shapes = AppTheme.shapes
        val colors = AppTheme.colors
        val elevation = AppTheme.elevation
        val spacing = AppTheme.spacing
        return Style {
            shape(shapes.large)
            background(colors.surface)
            dropShadow(
                Shadow(
                    radius = elevation.level1,
                    color = colors.scrim.copy(alpha = 0.1f),
                    offset = DpOffset(0.dp, 2.dp),
                ),
            )
            appPadding(spacing)
        }
    }

    @Composable
    override fun outlined(): Style {
        val colors = AppTheme.colors
        val borders = AppTheme.borders
        return default() then
            Style {
                border(borders.thin, colors.outline)
                dropShadow(Shadow(radius = 0.dp, color = Color.Transparent))
            }
    }

    @Composable
    override fun elevated(): Style {
        val colors = AppTheme.colors
        val elevation = AppTheme.elevation
        return default() then
            Style {
                dropShadow(
                    Shadow(
                        radius = elevation.level2,
                        color = colors.scrim.copy(alpha = 0.15f),
                        offset = DpOffset(0.dp, 4.dp),
                    ),
                )
            }
    }
}
