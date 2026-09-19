package com.laurentvrevin.wheris.core.designsystem.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class WherisBorders(
    val none: Dp = 0.dp,
    val default: Dp = 1.dp,
    val emphasis: Dp = 2.dp,
)

val LocalWherisBorders = staticCompositionLocalOf { WherisBorders() }