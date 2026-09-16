# Couche Réseau (Ktor 3) 🌐

Le starter utilise **Ktor Client 3.x** avec le moteur **OkHttp**, offrant une solution réseau moderne, performante et multiplateforme.

---

## 🛠️ Configuration (KtorClientFactory)

La configuration centralisée se trouve dans [`KtorClientFactory.kt`](../data/src/main/java/com/laurentvrevin/androidstarter/data/remote/KtorClientFactory.kt).

### Dépendance à NetworkConfig
Le `KtorClientFactory` a besoin d'un objet [`NetworkConfig`](../data/src/main/java/com/laurentvrevin/androidstarter/data/remote/NetworkConfig.kt) pour connaître la `baseUrl` et le niveau de log. Cet objet est automatiquement fourni par le **`configurationModule`** de Koin.

> [!IMPORTANT]
> Pour que le réseau fonctionne, assure-toi que `configurationModule` est bien déclaré dans ton fichier `App.kt`. Plus de détails dans le [Guide d'Injection de Dépendances](dependency_injection.md).

---

## 🛡️ Appels sécurisés (safeCall)

Tous les appels API doivent passer par la méthode `safeCall` de [`BaseRepository`](../data/src/main/java/com/laurentvrevin/androidstarter/data/base/BaseRepository.kt).

Elle capture les exceptions réseau (401, 404, 500, pas d'internet) et les transforme en un objet [`NetworkResult`](../data/src/main/java/com/laurentvrevin/androidstarter/data/network/NetworkResult.kt).

```kotlin
// Dans ton Repository
suspend fun fetchItems(): NetworkResult<List<ItemDto>> {
    return safeCall {
        client.get("items").body()
    }
}
```

---

## 🌍 Environnements (Base URL)

L'URL de base de l'API est injectée via le fichier `build.gradle.kts` du module `:app` (BuildConfig).

```kotlin
// app/build.gradle.kts
defaultConfig {
    buildConfigField("String", "API_BASE_URL", "\"https://api.votre-serveur.com/\"")
}
```

---

## 🧪 Simulation d'API (MockEngine)

Pour tester tes repositories sans serveur réel, utilise le **MockEngine** de Ktor.

```kotlin
// data/src/test/java/.../NetworkTest.kt
val mockClient = HttpClient(MockEngine) {
    engine {
        addHandler { request ->
            respond(
                content = """{"id": 1, "name": "Test"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
    }
}
```

---
[Précédent : Données](data.md) | [Suivant : Stratégie de Test](testing.md)
