# Stratégie de Test 🧪

La testabilité est un pilier de ce starter. L'architecture Clean et l'injection de dépendances permettent de tester chaque couche de manière isolée.

---

## 🏛️ La Pyramide des Tests

### 1. Tests Unitaires (Locaux)
-   **VM / Domain / Mappers** : Situés dans `src/test`. Rapides et sans simulateur.
-   **Outils** : JUnit 4, Kotlin Coroutines Test.
-   **Exemple** : [`TemplateViewModelTest.kt`](../feature/template/src/test/java/com/laurentvrevin/androidstarter/feature/template/TemplateViewModelTest.kt).

### 2. Tests de Données (Instrumentés)
-   **Room DB** : Situés dans `src/androidTest`. Vérifient les requêtes SQL sur un vrai appareil ou émulateur.
-   **Exemple** : [`DatabaseTest.kt`](../data/src/androidTest/java/com/laurentvrevin/androidstarter/data/local/DatabaseTest.kt).

### 3. Tests UI (Compose)
-   **Screens / Navigation** : Vérifient que l'interface s'affiche correctement et réagit aux clics.
-   **Outil** : Compose UI Test.
-   **Exemple** : [`TemplateScreenTest.kt`](../feature/template/src/androidTest/java/com/laurentvrevin/androidstarter/feature/template/TemplateScreenTest.kt).

---

## 🛠️ Commandes Gradle Utiles

| Tâche | Commande | Usage |
| :--- | :--- | :--- |
| **Tous les tests unitaires** | `.\gradlew.bat test` | À lancer avant chaque commit. |
| **Tests instrumentés** | `.\gradlew.bat connectedDebugAndroidTest` | Nécessite un appareil connecté. |
| **Analyse statique** | `.\gradlew.bat ktlintCheck` | Vérifie le style du code. |
| **Vérification CI** | `.\gradlew.bat clean build` | Simulation complète de la CI. |

---

## 🧪 Bonnes Pratiques (AAA)

Respecte toujours le pattern **Arrange (Préparer)** / **Act (Agir)** / **Assert (Vérifier)**.

```kotlin
@Test
fun `load data should update state to success`() = runTest {
    // Arrange (Prepare)
    val repo = FakeRepository()
    val vm = MyViewModel(repo)

    // Act (Agir)
    vm.load()

    // Assert (Vérifier)
    assertEquals(DataState.Success, vm.state.value)
}
```

---

## 🚫 Pièges à éviter

1.  **Utiliser Mockito pour tout** : Préfère les **Fakes** (fausses implémentations simples de tes interfaces) aux Mocks. C'est plus stable et plus lisible.
2.  **Lancer des tests sans TestDispatcher** : Pour tester les Coroutines, utilise toujours `StandardTestDispatcher` ou `UnconfinedTestDispatcher`.
3.  **Tests UI trop fragiles** : Ne teste pas les détails de design (couleurs, arrondis), concentre-toi sur le comportement (clics, affichage du texte).

---
[Précédent : Réseau (Ktor)](network.md) | [Suivant : Logic de Build](build_logic.md)
