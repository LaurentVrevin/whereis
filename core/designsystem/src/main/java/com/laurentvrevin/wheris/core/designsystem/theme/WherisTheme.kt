package com.laurentvrevin.wheris.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import com.laurentvrevin.wheris.core.designsystem.foundation.LocalWherisBorders
import com.laurentvrevin.wheris.core.designsystem.foundation.LocalWherisDimensions
import com.laurentvrevin.wheris.core.designsystem.foundation.LocalWherisElevation
import com.laurentvrevin.wheris.core.designsystem.foundation.LocalWherisShapes
import com.laurentvrevin.wheris.core.designsystem.foundation.LocalWherisSpacing
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisBorders
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisDimensions
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisElevation
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisShapes
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisSpacing

private val WherisLightColorScheme =
    lightColorScheme(
        primary = WherisLightColors.actionPrimary,
        onPrimary = WherisLightColors.textOnPrimary,
        primaryContainer = WherisLightColors.actionPrimarySoft,
        onPrimaryContainer = WherisLightColors.textPrimary,
        secondary = WherisLightColors.actionPrimaryEmphasis,
        onSecondary = WherisLightColors.textOnPrimary,
        background = WherisLightColors.background,
        onBackground = WherisLightColors.textPrimary,
        surface = WherisLightColors.surface,
        onSurface = WherisLightColors.textPrimary,
        surfaceVariant = WherisLightColors.surfaceSubtle,
        onSurfaceVariant = WherisLightColors.textSecondary,
        outline = WherisLightColors.borderDefault,
        error = WherisLightColors.statusError,
    )

private val WherisDarkColorScheme =
    darkColorScheme(
        primary = WherisDarkColors.actionPrimary,
        onPrimary = WherisDarkColors.textOnPrimary,
        primaryContainer = WherisDarkColors.actionPrimarySoft,
        onPrimaryContainer = WherisDarkColors.textPrimary,
        secondary = WherisDarkColors.actionPrimaryEmphasis,
        onSecondary = WherisDarkColors.textOnPrimary,
        background = WherisDarkColors.background,
        onBackground = WherisDarkColors.textPrimary,
        surface = WherisDarkColors.surface,
        onSurface = WherisDarkColors.textPrimary,
        surfaceVariant = WherisDarkColors.surfaceSubtle,
        onSurfaceVariant = WherisDarkColors.textSecondary,
        outline = WherisDarkColors.borderDefault,
        error = WherisDarkColors.statusError,
    )

@Composable
fun WherisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val spacing = WherisSpacing()
    val shapes = WherisShapes()
    val elevation = WherisElevation()
    val borders = WherisBorders()
    val dimensions = WherisDimensions()
    val typography = WherisTypography()

    val materialTypography =
        Typography(
            displaySmall = typography.displaySmall,
            headlineLarge = typography.headlineLarge,
            headlineMedium = typography.headlineMedium,
            headlineSmall = typography.headlineSmall,
            titleLarge = typography.titleLarge,
            titleMedium = typography.titleMedium,
            titleSmall = typography.titleSmall,
            bodyLarge = typography.bodyLarge,
            bodyMedium = typography.bodyMedium,
            bodySmall = typography.bodySmall,
            labelLarge = typography.labelLarge,
            labelMedium = typography.labelMedium,
            labelSmall = typography.labelSmall,
        )

    val materialShapes =
        Shapes(
            extraSmall = shapes.extraSmall,
            small = shapes.small,
            medium = shapes.medium,
            large = shapes.large,
            extraLarge = shapes.extraLarge,
        )

    CompositionLocalProvider(
        LocalWherisSpacing provides spacing,
        LocalWherisShapes provides shapes,
        LocalWherisElevation provides elevation,
        LocalWherisBorders provides borders,
        LocalWherisDimensions provides dimensions,
        LocalWherisTypography provides typography,
    ) {
        MaterialTheme(
            colorScheme =
                if (darkTheme) {
                    WherisDarkColorScheme
                } else {
                    WherisLightColorScheme
                },
            typography = materialTypography,
            shapes = materialShapes,
            content = content,
        )
    }
}

object WherisTheme {
    val spacing: WherisSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalWherisSpacing.current

    val shapes: WherisShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalWherisShapes.current

    val elevation: WherisElevation
        @Composable
        @ReadOnlyComposable
        get() = LocalWherisElevation.current

    val borders: WherisBorders
        @Composable
        @ReadOnlyComposable
        get() = LocalWherisBorders.current

    val dimensions: WherisDimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalWherisDimensions.current

    val typography: WherisTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalWherisTypography.current

    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme
}