package com.laurentvrevin.androidstarter.designsystem.foundation

import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Point d'entrée central pour les styles du Design System.
 * Regroupe les utilitaires et extensions pour l'API Styles.
 */
object AppStyles {
    /**
     * Style de base partagé par plusieurs composants.
     */
    val Base =
        Style {
            // Propriétés communes si nécessaires
        }
}

/**
 * Extension pour appliquer un padding standard du Design System dans un Style.
 */
fun StyleScope.appPadding(
    spacing: AppSpacing,
    all: Dp? = null,
) {
    contentPadding(all ?: spacing.standard)
}

/**
 * Extension pour appliquer une bordure standard du Design System dans un Style.
 */
fun StyleScope.appBorder(
    borders: AppBorders,
    width: Dp? = null,
    color: Color? = null,
) {
    color?.let { border(width ?: borders.thin, it) }
}
