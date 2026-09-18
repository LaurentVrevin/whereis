package com.laurentvrevin.wheris.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import com.laurentvrevin.wheris.designsystem.foundation.AppBorders
import com.laurentvrevin.wheris.designsystem.foundation.AppDimensions
import com.laurentvrevin.wheris.designsystem.foundation.AppElevation
import com.laurentvrevin.wheris.designsystem.foundation.AppShapes
import com.laurentvrevin.wheris.designsystem.foundation.AppSpacing
import com.laurentvrevin.wheris.designsystem.foundation.LocalAppBorders
import com.laurentvrevin.wheris.designsystem.foundation.LocalAppDimensions
import com.laurentvrevin.wheris.designsystem.foundation.LocalAppElevation
import com.laurentvrevin.wheris.designsystem.foundation.LocalAppShapes
import com.laurentvrevin.wheris.designsystem.foundation.LocalAppSpacing
import com.laurentvrevin.wheris.designsystem.styles.ButtonStyles
import com.laurentvrevin.wheris.designsystem.styles.CardStyles
import com.laurentvrevin.wheris.designsystem.styles.ChipStyles
import com.laurentvrevin.wheris.designsystem.styles.DefaultButtonStyles
import com.laurentvrevin.wheris.designsystem.styles.DefaultCardStyles
import com.laurentvrevin.wheris.designsystem.styles.DefaultChipStyles
import com.laurentvrevin.wheris.designsystem.styles.DefaultInputStyles
import com.laurentvrevin.wheris.designsystem.styles.DefaultTopBarStyles
import com.laurentvrevin.wheris.designsystem.styles.InputStyles
import com.laurentvrevin.wheris.designsystem.styles.LocalButtonStyles
import com.laurentvrevin.wheris.designsystem.styles.LocalCardStyles
import com.laurentvrevin.wheris.designsystem.styles.LocalChipStyles
import com.laurentvrevin.wheris.designsystem.styles.LocalInputStyles
import com.laurentvrevin.wheris.designsystem.styles.LocalTopBarStyles
import com.laurentvrevin.wheris.designsystem.styles.TopBarStyles

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme =
        if (darkTheme) {
            darkColorScheme(
                primary = AppDarkColors.primary,
                primaryContainer = AppDarkColors.primaryContainer,
                secondary = AppDarkColors.secondary,
                background = AppDarkColors.background,
                surface = AppDarkColors.surface,
                surfaceVariant = AppDarkColors.surfaceVariant,
                onPrimary = AppDarkColors.onPrimary,
                onBackground = AppDarkColors.onBackground,
                onSurface = AppDarkColors.onSurface,
                onSurfaceVariant = AppDarkColors.onSurfaceVariant,
                outline = AppDarkColors.outline,
                error = AppDarkColors.error,
                scrim = AppDarkColors.scrim,
            )
        } else {
            lightColorScheme(
                primary = AppLightColors.primary,
                primaryContainer = AppLightColors.primaryContainer,
                secondary = AppLightColors.secondary,
                background = AppLightColors.background,
                surface = AppLightColors.surface,
                surfaceVariant = AppLightColors.surfaceVariant,
                onPrimary = AppLightColors.onPrimary,
                onBackground = AppLightColors.onBackground,
                onSurface = AppLightColors.onSurface,
                onSurfaceVariant = AppLightColors.onSurfaceVariant,
                outline = AppLightColors.outline,
                error = AppLightColors.error,
                scrim = AppLightColors.scrim,
            )
        }

    val appTypography = AppTypography()
    val m3Typography =
        Typography(
            displayLarge = appTypography.display,
            headlineLarge = appTypography.h1,
            headlineMedium = appTypography.h2,
            titleLarge = appTypography.titleLarge,
            bodyLarge = appTypography.bodyLarge,
            bodyMedium = appTypography.bodySmall,
            labelMedium = appTypography.labelMedium,
        )

    CompositionLocalProvider(
        LocalButtonStyles provides DefaultButtonStyles(),
        LocalCardStyles provides DefaultCardStyles(),
        LocalChipStyles provides DefaultChipStyles(),
        LocalInputStyles provides DefaultInputStyles(),
        LocalTopBarStyles provides DefaultTopBarStyles(),
        LocalAppSpacing provides AppSpacing(),
        LocalAppShapes provides AppShapes(),
        LocalAppElevation provides AppElevation(),
        LocalAppBorders provides AppBorders(),
        LocalAppDimensions provides AppDimensions(),
        LocalAppTypography provides appTypography,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = m3Typography,
            content = content,
        )
    }
}

/**
 * Objet d'accès centralisé aux jetons (tokens) et styles du Design System.
 */
object AppTheme {
    val spacing: AppSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalAppSpacing.current

    val shapes: AppShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalAppShapes.current

    val elevation: AppElevation
        @Composable
        @ReadOnlyComposable
        get() = LocalAppElevation.current

    val borders: AppBorders
        @Composable
        @ReadOnlyComposable
        get() = LocalAppBorders.current

    val dimensions: AppDimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalAppDimensions.current

    val typography: AppTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTypography.current

    val buttonStyles: ButtonStyles
        @Composable
        @ReadOnlyComposable
        get() = LocalButtonStyles.current

    val cardStyles: CardStyles
        @Composable
        @ReadOnlyComposable
        get() = LocalCardStyles.current

    val chipStyles: ChipStyles
        @Composable
        @ReadOnlyComposable
        get() = LocalChipStyles.current

    val inputStyles: InputStyles
        @Composable
        @ReadOnlyComposable
        get() = LocalInputStyles.current

    val topBarStyles: TopBarStyles
        @Composable
        @ReadOnlyComposable
        get() = LocalTopBarStyles.current

    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme
}
