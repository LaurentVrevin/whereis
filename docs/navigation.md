# Navigation 🗺️

L'application utilise **Navigation Compose** avec un système de routes typées (introduit dans Navigation 2.8.0), garantissant une navigation robuste et sans erreur de saisie.

---

## 📍 1. AppNavHost

Le `AppNavHost` est situé dans le module `:app`. Il définit le graphe de navigation de toute l'application.

```kotlin
// app/src/main/java/com/exemple/app/navigation/AppNavHost.kt
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = StartRoute
    ) {
        composable<StartRoute> {
            StartScreen(
                onNavigateToShowcase = { navController.navigate(ShowcaseRoute) }
            )
        }
        
        composable<ShowcaseRoute> {
            ShowcaseScreen(onBack = { navController.popBackStack() })
        }
    }
}
```

---

## 🚦 2. Définition des Routes

Les routes sont des objets ou des classes annotés `@Serializable` (généralement placés dans le module `:app` ou partagés via `:core`).

```kotlin
@Serializable
data object StartRoute

@Serializable
data class DetailRoute(val id: String) // Route avec paramètre
```

---

## 🛠️ 3. Navigation avec Paramètres

Pour transmettre un identifiant à un écran de détail :

1.  **Déclenchement** : `navController.navigate(DetailRoute(id = "123"))`
2.  **Récupération** :
```kotlin
composable<DetailRoute> { backStackEntry ->
    val route: DetailRoute = backStackEntry.toRoute()
    val id = route.id
    // Injecte l'ID dans ton ViewModel ou utilise-le ici
}
```

---

## 🏛️ 4. Séparation Route / Screen

Pour garder les écrans testables et indépendants du moteur de navigation :
-   **`TaskRoute`** : Gère l'obtention du ViewModel et les appels au `navController`.
-   **`TaskScreen`** : Composable pur (Stateless) qui ne connaît pas la navigation.

```kotlin
// ✅ RECOMMANDÉ
@Composable
fun TaskRoute(onNavigateBack: () -> Unit) {
    val viewModel: TaskViewModel = koinViewModel()
    TaskScreen(onBack = onNavigateBack)
}
```

---

## ⚠️ Pourquoi le ViewModel ne reçoit pas le NavController ?

Passer le `NavController` à un ViewModel est une mauvaise pratique car :
1.  Il couple la logique métier à un composant UI Android.
2.  Il rend les tests unitaires impossibles.
3.  Il provoque des fuites mémoire lors des rotations d'écran.

---
[Précédent : UI State & UDF](ui_state.md) | [Suivant : Injection de Dépendances](dependency_injection.md)
