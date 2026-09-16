# Injection de Dépendances avec Koin 💉

Le projet utilise **Koin 4.x**, un framework d'injection de dépendances (DI) léger et pragmatique, écrit en pur Kotlin.

---

## 🏗️ Initialisation

Koin est démarré dans la classe `App` (située dans le module `:app`). Il est crucial d'y enregistrer **tous** les modules nécessaires au fonctionnement de l'application.

```kotlin
// app/src/main/java/com/laurentvrevin/androidstarter/App.kt
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                configurationModule, // Fournit NetworkConfig
                networkModule,       // Fournit HttpClient
                dataModule,          // Database, DAOs, Preferences
                designSystemModule,
                templateModule,
                appModule,
            )
        }
    }
}
```

---

## 🧩 Les Modules

Les dépendances sont organisées par responsabilité.

### Flux de la Couche Réseau
La couche réseau illustre parfaitement la chaîne de dépendances dans Koin :

1.  **`configurationModule`** : Extrait les valeurs de `BuildConfig` (Base URL, debug mode) pour fournir un objet [`NetworkConfig`](../data/src/main/java/com/laurentvrevin/androidstarter/data/remote/NetworkConfig.kt).
2.  **`networkModule`** : Demande ce `NetworkConfig` (via `get()`) pour configurer et fournir le `HttpClient` de Ktor.
3.  **`repositories`** : Injectent le `HttpClient` pour effectuer les appels API.

> [!CAUTION]
> Si `configurationModule` est omis dans `App.kt`, le `networkModule` ne pourra pas résoudre `NetworkConfig`. L'erreur (exception de définition manquante) ne surviendra qu'au moment où le premier Repository tentera d'utiliser le client réseau.

---

## 🧪 Tests de Non-Régression

Un test dédié vérifie l'intégrité du graphe Koin pour la partie réseau :
[`NetworkKoinModuleTest.kt`](../app/src/test/java/com/laurentvrevin/androidstarter/di/NetworkKoinModuleTest.kt)

Ce test charge `configurationModule` et `networkModule` pour s'assurer que `HttpClient` est résolvable. Il prend soin de fermer le client et d'arrêter Koin après l'exécution pour éviter les fuites entre les tests.

---

## 💉 Injection dans Compose

Utilise les fonctions dédiées pour récupérer tes instances dans l'UI.

```kotlin
@Composable
fun TaskRoute() {
    // Récupère le ViewModel injecté
    val viewModel: TaskViewModel = koinViewModel()
    
    // Récupère une dépendance simple
    val analytics: AnalyticsHelper = koinInject()
}
```

---

## 🚫 Anti-patterns

1.  **Oubli de module technique** : Ne pas enregistrer les modules de configuration (comme `configurationModule`) casse silencieusement les dépendances techniques.
2.  **Enorme module unique** : Ne mets pas tout dans `appModule`. Crée un module par feature.
3.  **Inversion non respectée** : Déclare toujours tes interfaces dans le `domain` et injecte-les, plutôt que d'injecter directement l'implémentation.

---
[Voir aussi : Couche Réseau](network.md) | [Précédent : Navigation](navigation.md) | [Suivant : Données](data.md)
