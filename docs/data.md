# Couche de Données 💾

Le module `:data` est le cœur de ton application. Il orchestre la persistance locale et la communication réseau suivant la stratégie **Offline-First**.

---

## 🏛️ Architecture du module :data

```text
com.exemple.app.data/
├── local/             # Room Database & DataStore
├── remote/            # Ktor Client & API Config
├── network/           # Modèles génériques (NetworkResult, etc.)
└── repository/        # Implémentations des repositories
```

---

## 🏗️ 1. Room Database (SSOT)

Nous utilisons **Room** comme **Source Unique de Vérité (SSOT)**. L'UI n'affiche que les données stockées localement.

### BaseDao
Le starter fournit un [`BaseDao<T>`](../data/src/main/java/com/laurentvrevin/androidstarter/data/local/dao/BaseDao.kt) générique pour les opérations standard.

```kotlin
@Dao
interface TaskDao : BaseDao<TaskEntity> {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>
}
```

### Flow de données
Tes DAOs doivent retourner des `Flow`. Cela permet à l'UI de réagir automatiquement dès que la base de données est modifiée (par un appel réseau ou une action utilisateur).

---

## ⚙️ 2. Préférences (DataStore)

Pour les réglages simples (thème, réglages utilisateur), utilise **Jetpack DataStore**.
Le starter fournit [`AppPreferences`](../data/src/main/java/com/laurentvrevin/androidstarter/data/local/AppPreferences.kt) pré-configuré pour la gestion du mode sombre.

---

## 🔄 3. Mappage des modèles

Pour garder un code propre, nous distinguons 3 types de modèles :

| Modèle | Localisation | Usage |
| :--- | :--- | :--- |
| **DTO** | `:data (remote)` | Reflet exact du JSON de l'API. |
| **Entity** | `:data (local)` | Reflet de la table Room. |
| **Domain Model** | `:feature (domain)` | Objet pur utilisé par la logique métier et l'UI. |

> [!TIP]
> Ne fais jamais remonter une **Entity** Room jusqu'à l'UI. Utilise une fonction d'extension `toDomain()` pour la transformer.

---

## 🛡️ 4. Stratégie Offline-First

Le flux recommandé pour un Repository est le suivant :
1.  Observer le `Flow` de Room (SSOT).
2.  Lancer un appel réseau asynchrone pour rafraîchir les données.
3.  Insérer les données reçues dans Room.
4.  Room émet automatiquement les nouvelles données via le `Flow` déjà observé par l'UI.

---

## 🧪 Tests de Données

-   **Room** : Utilise le module `:data:androidTest` pour lancer des tests sur une base de données en mémoire.
-   **Mappers** : Utilise des tests unitaires simples pour vérifier tes conversions de modèles.

---
[Précédent : Injection de Dépendances](dependency_injection.md) | [Suivant : Réseau (Ktor)](network.md)
