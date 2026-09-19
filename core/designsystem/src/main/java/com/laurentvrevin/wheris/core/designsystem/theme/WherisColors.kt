package com.laurentvrevin.wheris.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal val WherisLightColorScheme =
    lightColorScheme(
        primary = Color(0xFFF97316),
        onPrimary = Color(0xFF1C1917),
        primaryContainer = Color(0xFFFFEDD5),
        onPrimaryContainer = Color(0xFF1C1917),
        secondary = Color(0xFFFB923C),
        onSecondary = Color(0xFF1C1917),
        background = Color(0xFFFFF7ED),
        onBackground = Color(0xFF1C1917),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF1C1917),
        surfaceVariant = Color(0xFFFFF7ED),
        onSurfaceVariant = Color(0xFF57534E),
        outline = Color(0xFFE7E5E4),
        error = Color(0xFFDC2626),
    )

internal val WherisDarkColorScheme =
    darkColorScheme(
        primary = Color(0xFFFB923C),
        onPrimary = Color(0xFF1C1917),
        primaryContainer = Color(0xFF7C2D12),
        onPrimaryContainer = Color(0xFFFAFAF9),
        secondary = Color(0xFFFDBA74),
        onSecondary = Color(0xFF1C1917),
        background = Color(0xFF12100F),
        onBackground = Color(0xFFFAFAF9),
        surface = Color(0xFF1C1917),
        onSurface = Color(0xFFFAFAF9),
        surfaceVariant = Color(0xFF292524),
        onSurfaceVariant = Color(0xFFD6D3D1),
        outline = Color(0xFF44403C),
        error = Color(0xFFF87171),
    )
