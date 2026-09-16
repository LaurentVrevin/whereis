package com.laurentvrevin.androidstarter.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import com.laurentvrevin.androidstarter.designsystem.foundation.AppBorders
import com.laurentvrevin.androidstarter.designsystem.foundation.AppDimensions
import com.laurentvrevin.androidstarter.designsystem.foundation.AppElevation
import com.laurentvrevin.androidstarter.designsystem.foundation.AppShapes
import com.laurentvrevin.androidstarter.designsystem.foundation.AppSpacing
import com.laurentvrevin.androidstarter.designsystem.foundation.LocalAppBorders
import com.laurentvrevin.androidstarter.designsystem.foundation.LocalAppDimensions
import com.laurentvrevin.androidstarter.designsystem.foundation.LocalAppElevation
import com.laurentvrevin.androidstarter.designsystem.foundation.LocalAppShapes
import com.laurentvrevin.androidstarter.designsystem.foundation.LocalAppSpacing
import com.laurentvrevin.androidstarter.designsystem.styles.ButtonStyles
import com.laurentvrevin.androidstarter.designsystem.styles.CardStyles
import com.laurentvrevin.androidstarter.designsystem.styles.ChipStyles
import com.laurentvrevin.androidstarter.designsystem.styles.DefaultButtonStyles
import com.laurentvrevin.androidstarter.designsystem.styles.DefaultCardStyles
import com.laurentvrevin.androidstarter.designsystem.styles.DefaultChipStyles
import com.laurentvrevin.androidstarter.designsystem.styles.DefaultInputStyles
import com.laurentvrevin.androidstarter.designsystem.styles.DefaultTopBarStyles
import com.laurentvrevin.androidstarter.designsystem.styles.InputStyles
import com.laurentvrevin.androidstarter.designsystem.styles.LocalButtonStyles
import com.laurentvrevin.androidstarter.designsystem.styles.LocalCardStyles
import com.laurentvrevin.androidstarter.designsystem.styles.LocalChipStyles
import com.laurentvrevin.androidstarter.designsystem.styles.LocalInputStyles
import com.laurentvrevin.androidstarter.designsystem.styles.LocalTopBarStyles
import com.laurentvrevin.androidstarter.designsystem.styles.TopBarStyles

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
