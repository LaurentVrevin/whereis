# Logique de Build & Convention Plugins 🛠️

Le projet utilise des **Convention Plugins** Gradle (situés dans le dossier `build-logic`) pour centraliser la configuration. Cela évite de répéter des dizaines de lignes de configuration dans chaque module.

---

## 🏛️ Rôle de build-logic

Au lieu d'avoir un fichier `build.gradle.kts` géant à la racine, nous avons des plugins réutilisables par type de module :
1.  **Android Application** : Configuration standard pour le module `:app`.
2.  **Android Library** : Configuration commune pour les bibliothèques.
3.  **Compose** : Ajoute le support de Jetpack Compose aux modules concernés.

---

## 🧩 Plugins disponibles

| ID du Plugin | Usage |
| :--- | :--- |
| **`com.laurentvrevin.android.application`** | Modules d'application (APK). |
| **`com.laurentvrevin.android.application.compose`** | Modules d'application avec UI Compose. |
| **`com.laurentvrevin.android.library`** | Modules de données ou métier (sans UI). |
| **`com.laurentvrevin.android.library.compose`** | Modules de fonctionnalités avec UI Compose. |
| **`com.laurentvrevin.project.bootstrap`** | Outil d'initialisation du template. |

---

## 🛠️ Exemple d'utilisation

Pour créer un nouveau module feature avec Compose, ton `build.gradle.kts` ressemblera simplement à ceci :

```kotlin
plugins {
    id("com.laurentvrevin.android.library.compose") // Tout est déjà configuré ici !
}

android {
    namespace = "com.monapp.feature.mabellefeature"
}

dependencies {
    implementation(project(":core"))
    // ... tes dépendances spécifiques
}
```

---

## ⚙️ Où modifier la configuration ?

-   **Versions des bibliothèques** : Modifie `gradle/libs.versions.toml`.
-   **Configuration Android (SDK, Java)** : Modifie les fichiers dans `build-logic/convention/src/main/kotlin/`.
-   **Ajouter un plugin** : Enregistre-le dans `build-logic/convention/build.gradle.kts`.

---

## 🧪 Tester la logique de build

Puisque le build est du code Kotlin, nous le testons également.
**Commande** : `.\gradlew.bat -p build-logic :bootstrap:test`

---
[Précédent : Stratégie de Test](testing.md) | [Suivant : Bootstrap](bootstrap.md)
