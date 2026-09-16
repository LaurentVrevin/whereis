package com.laurentvrevin.androidstarter.designsystem.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class ButtonDimensions(
    val heightSmall: Dp = 40.dp,
    val heightMedium: Dp = 48.dp,
    val heightLarge: Dp = 56.dp,
)

@Immutable
data class ChipDimensions(
    val heightSmall: Dp = 32.dp,
    val heightMedium: Dp = 40.dp,
)

@Immutable
data class IconDimensions(
    val sizeSmall: Dp = 16.dp,
    val sizeMedium: Dp = 24.dp,
    val sizeLarge: Dp = 32.dp,
)

@Immutable
data class AppDimensions(
    val button: ButtonDimensions = ButtonDimensions(),
    val chip: ChipDimensions = ChipDimensions(),
    val icon: IconDimensions = IconDimensions(),
)

val LocalAppDimensions = staticCompositionLocalOf { AppDimensions() }
