package com.laurentvrevin.androidstarter.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * Palette claire de l'application.
 *
 * Cette palette contient les valeurs utilisées quand l'application
 * est affichée en mode clair.
 *
 * On raisonne en rôles de couleurs (primary, surface, onSurface...)
 * plutôt qu'en noms visuels (turquoise, blanc, gris), afin de garder
 * une structure compatible avec le dark mode et plus facile à maintenir.
 */
object AppLightColors {
    /**
     * Couleur d'accent principale.
     *
     * À utiliser pour :
     * - les CTA principaux
     * - les éléments interactifs majeurs
     * - les accents visuels importants
     */
    val primary = Color(0xFF00BFA5)

    /**
     * Variante douce de la couleur principale.
     *
     * Utile pour :
     * - fonds légers teintés
     * - chips sélectionnés
     * - états passifs
     * - zones secondaires liées à la couleur principale
     */
    val primaryContainer = Color(0xFFE0F7F4)

    /**
     * Couleur secondaire.
     *
     * À utiliser avec modération pour :
     * - accents secondaires
     * - icônes
     * - éléments complémentaires
     */
    val secondary = Color(0xFF26A69A)

    /**
     * Couleur de fond principale des écrans.
     *
     * Correspond au fond global de l'application.
     */
    val background = Color(0xFFF5FFFE)

    /**
     * Couleur de surface principale.
     *
     * À utiliser pour les composants posés sur le fond :
     * - cards
     * - sheets
     * - dialogs
     * - conteneurs
     */
    val surface = Color(0xFFFFFFFF)

    /**
     * Variante de surface.
     *
     * Sert à créer une légère hiérarchie visuelle entre surfaces,
     * sans ajouter trop de contraste.
     */
    val surfaceVariant = Color(0xFFF0F9F8)

    /**
     * Couleur du contenu affiché sur la couleur primary.
     *
     * Généralement utilisée pour :
     * - texte sur bouton principal
     * - icônes sur fond principal
     */
    val onPrimary = Color(0xFFFFFFFF)

    /**
     * Couleur principale du contenu affiché sur le background.
     *
     * Sert pour le texte principal au niveau écran.
     */
    val onBackground = Color(0xFF1B2B2A)

    /**
     * Couleur principale du contenu affiché sur les surfaces.
     *
     * Sert pour le texte et les icônes sur cards, sheets et dialogs.
     */
    val onSurface = Color(0xFF1B2B2A)

    /**
     * Couleur secondaire du contenu sur surface.
     *
     * Idéale pour :
     * - descriptions
     * - placeholders
     * - textes secondaires
     * - métadonnées visuelles
     */
    val onSurfaceVariant = Color(0xFF546E6C)

    /**
     * Couleur des bordures, séparateurs et traits.
     */
    val outline = Color(0xFFCFD8D7)

    /**
     * Couleur utilisée pour les états d'erreur.
     *
     * Exemples :
     * - message d'erreur
     * - champ invalide
     * - alerte critique
     */
    val error = Color(0xFFEF5350)

    /**
     * Couleur d'overlay sombre.
     *
     * Utilisée derrière :
     * - dialogs
     * - modals
     * - bottom sheets
     */
    val scrim = Color(0x52000000)
}

/**
 * Palette sombre de l'application.
 *
 * Cette palette contient les valeurs utilisées quand l'application
 * est affichée en mode sombre.
 */
object AppDarkColors {
    /**
     * Couleur d'accent principale en mode sombre.
     */
    val primary = Color(0xFF26D7C1)

    /**
     * Variante douce de la couleur principale en mode sombre.
     */
    val primaryContainer = Color(0xFF103A36)

    /**
     * Couleur secondaire en mode sombre.
     */
    val secondary = Color(0xFF4DD0C1)

    /**
     * Couleur de fond principale des écrans en mode sombre.
     */
    val background = Color(0xFF0F1716)

    /**
     * Couleur de surface principale en mode sombre.
     */
    val surface = Color(0xFF16201F)

    /**
     * Variante de surface en mode sombre.
     */
    val surfaceVariant = Color(0xFF1D2A29)

    /**
     * Couleur du contenu affiché sur primary en mode sombre.
     */
    val onPrimary = Color(0xFF062B27)

    /**
     * Couleur principale du contenu affiché sur le fond en mode sombre.
     */
    val onBackground = Color(0xFFE7F2F1)

    /**
     * Couleur principale du contenu affiché sur les surfaces en mode sombre.
     */
    val onSurface = Color(0xFFE7F2F1)

    /**
     * Couleur secondaire du contenu sur surface en mode sombre.
     */
    val onSurfaceVariant = Color(0xFFA7BCB8)

    /**
     * Couleur des bordures et séparateurs en mode sombre.
     */
    val outline = Color(0xFF35514E)

    /**
     * Couleur d'erreur en mode sombre.
     */
    val error = Color(0xFFFF8A80)

    /**
     * Overlay sombre utilisé derrière les modals.
     *
     * Ici on garde une valeur semi-transparente adaptée au dark mode.
     */
    val scrim = Color(0x99000000)
}
