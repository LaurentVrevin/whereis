# Design System 🎨

Le module `:designsystem` est ton framework UI interne. Il garantit la cohérence visuelle et réduit drastiquement le temps de création des écrans.

---

## 💎 Hiérarchie du Design System

1.  **Tokens (Fondations)** : Atomes visuels (couleurs, espacements, tailles).
2.  **Styles** : Configuration des composants (ex: ButtonStylePrimary).
3.  **Composants** : Éléments interactifs (Boutons, Cards, Inputs).
4.  **Patterns** : Assemblages fréquents (Écran de base, Sections).

---

## 🪙 1. Les Tokens

### Couleurs
-   **Localisation** : `foundation/AppColorsScheme.kt`
-   **Usage** : Utilise `AppTheme.colors.primary` plutôt que des valeurs hexadécimales.
-   **Thème Dynamique** : Le starter supporte nativement le mode clair et sombre.

### Spacing (Espacements)
-   **Localisation** : `foundation/AppSpacing.kt`
-   **Valeurs** : `extraSmall` (4dp), `small` (8dp), `medium` (12dp), `standard` (16dp), `large` (24dp), etc.
-   **Règle** : N'utilise jamais de `8.dp` en dur dans tes features. Préfère `AppTheme.spacing.small`.

### Typography
-   **Localisation** : `theme/AppTypography.kt`
-   **Échelles** : `display`, `h1`, `h2`, `titleLarge`, `bodyLarge`, `bodySmall`, `labelMedium`.

---

## 🏗️ 2. L'objet AppTheme

C'est le point d'entrée unique. Il utilise des `CompositionLocal` pour injecter les tokens sans polluer les paramètres des fonctions.

```kotlin
// Dans n'importe quel Composable d'une feature
Text(
    text = "Hello World",
    color = AppTheme.colors.onBackground,
    style = AppTheme.typography.h1
)
```

---

## 🧩 3. Les Composants

Chaque composant est documenté interactivement dans le **Showcase**.

### Boutons (`components/button/`)
-   `AppPrimaryButton` : Action principale.
-   `AppSecondaryButton` : Action secondaire.
-   `AppOutlinedButton` : Action tertiaire ou neutre.
-   `AppDangerButton` : Actions destructrices (ex: Supprimer).

### Feedback & États (`components/feedback/`)
-   `AppSnackbar` : Messages temporaires (Succès, Erreur).
-   `EmptyState` : Affiché quand une liste est vide.
-   `LoadingOverlay` : Bloque l'écran pendant un chargement long.

---

## 🖋️ 4. API de Styles

Pour personnaliser l'apparence des composants (ex: arrondis des cartes), modifie les fichiers dans le package `styles/`.
Le starter utilise une architecture basée sur l'**API Style expérimentale de Compose**, permettant de découpler la structure du composant de son style visuel.

---

## ♿ Accessibilité

Le Design System impose :
1.  **Tailles tactiles** : Minimum 48x48dp pour tous les boutons.
2.  **Contrastes** : Respect des normes WCAG AA via les palettes Material 3.
3.  **Sémantique** : Utilisation de `Modifier.semantics` et `contentDescription`.

---

## 📺 Le Showcase

Le **Showcase** est un écran spécial qui affiche tous les composants et tokens. C'est l'outil parfait pour :
1.  Vérifier le rendu d'un composant en mode sombre.
2.  Tester la réactivité aux changements de thème.
3.  Montrer les possibilités UI aux designers.

**Comment le lancer ?** 
Exécute l'application et clique sur le bouton "Showcase" sur l'écran de démarrage.

---
[Précédent : Guide Feature](feature_guide.md) | [Suivant : UI State & UDF](ui_state.md)
