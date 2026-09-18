# Wheris 📍

> **Ta mémoire géographique.**

**Enregistre un lieu. Retrouve-le quand tu veux.**

Wheris est une application Android de **mémoire géographique personnelle**.

Elle permet d’enregistrer rapidement l’endroit où tu te trouves, de l’organiser et de le retrouver plus tard, sans avoir besoin de mémoriser une adresse ou des coordonnées GPS.

---

## 💡 Le principe

Le fonctionnement de Wheris est volontairement simple :

```text
JE SUIS ICI
    ↓
CET ENDROIT M'INTÉRESSE
    ↓
JE L'ENREGISTRE
    ↓
JE PEUX LE RETROUVER PLUS TARD
```

Une position GPS comme :

```text
49.xxxxxx, -0.xxxxxx
```

peut ainsi devenir quelque chose de réellement mémorable :

```text
🚗 Ma voiture
⛺ Ma tente
📸 Spot coucher de soleil
🍄 Mon coin à champignons
🎣 Coin de pêche
🍴 Restaurant du voyage
```

L’utilisateur choisit ce qui compte.

**Wheris se souvient d’où cela se trouve.**

---

## 🎯 Objectif principal

L’action la plus importante de Wheris est :

> **Enregistrer un lieu.**

Cette action doit pouvoir être réalisée en quelques secondes.

L’objectif UX est de permettre, dans de bonnes conditions, d’enregistrer sa position en **moins de 10 secondes**.

Wheris doit rester utilisable dans des situations réelles :

- sur un parking ;
- en randonnée ;
- dans un festival ;
- sous la pluie ;
- de nuit ;
- à une seule main ;
- avec une mauvaise connexion ;
- avec une précision GPS imparfaite.

---

## ⚡ Parcours principal

Le parcours idéal doit rester extrêmement court :

```text
Carte
  ↓
+ Ajouter
  ↓
Recherche de la position
  ↓
Confirmer la position
  ↓
Choisir une catégorie
  ↓
Enregistrer
  ↓
✓ Lieu enregistré
```

Les informations supplémentaires restent facultatives :

- nom ;
- note ;
- favori ;
- photo.

Aucun clavier, aucune adresse et aucune photo ne doivent être nécessaires pour enregistrer rapidement un lieu.

---

## 🧭 Navigation

La navigation principale de Wheris comporte trois destinations :

```text
Carte · Lieux · Plus
```

### 🗺️ Carte

Écran d’accueil principal.

Il permet de voir :

- la position actuelle ;
- les lieux enregistrés ;
- leurs marqueurs ;
- leurs catégories ;
- le lieu actuellement sélectionné.

L’action principale **+ Ajouter** permet d’enregistrer rapidement la position actuelle.

### 📍 Lieux

La mémoire géographique de l’utilisateur sous forme de liste.

Elle permet notamment de consulter :

- tous les lieux ;
- les favoris ;
- les lieux récents ;
- les catégories.

L’architecture prévoit également :

- recherche ;
- filtres ;
- tri par date ;
- tri par distance ;
- tri alphabétique.

### ⚙️ Plus

Regroupe notamment :

- catégories ;
- paramètres ;
- apparence ;
- unités ;
- application de navigation ;
- confidentialité ;
- à propos.

---

## 🔒 Local-first & vie privée

Wheris est conçu selon une approche **local-first**.

Les données principales de l’utilisateur sont stockées localement sur son téléphone.

Le fonctionnement principal de l’application ne nécessite :

- aucun compte ;
- aucun serveur Wheris ;
- aucun backend ;
- aucune synchronisation cloud ;
- aucun suivi permanent des déplacements.

La localisation est utilisée lorsque cela est nécessaire pour enregistrer ou retrouver un lieu.

Wheris ne demande pas de localisation permanente en arrière-plan pour son fonctionnement principal.

> **Tes lieux t’appartiennent.**

---

## 📴 Fonctionnement hors connexion

La donnée importante dans Wheris est le **lieu enregistré**, pas la carte.

```text
LA CARTE EST UNE REPRÉSENTATION.
LE LIEU EST LA DONNÉE.
```

Une indisponibilité de la carte ne doit jamais faire disparaître les lieux enregistrés.

Sans connexion Internet, Wheris doit permettre autant que possible de :

- consulter ses lieux ;
- créer un lieu ;
- modifier un lieu ;
- supprimer un lieu ;
- consulter et gérer les catégories ;
- consulter les favoris ;
- voir les coordonnées ;
- voir la précision GPS ;
- voir la date d’enregistrement ;
- calculer une distance ;
- calculer une direction.

---

## 📐 Localisation et précision

Wheris utilise la localisation Android pour acquérir la position de l’utilisateur.

La précision GPS est prise en compte afin de permettre à l’utilisateur de savoir si la position détectée est suffisamment fiable.

Classification initiale :

| Précision | Valeur |
|---|---:|
| Excellente | ≤ 5 m |
| Bonne | > 5 m et ≤ 15 m |
| Moyenne | > 15 m et ≤ 30 m |
| Faible | > 30 m |
| Inconnue | aucune valeur |

Une mauvaise précision ne doit pas arbitrairement bloquer l’utilisateur.

Wheris doit l’informer clairement et lui laisser la décision de continuer ou d’attendre une meilleure position.

---

## 📏 Distance et direction

Wheris peut calculer localement :

- la distance entre la position actuelle et un lieu enregistré ;
- le bearing géographique ;
- une direction cardinale.

Directions possibles :

```text
Nord
Nord-Est
Est
Sud-Est
Sud
Sud-Ouest
Ouest
Nord-Ouest
```

Ces calculs ne nécessitent aucun serveur.

---

## 🚗 Navigation externe

Wheris n’est pas une application de navigation turn-by-turn.

Lorsqu’un utilisateur souhaite rejoindre un lieu, Wheris transmet la destination à une application de navigation compatible installée sur le téléphone.

Par exemple :

- Google Maps ;
- Waze ;
- Organic Maps ;
- OsmAnd ;
- autre application compatible.

---

## 🗺️ Cartographie

La solution cartographique Android prévue pour Wheris est :

**Mapbox Maps SDK for Android**

La carte sert principalement de contexte visuel aux lieux enregistrés.

Wheris n’a pas vocation à devenir une application cartographique généraliste.

Ne font notamment pas partie du besoin principal :

- Mapbox Navigation ;
- Mapbox Directions ;
- Mapbox Search ;
- Google Maps SDK ;
- Google Places ;
- Google Routes ;
- Google Directions.

---

## 🧩 Fonctionnalités prévues pour le MVP

Le MVP de Wheris couvre notamment :

- onboarding ;
- permissions de localisation ;
- acquisition de la position ;
- gestion de la précision GPS ;
- carte ;
- ajout d’un lieu ;
- confirmation de position ;
- catégories système ;
- catégories personnalisées ;
- nom facultatif ;
- note facultative ;
- favori ;
- photo locale ;
- sauvegarde locale ;
- marqueurs sur la carte ;
- liste des lieux ;
- fiche d’un lieu ;
- modification ;
- suppression ;
- distance ;
- direction ;
- navigation externe ;
- fonctionnement dégradé hors connexion ;
- apparence claire / sombre ;
- paramètres ;
- accessibilité.

---

## 🧱 Architecture

Wheris est développé comme une application Android **multi-module**, avec une architecture inspirée de **Clean Architecture** et **MVVM**.

Organisation cible :

```text
:app

:core:common
:core:model
:core:designsystem
:core:ui
:core:navigation
:core:location
:core:map
:core:database
:core:datastore

:domain
:data

:feature:home
:feature:addpin
:feature:pins
:feature:pindetail
:feature:categories
:feature:settings
:feature:onboarding
```

Le domaine métier doit rester autant que possible indépendant d’Android.

---

## 🛠️ Stack technique

### Langage & UI

- Kotlin
- Jetpack Compose
- Material 3

### Architecture

- MVVM
- Clean Architecture
- architecture multi-module
- Coroutines
- Flow
- StateFlow

### Injection de dépendances

- Koin

### Persistance locale

- Room
- DataStore

### Localisation

- Google Play Services Location
- `FusedLocationProviderClient`

### Cartographie

- Mapbox Maps SDK for Android

### Navigation

- Navigation Compose
- routes typées

### Build

- Gradle Kotlin DSL
- Version Catalog
- Convention Plugins
- KSP

### Tests

- JUnit
- tests du domaine
- tests Room
- tests ViewModel
- tests Compose UI

---

## 🎨 Direction UX/UI

Wheris doit donner l’impression :

> **« J’ouvre. Je marque. C’est enregistré. »**

et non :

> « J’ouvre une grosse application cartographique et je cherche comment créer quelque chose. »

La carte est un support.

Le lieu enregistré reste toujours le sujet principal.

Principes visuels :

```text
Orange     = action / mémoire
Carte      = contexte
Catégorie  = identité du lieu
```

L’interface doit rester :

- simple ;
- rapide ;
- lisible ;
- accessible ;
- utilisable à une main ;
- cohérente en mode clair et sombre.

---

## ❌ Ce que Wheris n’est pas

Wheris n’est pas :

- un réseau social ;
- un moteur de recherche de lieux ;
- un concurrent de Google Maps ;
- un GPS routier ;
- un système de navigation turn-by-turn ;
- une plateforme d’avis ;
- un guide touristique ;
- un tracker permanent de déplacements ;
- un service nécessitant un compte utilisateur.

Wheris ne cherche pas à savoir quels endroits sont importants.

**C’est l’utilisateur qui décide.**

---

## 🧠 North Star

Toute décision produit doit servir cette expérience :

```text
« Je veux me souvenir de cet endroit. »

Quelques secondes plus tard :

« C'est enregistré. »
```

Puis, plus tard :

```text
« Je veux retrouver cet endroit. »

Quelques secondes plus tard :

« Le voilà. »
```

Wheris doit rester une application extrêmement simple malgré la richesse potentielle de ses usages.

---

## 🚧 État du projet

Wheris est actuellement **en cours de développement**.

Le projet est construit progressivement en privilégiant :

1. simplicité utilisateur ;
2. fiabilité des données ;
3. rapidité d’utilisation ;
4. vie privée ;
5. fonctionnement local ;
6. architecture propre ;
7. testabilité ;
8. maintenabilité.

---

## ▶️ Build

### Windows

```powershell
.\gradlew.bat :app:assembleDebug
```

### macOS / Linux

```bash
./gradlew :app:assembleDebug
```

Un build valide doit se terminer par :

```text
BUILD SUCCESSFUL
```

---

# Wheris

**Ta mémoire géographique.**

**Enregistre un lieu. Retrouve-le quand tu veux.**