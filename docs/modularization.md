# Modularisation & Scalabilité 📦

Le projet est découpé en plusieurs modules Gradle pour améliorer le temps de compilation, isoler les fonctionnalités et faciliter le travail en équipe.

---

## 📐 Structure Actuelle

| Module | Type | Description |
| :--- | :--- | :--- |
| **`:app`** | App | Point d'entrée. Initialise Koin, gère la navigation globale et le thème racine. |
| **`:core`** | Library | Classes de base (`UiText`, `BaseViewModel`), extensions et utilitaires sans UI. |
| **`:data`** | Library | Couche technique : configuration Ktor, base Room, DataStore. |
| **`:designsystem`** | Library | UI Framework interne : Tokens, Styles et Composants atomiques (Boutons, Cards). |
| **`:feature:template`** | Library | Exemple d'une feature complète. Sert de modèle à copier/coller. |
| **`build-logic`** | Non-Android | Convention Plugins pour partager la configuration Gradle entre les modules. |

---

## 🛠 Créer une nouvelle Feature

Imaginons que tu veuilles créer une liste de tâches (`tasks`). Voici la procédure :

### 1. Création physique
Crée le dossier `feature/tasks` et ajoute un fichier `build.gradle.kts` :

```kotlin
// feature/tasks/build.gradle.kts
plugins {
    id("com.laurentvrevin.android.library.compose") // Utilise la convention partagée
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.laurentvrevin.androidstarter.feature.tasks"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":designsystem"))
    // ... autres dépendances (Koin, Lifecycle, etc.)
}
```

### 2. Enregistrement
Ajoute le module dans le fichier racine `settings.gradle.kts` :
```kotlin
include(":feature:tasks")
```

### 3. Structure interne
Respecte la convention de package :
```text
com.exemple.monapplication.feature.tasks
├── presentation/   # Écrans, ViewModel, UiState
├── domain/         # Modèles métier, Interface Repository
├── data/           # Implémentation Repository, Mappers, DTOs
└── di/             # Module Koin de la feature
```

### 4. Navigation
Définis ta route dans `:app` (ou dans un fichier de navigation propre à la feature) et ajoute-la dans le `AppNavHost` global.

---

## 🔄 Évolutions fréquentes

### Retirer la feature Template
Une fois que tu as compris le fonctionnement, supprime simplement le dossier `feature/template`, retire son `include` dans `settings.gradle.kts` et supprime sa référence dans le module Koin de `:app`.

### Créer un module Core spécialisé
Si ton projet devient gros, n'hésite pas à découper `:core` en sous-modules :
- `:core:ui` (Classes de base Compose).
- `:core:network` (Abstractions réseau).
- `:core:model` (Modèles transversaux).

---

## 🧮 Tableau des visibilités

| Module | Peut dépendre de | Ne doit pas dépendre de |
| :--- | :--- | :--- |
| **`:feature:*`** | `:core`, `:data`, `:designsystem` | Une autre feature. |
| **`:data`** | `:core` | `:designsystem`, `:feature:*`. |
| **`:designsystem`** | `:core` | `:data`, `:feature:*`. |
| **`:app`** | **Tout** | Rien (personne ne dépend de `:app`). |

---
[Précédent : Architecture](architecture.md) | [Suivant : Guide de création de Feature](feature_guide.md)
