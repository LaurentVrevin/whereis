package com.laurentvrevin.wheris.core.designsystem.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class WherisControlHeights(
    val small: Dp = 40.dp,
    val medium: Dp = 48.dp,
    val large: Dp = 56.dp,
)

@Immutable
data class WherisIconSizes(
    val extraSmall: Dp = 16.dp,
    val small: Dp = 20.dp,
    val medium: Dp = 24.dp,
    val large: Dp = 32.dp,
    val extraLarge: Dp = 40.dp,
)

@Immutable
data class WherisDimensions(
    val minimumTouchTarget: Dp = 48.dp,
    val controlHeight: WherisControlHeights = WherisControlHeights(),
    val iconSize: WherisIconSizes = WherisIconSizes(),
)

val LocalWherisDimensions = staticCompositionLocalOf { WherisDimensions() }