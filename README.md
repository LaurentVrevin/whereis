# Android Starter Pack 🚀

Bienvenue dans ton **Android Starter Pack**. Ce projet est une base de démarrage robuste et moderne conçue pour accélérer le développement d'applications Android de qualité professionnelle.

---

## 🛠️ Stack Technique

- **Kotlin 2.0** : Utilisation du compilateur K2.
- **Jetpack Compose (BOM)** : Développement UI déclaratif.
- **Material 3** : Design system moderne et accessible.
- **Koin** : Injection de dépendances pragmatique.
- **Ktor Client** : Réseau résilient et multiplateforme.
- **Room & DataStore** : Persistance locale (Offline-First).
- **Architecture Multi-module** : Scalabilité et isolation.

---

## 📖 Parcours recommandé dans la documentation

Pour tirer le meilleur parti de ce starter, nous te conseillons de lire la documentation dans l'ordre suivant :

1.  📂 [**Vision d'Ensemble**](docs/overview.md) : Comprendre les buts et le flux du projet.
2.  🏛️ [**Architecture**](docs/architecture.md) : Découvrir les principes Clean et MVVM appliqués.
3.  📦 [**Modularisation**](docs/modularization.md) : Structure et responsabilités des modules.
4.  🚀 [**Guide de création de Feature**](docs/feature_guide.md) : Tutoriel pas à pas pour tes propres écrans.
5.  🎨 [**Design System**](docs/design_system.md) : Tokens, Styles et Composants.
6.  🔄 [**UI State & UDF**](docs/ui_state.md) : Gestion de l'état et de la réactivité.
7.  🗺️ [**Navigation**](docs/navigation.md) : Routes typées et graphe global.
8.  💉 [**Injection de Dépendances**](docs/dependency_injection.md) : Utilisation de Koin.
9.  💾 [**Données (Room & DataStore)**](docs/data.md) : Persistance et SSOT.
10. 🌐 [**Réseau (Ktor)**](docs/network.md) : Configuration et appels sécurisés.
11. 🧪 [**Stratégie de Test**](docs/testing.md) : Unitaires, instrumentés et UI.
12. 🛠️ [**Logique de Build**](docs/build_logic.md) : Convention plugins Gradle.
13. 🚩 [**Bootstrap**](docs/bootstrap.md) : **À LIRE AVANT DE DÉMARRER.**
14. 🆘 [**Dépannage**](docs/troubleshooting.md) : Solutions aux problèmes courants.

---

## 🚀 Démarrage Rapide (Bootstrap)

Pour transformer ce template en ton propre projet :

1.  Sur GitHub, clique sur **"Use this template"**.
2.  Clone ton nouveau dépôt.
3.  Lance la tâche interactive à la racine :

**Windows :**
```powershell
.\gradlew.bat bootstrapProject
```

**macOS / Linux :**
```bash
./gradlew bootstrapProject
```

4.  Suis les instructions et confirme avec `y`.
5.  Fais un **Gradle Sync** dans Android Studio.

> [!IMPORTANT]
> Pour plus de détails sur les formats attendus et la sécurité, consulte le [**Guide de Bootstrap**](docs/bootstrap.md).

---

## 🛠️ Commandes principales

- **Initialisation** : `./gradlew bootstrapProject`
- **Compilation** : `./gradlew :app:assembleDebug`
- **Tests** : `./gradlew test`
- **Lint** : `./gradlew ktlintCheck`

---

*Développé avec passion pour des applications Android performantes.*
