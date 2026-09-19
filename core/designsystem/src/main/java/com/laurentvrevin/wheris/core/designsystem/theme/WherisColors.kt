package com.laurentvrevin.wheris.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class WherisColors(
    val background: Color,
    val surface: Color,
    val surfaceSubtle: Color,
    val surfaceElevated: Color,

    val textPrimary: Color,
    val textSecondary: Color,
    val textOnPrimary: Color,

    val borderDefault: Color,

    val actionPrimary: Color,
    val actionPrimaryEmphasis: Color,
    val actionPrimarySoft: Color,

    val statusSuccess: Color,
    val statusError: Color,
    val statusInfo: Color,
)

val WherisLightColors =
    WherisColors(
        background = Color(0xFFFFF7ED),
        surface = Color(0xFFFFFFFF),
        surfaceSubtle = Color(0xFFFFF7ED),
        surfaceElevated = Color(0xFFFFFFFF),

        textPrimary = Color(0xFF1C1917),
        textSecondary = Color(0xFF57534E),
        textOnPrimary = Color(0xFF1C1917),

        borderDefault = Color(0xFFE7E5E4),

        actionPrimary = Color(0xFFF97316),
        actionPrimaryEmphasis = Color(0xFFFB923C),
        actionPrimarySoft = Color(0xFFFFEDD5),

        statusSuccess = Color(0xFF22C55E),
        statusError = Color(0xFFDC2626),
        statusInfo = Color(0xFF2563EB),
    )

val WherisDarkColors =
    WherisColors(
        background = Color(0xFF12100F),
        surface = Color(0xFF1C1917),
        surfaceSubtle = Color(0xFF292524),
        surfaceElevated = Color(0xFF292524),

        textPrimary = Color(0xFFFAFAF9),
        textSecondary = Color(0xFFD6D3D1),
        textOnPrimary = Color(0xFF1C1917),

        borderDefault = Color(0xFF44403C),

        actionPrimary = Color(0xFFFB923C),
        actionPrimaryEmphasis = Color(0xFFFDBA74),
        actionPrimarySoft = Color(0xFF7C2D12),

        statusSuccess = Color(0xFF4ADE80),
        statusError = Color(0xFFF87171),
        statusInfo = Color(0xFF60A5FA),
    )

val LocalWherisColors =
    staticCompositionLocalOf {
        WherisLightColors
    }