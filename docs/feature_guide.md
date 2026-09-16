# Guide : Créer une Feature de A à Z 🚀

Ce guide détaille la création d'une fonctionnalité complète à travers l'exemple pédagogique d'un gestionnaire de tâches (**Tasks**).

---

## 📂 Structure recommandée

```text
feature/tasks/src/main/java/com/exemple/app/feature/tasks/
├── domain/            # Logique métier pure
│   ├── Task.kt        # Modèle
│   └── TaskRepository.kt # Interface
├── data/              # Implémentation technique
│   ├── TaskRepositoryImpl.kt
│   └── TaskMappers.kt
├── presentation/      # UI (Compose + ViewModel)
│   ├── TaskViewModel.kt
│   ├── TaskUiState.kt
│   ├── TaskScreen.kt
│   └── TaskRoute.kt
└── di/                # Injection de dépendances
    └── TaskModule.kt
```

---

## 1. Couche Domaine

Commence par définir ton modèle et le contrat de ton repository.

```kotlin
// domain/Task.kt
data class Task(val id: Int, val title: String, val isCompleted: Boolean)

// domain/TaskRepository.kt
interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    suspend fun addTask(title: String)
}
```

---

## 2. Couche Données (Implementation)

Implémente le repository en utilisant Room (via le module `:data`).

```kotlin
// data/TaskRepositoryImpl.kt
class TaskRepositoryImpl(
    private val taskDao: TaskDao // Défini dans le module :data
) : TaskRepository {
    override fun getTasks(): Flow<List<Task>> = 
        taskDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addTask(title: String) {
        taskDao.insert(TaskEntity(title = title))
    }
}
```

---

## 3. Couche Présentation (ViewModel & UI)

### État UI (Immutable)
```kotlin
// presentation/TaskUiState.kt
data class TaskUiState(
    val items: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null
)
```

### ViewModel (UDF)
```kotlin
// presentation/TaskViewModel.kt
class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _state = MutableStateFlow(TaskUiState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        repository.getTasks()
            .onEach { items -> _state.update { it.copy(items = items, isLoading = false) } }
            .launchIn(viewModelScope)
    }

    fun onAddTask(title: String) {
        viewModelScope.launch { repository.addTask(title) }
    }
}
```

### Interface Compose (Stateless)
```kotlin
// presentation/TaskScreen.kt
@Composable
fun TaskScreen(
    state: TaskUiState,
    onAddClick: (String) -> Unit
) {
    Scaffold(
        topBar = { AppTopBar(title = "Mes Tâches") }
    ) { padding ->
        // Contenu utilisant les composants du Design System
        LazyColumn(Modifier.padding(padding)) {
            items(state.items) { task ->
                AppCard { Text(task.title) }
            }
        }
    }
}
```

---

## 4. Injection de Dépendances (Koin)

```kotlin
// di/TaskModule.kt
val taskModule = module {
    single<TaskRepository> { TaskRepositoryImpl(get()) }
    viewModel { TaskViewModel(get()) }
}
```
*N'oublie pas d'enregistrer ce module dans ton `App.kt` !*

---

## 5. Navigation

Enregistre ta route dans le `AppNavHost` global :

```kotlin
// Dans AppNavHost.kt
composable<TaskRoute> {
    val viewModel: TaskViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    TaskScreen(
        state = state,
        onAddClick = viewModel::onAddTask
    )
}
```

---

## ✅ Checklist "Feature Terminée"

- [ ] Les modèles domaine sont indépendants de Room/Ktor.
- [ ] Le ViewModel n'expose que des `StateFlow` read-only.
- [ ] L'UI observe l'état avec `collectAsStateWithLifecycle()`.
- [ ] Les textes sont passés via `UiText` et externalisés dans `strings.xml`.
- [ ] Une Preview Compose (Claire et Sombre) a été créée.
- [ ] Un test unitaire du ViewModel a été ajouté.

---
[Précédent : Modularisation](modularization.md) | [Suivant : Design System](design_system.md)
