package com.laurentvrevin.androidstarter.designsystem.styles

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocals fournissant les implémentations de styles pour chaque composant.
 *
 * On utilise [staticCompositionLocalOf] car ces définitions de styles ne changent
 * généralement pas pendant le cycle de vie de l'application (contrairement au thème).
 * Cela évite des recompositions inutiles de l'arborescence UI.
 */

val LocalButtonStyles =
    staticCompositionLocalOf<ButtonStyles> {
        error("No ButtonStyles provided")
    }

val LocalCardStyles =
    staticCompositionLocalOf<CardStyles> {
        error("No CardStyles provided")
    }

val LocalChipStyles =
    staticCompositionLocalOf<ChipStyles> {
        error("No ChipStyles provided")
    }

val LocalInputStyles =
    staticCompositionLocalOf<InputStyles> {
        error("No InputStyles provided")
    }

val LocalTopBarStyles =
    staticCompositionLocalOf<TopBarStyles> {
        error("No TopBarStyles provided")
    }
