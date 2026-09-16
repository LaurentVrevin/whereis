package com.laurentvrevin.androidstarter.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Système typographique principal de l'application.
 */
@Immutable
data class AppTypography(
    val defaultFontFamily: FontFamily = FontFamily.Default,
    val display: TextStyle =
        TextStyle(
            fontFamily = defaultFontFamily,
            fontSize = 48.sp,
            fontWeight = FontWeight.Light,
            lineHeight = 56.sp,
        ),
    val h1: TextStyle =
        TextStyle(
            fontFamily = defaultFontFamily,
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 40.sp,
        ),
    val h2: TextStyle =
        TextStyle(
            fontFamily = defaultFontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 32.sp,
        ),
    val titleLarge: TextStyle =
        TextStyle(
            fontFamily = defaultFontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 28.sp,
        ),
    val bodyLarge: TextStyle =
        TextStyle(
            fontFamily = defaultFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp,
        ),
    val bodySmall: TextStyle =
        TextStyle(
            fontFamily = defaultFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp,
        ),
    val labelMedium: TextStyle =
        TextStyle(
            fontFamily = defaultFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.sp,
        ),
)

val LocalAppTypography = staticCompositionLocalOf { AppTypography() }
