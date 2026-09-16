package com.laurentvrevin.androidstarter.designsystem.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppBorders(
    val none: Dp = 0.dp,
    val thin: Dp = 1.dp,
    val thick: Dp = 2.dp,
)

val LocalAppBorders = staticCompositionLocalOf { AppBorders() }
