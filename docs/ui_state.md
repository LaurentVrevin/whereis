# UI State & Flux Unidirectionnel (UDF) 🔄

La gestion de l'état est le cœur de la réactivité dans Jetpack Compose. Ce starter impose un flux unidirectionnel strict.

---

## 🏛️ Le Pattern UDF

1.  **L'UI** émet des actions (clic, saisie) vers le **ViewModel**.
2.  **Le ViewModel** traite l'action, met à jour son état interne.
3.  **L'UI** reçoit le nouvel état et se recompose.

---

## 📦 1. UiState (Immutable)

L'état d'un écran doit toujours être une `data class` immutable.

```kotlin
data class TemplateUiState(
    val items: List<TemplateItem> = emptyList(),
    val isInitialLoading: Boolean = true,
    val error: UiText? = null
)
```

---

## 🧠 2. Le ViewModel

Le ViewModel possède le `MutableStateFlow` (privé) et expose un `StateFlow` (public/read-only).

```kotlin
class TemplateViewModel(private val repository: TemplateRepository) : ViewModel() {

    private val _state = MutableStateFlow(TemplateUiState())
    val state = _state.asStateFlow()

    fun deleteItem(id: Int) {
        viewModelScope.launch {
            repository.delete(id)
            // L'UI se mettra à jour via le Flow de Room
        }
    }
}
```

---

## 📱 3. Observation dans Compose

Utilise toujours `collectAsStateWithLifecycle()` pour observer l'état. Cela garantit que le flux s'arrête quand l'application est en arrière-plan (économie de ressources).

```kotlin
@Composable
fun TemplateRoute(viewModel: TemplateViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TemplateScreen(
        state = state,
        onAction = viewModel::onAction
    )
}
```

---

## ⚡ 4. Événements Ponctuels (UiEffect)

Certaines actions ne sont pas des états (ex: afficher un Toast, naviguer). Nous utilisons un **Channel** ou un **SharedFlow** pour ces effets "fire and forget".

| Situation | Type | Exemple |
| :--- | :--- | :--- |
| Donnée qui reste à l'écran | **UiState** | Liste d'utilisateurs, texte d'un champ. |
| Action instantanée | **UiEffect** | Navigation, Snackbar, Vibration. |
| Action utilisateur | **UiAction** | Clic sur "Sauvegarder", Refresh. |

---

## ⚠️ Erreurs fréquentes

1.  **Lancer une coroutine dans l'UI** : Ne lance jamais `scope.launch { repo.call() }` dans un Composable. Passe par le ViewModel.
2.  **État mutable dans l'UI** : Ne crée pas de `var` mutable dans ton écran pour stocker des données métier.
3.  **Snackbar dans UiState** : Si tu stockes un message d'erreur dans l'état, il risque de s'afficher à nouveau lors d'une recomposition. Préfère un événement ponctuel.

---
[Précédent : Design System](design_system.md) | [Suivant : Navigation](navigation.md)
