package com.laurentvrevin.wheris.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.laurentvrevin.wheris.core.designsystem.foundation.WherisShapes

val LocalWherisDarkTheme = staticCompositionLocalOf { false }

private val MaterialShapes =
    Shapes(
        extraSmall = WherisShapes.extraSmall,
        small = WherisShapes.small,
        medium = WherisShapes.medium,
        large = WherisShapes.large,
        extraLarge = WherisShapes.extraLarge,
    )

@Composable
fun WherisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme =
            if (darkTheme) {
                WherisDarkColorScheme
            } else {
                WherisLightColorScheme
            },
        typography = WherisTypography,
        shapes = MaterialShapes,
        content = { CompositionLocalProvider(LocalWherisDarkTheme provides darkTheme, content = content) },
    )
}
