package com.laurentvrevin.androidstarter.designsystem.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppElevation(
    val none: Dp = 0.dp,
    val level1: Dp = 2.dp,
    val level2: Dp = 6.dp,
    val level3: Dp = 12.dp,
)

val LocalAppElevation = staticCompositionLocalOf { AppElevation() }
