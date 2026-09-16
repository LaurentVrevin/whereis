# Guide de Dépannage (Troubleshooting) 🛠️

Ce guide recense les problèmes les plus fréquents rencontrés lors de l'utilisation du starter et leurs solutions.

---

## 🏗️ Problèmes Gradle & Sync

### Le bootstrapProject n'apparaît pas
-   **Symptôme** : La commande `gradlew bootstrapProject` échoue avec "Task not found".
-   **Solution** : 
    1.  Vérifie que tu es bien à la racine du projet.
    2.  Lance `.\gradlew.bat tasks --all` pour forcer la découverte des tâches.
    3.  Fais un **File > Invalidate Caches** dans Android Studio.

### Gradle Sync échoue après le bootstrap
-   **Symptôme** : Android Studio affiche des erreurs rouges partout alors que le build Gradle a réussi.
-   **Solution** : 
    1.  Ferme Android Studio.
    2.  Supprime les dossiers `.gradle/` et `.idea/` (attention, cela réinitialise tes réglages d'IDE).
    3.  Relance le projet.

---

## 💾 Problèmes de Données (Room/DataStore)

### Room signale une erreur de schéma
-   **Symptôme** : L'app crash au lancement avec une erreur `IllegalStateException: Room cannot verify the data integrity`.
-   **Solution** : 
    -   Pendant le développement : Désinstalle l'app de ton téléphone ou de l'émulateur pour recréer la base.
    -   En production : Lis le [Guide Data](data.md) sur les migrations.

### KSP ne génère pas le code Room
-   **Symptôme** : Les classes `_Impl` de Room sont introuvables.
-   **Solution** : Lance `.\gradlew.bat clean kspDebugKotlin` pour forcer la génération.

---

## 🧪 Problèmes de Tests

### Un test coroutine reste bloqué (Timeout)
-   **Symptôme** : Le test ne finit jamais ou échoue après 60s.
-   **Solution** : Vérifie que tu utilises `runTest` (du package `kotlinx.coroutines.test`) et que ton ViewModel utilise bien le dispatcher injecté via Koin.

### Les tests UI échouent sur l'émulateur
-   **Symptôme** : "Could not find view".
-   **Solution** : Désactive les animations de l'émulateur (Settings > Developer options > Window animation scale = Off).

---

## 💉 Problèmes d'Injection (Koin)

### DefinitionNotFoundException
-   **Symptôme** : Crash au lancement : `No definition found for class '...'`.
-   **Solution** : Vérifie que ton module Koin est bien déclaré ET ajouté à la liste des modules dans ton fichier `App.kt`.

---
[Retour au README](../README.md)
