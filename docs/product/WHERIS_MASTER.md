# WHERIS --- CADRAGE PRODUIT, UX ET TECHNIQUE

**Statut :** source de vérité principale pour la vision produit, le
périmètre fonctionnel, l'UX et la direction technique de Wheris.

**Référence business canonique :**
`docs/reference/WHERIS_BUSINESS_REFERENCE.md`

Tu es mon assistant principal pour la conception et le développement de
Wheris.

Ce document constitue la source de vérité principale du produit, de l'UX
et de la technique.

Tu dois t'y référer pour toutes les décisions concernant :

-   le produit ;
-   l'expérience utilisateur ;
-   le design ;
-   l'architecture Android ;
-   le modèle de données ;
-   la cartographie ;
-   la localisation ;
-   la sécurité ;
-   la vie privée ;
-   les tests ;
-   les coûts techniques ;
-   la roadmap technique.

Pour toute décision concernant :

-   le modèle économique ;
-   les offres Free / Plus / Premium ;
-   les entitlements commerciaux ;
-   les limites gratuites ;
-   les prix ;
-   les règles de paywall, achat, abonnement, renouvellement ou
    downgrade ;
-   les KPIs business ;
-   les hypothèses financières ;

la source de vérité canonique est
`docs/reference/WHERIS_BUSINESS_REFERENCE.md`.

Le présent document doit rester cohérent avec ce référentiel sans
dupliquer inutilement ses paramètres commerciaux.

Si une décision future entre en contradiction avec l'un des documents
canoniques applicables, signale explicitement la contradiction avant de
proposer une modification.

Ne complexifie jamais Wheris sans raison.

================================================== 1. IDENTITÉ DU
PRODUIT ==================================================

Nom :

Wheris

Concept :

Wheris est une application de mémoire géographique personnelle.

Elle permet à un utilisateur d'épingler un lieu ou la position d'un
objet afin de pouvoir le retrouver facilement plus tard.

Un lieu enregistré peut correspondre à absolument n'importe quoi.

Exemples :

-   une voiture ;
-   une tente ;
-   un bivouac ;
-   un restaurant ;
-   un spot photo ;
-   un point de vue ;
-   un parking ;
-   un vélo ;
-   une plage ;
-   un coin de pêche ;
-   un lieu de randonnée ;
-   un lieu de rendez-vous ;
-   une source d'eau ;
-   un spot pour drone ;
-   un lieu pour observer des animaux ;
-   un endroit découvert pendant un voyage ;
-   n'importe quel lieu personnel défini par l'utilisateur.

Wheris ne cherche pas à déterminer ce qui mérite d'être enregistré.

L'utilisateur construit sa propre mémoire géographique.

================================================== 2. VISION
==================================================

La vision de Wheris est :

« Ta mémoire géographique. »

La promesse produit est :

« Enregistre un lieu. Retrouve-le quand tu veux. »

Le fonctionnement fondamental est :

JE SUIS ICI ↓ CET ENDROIT M'INTÉRESSE ↓ JE L'ENREGISTRE ↓ JE PEUX LE
RETROUVER PLUS TARD

Wheris doit transformer une position GPS sans signification en un
souvenir géographique compréhensible par l'utilisateur.

Une coordonnée :

49.xxxxxx, -0.xxxxxx peut ainsi devenir :

🍄 Mon coin à champignons ⛺ Ma tente 🚗 Ma voiture 📸 Spot coucher de
soleil 🎣 Coin à brochets 🍴 Restaurant du voyage L'utilisateur ne doit
pas avoir besoin de mémoriser une adresse ou des coordonnées.

Wheris s'en charge pour lui.

================================================== 3. OBJECTIF PRODUIT
PRINCIPAL ==================================================

L'action la plus importante de Wheris est :

ENREGISTRER UN LIEU.

Cette action doit pouvoir être réalisée en quelques secondes.

Objectif UX :

un utilisateur doit idéalement pouvoir enregistrer sa position en moins
de 10 secondes.

Le parcours doit rester utilisable dans des situations réelles :

-   sous la pluie ;
-   dans un festival ;
-   en randonnée ;
-   sur un parking ;
-   de nuit ;
-   avec une seule main ;
-   avec une mauvaise connexion ;
-   avec une précision GPS imparfaite.

Aucune fonctionnalité future ne doit rendre cette action fondamentale
compliquée.

================================================== 4. PRINCIPES PRODUIT
================================================== Wheris doit être :

-   simple ;
-   rapide ;
-   local-first ;
-   fiable ;
-   respectueux de la vie privée ;
-   utilisable sans compte ;
-   utilisable autant que possible sans connexion ;
-   accessible ;
-   moderne ;
-   peu coûteux à exploiter ;
-   adapté aux usages urbains et outdoor.

La sophistication technique ne doit jamais devenir une fonctionnalité
visible inutilement pour l'utilisateur.

L'utilisateur ne doit pas être obligé de :

-   créer un compte ;
-   renseigner une adresse ;
-   donner un nom au lieu ;
-   ajouter une note ;
-   ajouter une photo ;
-   être connecté à Internet pour sauvegarder ses coordonnées ;
-   comprendre le fonctionnement du GPS.

================================================== 5. CE QUE WHERIS
N'EST PAS ==================================================

Wheris n'est pas :

-   un réseau social ;
-   un moteur de recherche de lieux ;
-   un concurrent de Google Maps ;
-   un GPS routier ;
-   un système de navigation turn-by-turn ;
-   une plateforme d'avis ;
-   un guide touristique ;
-   un service de géocodage ;
-   un tracker permanent de déplacement ;
-   une plateforme publicitaire fondée sur la localisation ;
-   un service de vente ou de location des données géographiques
    personnelles de l'utilisateur.

Wheris mémorise les lieux importants pour l'utilisateur.

# La navigation vers ces lieux peut être déléguée aux applications spécialisées.

# 6. VOCABULAIRE

Dans l'interface utilisateur, privilégier :

« lieu »

Exemples :

-   Ajouter un lieu
-   Mes lieux
-   Détails du lieu
-   Lieu enregistré
-   Type de lieu
-   Supprimer le lieu

Le terme « épingle » peut être utilisé lorsque le contexte
cartographique le rend naturel, mais ne doit pas devenir le vocabulaire
principal de toute l'application.

Dans le code, utiliser :

Pin

Exemples :

Pin PinId PinEntity PinDao PinRepository CreatePinUseCase

Cette distinction permet d'avoir :

-   un vocabulaire utilisateur naturel ;
-   un vocabulaire technique clair.

================================================== 7. EXPÉRIENCE
PRINCIPALE ==================================================

La navigation principale comporte trois destinations :

CARTE

LIEUX PLUS

  -------
  CARTE
  -------

La carte constitue l'écran d'accueil.

Elle permet de voir :

-   la position actuelle ;
-   les lieux enregistrés ;
-   leurs catégories ;
-   les marqueurs ;
-   le lieu sélectionné.

Elle comporte une action principale très visible :

-   Ajouter

  -------
  LIEUX
  -------

Cet écran constitue la mémoire personnelle de l'utilisateur.

Il permet de consulter :

-   tous les lieux ;
-   les favoris ;
-   les catégories ;
-   les lieux récents.

Il accueillera progressivement :

-   recherche ;
-   filtres ;
-   tri.

  ------
  PLUS
  ------

Cet espace contient notamment :

-   catégories ;
-   paramètres ;
-   apparence ;
-   préférences ;
-   à propos ;
-   confidentialité ;
-   fonctionnalités Premium lorsqu'elles existeront.

================================================== 8. FLOW PRINCIPAL ---
AJOUTER UN LIEU ==================================================

Le flow principal prioritaire est :

Carte ↓ Ajouter un lieu ↓ Acquisition de la position ↓ Visualisation sur
la carte ↓ Confirmation de la position ↓ Choix de catégorie ↓
Enregistrer ↓ Confirmation

Une branche secondaire permet l'enrichissement avant sauvegarde :

Choix de catégorie ↓ Ajouter des détails ↓ Nom / note / photo / favori
facultatifs ↓ Enregistrer ↓ Confirmation

Le passage par l'écran de détails ne fait donc pas partie du happy path
ultra-rapide. Le choix d'une catégorie doit rendre l'action
`Enregistrer` immédiatement disponible. `Ajouter des détails` reste une
action secondaire explicite.

  --------------------------
  ÉTAPE 1 --- LOCALISATION
  --------------------------

Afficher la carte.

Chercher la position actuelle.

États possibles :

-   recherche ;

-   position disponible ;

-   précision faible ;

-   permission nécessaire ;

-   permission refusée ;

-   localisation désactivée ;

-   timeout ;

-   ## erreur.

    ## ÉTAPE 2 --- CONFIRMATION

L'utilisateur doit voir le point détecté sur la carte avant de
l'enregistrer.

Afficher notamment :

Position trouvée

Précision : ± X m

Actions :

Confirmer cette position

Recentrer

Réessayer si nécessaire

Le point proposé peut être ajusté manuellement avant confirmation :

-   appui long sur le repère de position proposé ;
-   déplacement du repère sur la carte ;
-   relâchement à l'endroit souhaité ;
-   la coordonnée déplacée devient la position candidate à confirmer.

Ce geste est limité au flow d'ajout `ADD_001`. Il ne rend pas les lieux
déjà enregistrés déplaçables depuis `MAP_001`.

Après un déplacement manuel, l'interface doit indiquer que la position a
été ajustée manuellement et ne doit plus présenter la précision GPS du
fix d'origine comme la précision du point déplacé. Le fast save reste
inchangé pour l'utilisateur qui accepte directement la position
détectée.

  ------------------------------
  ÉTAPE 3 --- PRÉCISION FAIBLE
  ------------------------------

Si la précision est insuffisante :

Précision faible

Position actuelle : ± X m

Actions :

Attendre une meilleure position

Réessayer

Continuer quand même

L'utilisateur reste maître de la décision.

  --------------------------------------
  ÉTAPE 4 --- CATÉGORIE ET SAVE RAPIDE
  --------------------------------------

Question :

« Quel type de lieu enregistres-tu ? » Afficher les catégories
existantes.

Ajouter :

-   Créer une catégorie

Dès qu'une catégorie est sélectionnée, rendre disponibles :

-   action principale : Enregistrer ;
-   action secondaire : Ajouter des détails.

L'utilisateur ne doit pas être obligé d'ouvrir les détails facultatifs
pour terminer l'enregistrement.

  ---------------------------------
  ÉTAPE 5 --- DÉTAILS FACULTATIFS
  ---------------------------------

Si l'utilisateur choisit `Ajouter des détails`, permettre :

-   nom ;
-   note ;
-   photo ;
-   favori.

Ces informations restent facultatives et la sauvegarde reste disponible
à tout moment.

  --------------------------
  ÉTAPE 6 --- CONFIRMATION
  --------------------------

Afficher une confirmation courte.

Exemple :

✓ Lieu enregistré

Actions :

Voir sur la carte

Terminer

================================================== 9. CATÉGORIES
==================================================

Les catégories constituent une fonctionnalité centrale de Wheris.

Elles permettent à chaque utilisateur de construire sa propre
organisation.

Wheris fournit des catégories système pour démarrer rapidement.

Catégories initiales envisagées :

-   Voiture ;
-   Tente ;
-   Bivouac ;
-   Restaurant ;
-   Spot photo ;
-   Point de vue ;
-   Vélo ;
-   Parking ;
-   Plage ;
-   Pêche ;
-   Randonnée ;
-   Rendez-vous ;
-   Autre.

La liste exacte pourra évoluer en fonction des tests UX.

================================================== 10. CATÉGORIES
PERSONNALISÉES ==================================================

L'utilisateur peut créer ses propres catégories.

Exemples :

🍄 Champignons 🚁 Drone 🦌 Affût 💧 Source 🌅 Coucher de soleil 🧗
Escalade 🐕 Balade du chien Une catégorie personnalisée possède au
minimum :

-   un identifiant ;
-   un nom ;
-   une icône ;
-   une couleur ;
-   une date de création.

L'utilisateur peut :

-   créer une catégorie ;
-   la renommer ;
-   changer son icône ;
-   changer sa couleur ;
-   la supprimer.

Une catégorie personnalisée créée pendant l'ajout d'un lieu doit être
automatiquement sélectionnée lorsque l'utilisateur revient au flow.

La position GPS et les informations déjà renseignées ne doivent pas être
perdues.

================================================== 11. SUPPRESSION D'UNE
CATÉGORIE ==================================================

Supprimer une catégorie ne doit JAMAIS entraîner silencieusement la
suppression des lieux associés.

Si une catégorie contient des lieux :

afficher une confirmation.

Proposer :

-   déplacer les lieux vers une autre catégorie ;
-   déplacer les lieux vers « Autre » ;
-   annuler.

Les catégories système essentielles ne sont pas supprimables dans le
MVP.

================================================== 12. MODÈLE MÉTIER
PRINCIPAL ==================================================

Le modèle métier fondamental est Pin.

Conceptuellement :

data class GeoPoint( val latitude: Double, val longitude: Double )

@JvmInline value class PinId( val value: String )

@JvmInline value class CategoryId( val value: String )

data class Pin( val id: PinId, val position: GeoPoint, val categoryId:
CategoryId, val name: String?, val note: String?, val accuracyMeters:
Float?, val altitudeMeters: Double?, val isFavorite: Boolean, val
photoReference: PhotoReference?, val createdAtEpochMillis: Long, val
updatedAtEpochMillis: Long )

Ce modèle est indicatif.

Il peut être ajusté si une meilleure modélisation est justifiée.

Ne jamais utiliser de type Android dans ce modèle.

Par exemple :

INTERDIT :

android.net.Uri android.location.Location Context

================================================== 13. MODÈLE CATEGORY
==================================================

NE PAS utiliser :

enum class PinCategory

car les catégories sont dynamiques.

Utiliser conceptuellement :

data class PinCategory( val id: CategoryId, val name: String, val icon:
CategoryIcon, val color: CategoryColor, val isSystem: Boolean, val
createdAtEpochMillis: Long )

Les représentations exactes de :

CategoryIcon

CategoryColor

doivent rester indépendantes de Compose et Android dans le domaine.

================================================== 14. CATÉGORIES
SYSTÈME ET LOCALISATION
==================================================

Les catégories système doivent posséder des identifiants stables.

Ne pas utiliser directement leur texte français comme identité métier.

Exemple conceptuel :

CAR TENT BIVOUAC RESTAURANT

Le texte affiché doit provenir des ressources Android.

Cela permettra plus tard :

Voiture → Car

Tente → Tent

etc.

================================================== 15. FAVORIS
==================================================

Un lieu peut être favori.

Modèle simple :

isFavorite: Boolean Ne pas créer de système complexe de collections dans
le MVP.

L'utilisateur doit pouvoir :

-   ajouter aux favoris ;
-   retirer des favoris ;
-   filtrer les favoris lorsque le filtre sera implémenté.

================================================== 16. PHOTOS
==================================================

Un lieu peut posséder une photo facultative.

Exemples d'utilisation :

-   photographier l'entrée du parking ;
-   mémoriser l'emplacement de la tente ;
-   photographier un paysage ;
-   mémoriser un panneau ;
-   identifier visuellement un accès.

Pour le MVP :

PHOTO LOCALE UNIQUEMENT.

Pas de cloud.

Pas de backend.

Pas de compte.

Ne jamais stocker le bitmap directement dans Room.

Stocker un fichier local contrôlé par l'application et une référence
persistante.

Le domaine ne doit pas connaître android.net.Uri.

La suppression définitive d'un lieu doit gérer correctement les fichiers
associés afin d'éviter les fichiers orphelins.

================================================== 17. FICHE D'UN LIEU
==================================================

La fiche détaillée peut afficher :

-   nom ;
-   catégorie ;
-   icône ;
-   carte ;
-   photo ;
-   distance ;
-   direction ;
-   date d'enregistrement ;
-   précision GPS ;
-   altitude lorsque disponible ;
-   coordonnées ;
-   note ;
-   favori.

Actions principales :

Naviguer

Modifier

Supprimer

================================================== 18. APERÇU RAPIDE SUR
LA CARTE ==================================================

Toucher un marqueur doit afficher un aperçu rapide.

Exemple :

🚗 Ma voiture 350 m

Voiture

Actions :

Naviguer

Détails

L'utilisateur ne doit pas être obligé d'ouvrir la fiche complète pour
lancer la navigation.

================================================== 19. RECHERCHE /
FILTRES ==================================================

Prévoir l'architecture pour permettre : - recherche par nom ; - filtre
catégorie ; - favoris ; - tri par date ; - tri par distance ; - tri
alphabétique.

Ne pas complexifier prématurément l'implémentation.

================================================== 20. LOCAL-FIRST
==================================================

Les données utilisateur doivent être stockées localement par défaut.

Le fonctionnement principal ne nécessite :

-   ni compte ;
-   ni serveur Wheris ;
-   ni synchronisation ;
-   ni backend.

La création d'un lieu doit être possible sans connexion Internet si le
téléphone dispose d'une position exploitable.

Principe :

LA CARTE EST UNE REPRÉSENTATION.

LE LIEU EST LA DONNÉE.

Une panne cartographique ne doit jamais faire disparaître ou rendre
inaccessible la mémoire géographique de l'utilisateur.

================================================== 21. MODE HORS
CONNEXION ==================================================

Sans connexion, l'utilisateur doit pouvoir autant que possible :

-   consulter ses lieux ;
-   créer un lieu ;
-   modifier un lieu ;
-   supprimer un lieu ;
-   consulter les catégories ;
-   créer/modifier les catégories ;
-   consulter ses favoris ;
-   voir les coordonnées ;
-   voir la précision ;
-   voir la date ;
-   calculer la distance ;
-   calculer la direction.

Si les tuiles cartographiques ne sont pas disponibles :

afficher un fallback utile.

Exemple :

Ma tente

420 m

Nord-Est

Coordonnées 49.xxxx -0.xxxx

Précision ±7m

Carte indisponible

Ne pas développer immédiatement un système complet de cartes offline.

================================================== 22. LOCALISATION
==================================================

Android utilise :

Google Play Services Location

FusedLocationProviderClient

Cela n'implique PAS l'utilisation de Google Maps.

Utiliser lorsque pertinent :

getCurrentLocation()

et temporairement :

requestLocationUpdates() si nécessaire.

Ne pas dépendre uniquement de :

lastLocation

Gérer :

-   permission ;
-   précision approximative ;
-   précision exacte ;
-   GPS désactivé ;
-   timeout ;
-   position ancienne ;
-   absence de position ;
-   erreur technique.

Ne pas demander de localisation permanente en arrière-plan pour le MVP.

================================================== 23. PERMISSIONS
==================================================

Permissions envisagées :

ACCESS_COARSE_LOCATION

ACCESS_FINE_LOCATION

INTERNET

Ne pas demander :

ACCESS_BACKGROUND_LOCATION

Ne pas demander de permission générale de stockage si elle peut être
évitée.

Appliquer le principe du moindre privilège.

================================================== 24. PRÉCISION GPS
==================================================

Classification initiale :

Excellent \<= 5 m Good \> 5 m et \<= 15 m

Medium \> 15 m et \<= 30 m

Poor \> 30 m

Unknown absence de valeur

Ces seuils appartiennent au domaine ou à une configuration métier.

Ils ne doivent pas être codés directement dans les Composables.

================================================== 25. DISTANCE
==================================================

Wheris doit pouvoir calculer localement la distance entre :

position actuelle

et

lieu enregistré.

Utiliser une formule géographique appropriée telle que Haversine.

Ce calcul :

-   ne nécessite aucun serveur ;
-   ne nécessite aucun moteur de navigation ;
-   doit être testé unitairement.

================================================== 26. DIRECTION
==================================================

Calculer localement le bearing entre deux positions.

Résultat :

0..360°

Convertir également en direction cardinale : Nord

Nord-Est

Est

Sud-Est

Sud

Sud-Ouest

Ouest

Nord-Ouest

Attention :

un bearing calculé entre deux coordonnées n'est pas une vraie boussole
orientée physiquement avec le téléphone.

Ne pas simuler une fonctionnalité capteur sans capteurs.

================================================== 27. NAVIGATION
EXTERNE ==================================================

Wheris ne fournit pas de navigation turn-by-turn.

Bouton :

Naviguer

La destination est envoyée vers une application externe compatible.

Exemples :

-   Google Maps ;
-   Waze ;
-   Organic Maps ;
-   OsmAnd ;
-   autre application compatible.

Google Maps peut être utilisé comme application externe.

# Le SDK Google Maps ne doit pas être intégré à Wheris.

# 28. CARTOGRAPHIE

Solution Android initiale :

Mapbox Maps SDK for Android.

Utiliser une cartographie reposant sur OpenStreetMap lorsque pertinent.

Mapbox sert principalement de moteur de rendu cartographique.

Ne pas intégrer automatiquement :

-   Mapbox Navigation ;
-   Mapbox Directions ;
-   Mapbox Search.

Ne pas intégrer :

-   Google Maps SDK ;
-   Google Maps Compose ;
-   Google Places ;
-   Google Routes ;
-   Google Directions.

================================================== 29. OPENSTREETMAP
==================================================

Les données OpenStreetMap sont ouvertes selon leurs conditions de
licence.

Cela ne signifie pas que les serveurs publics de tuiles OpenStreetMap
constituent une infrastructure gratuite et illimitée pour une
application commerciale.

Ne jamais construire l'application autour de l'hypothèse :

« les tuiles OSM sont gratuites et illimitées ».

Respecter :

-   attribution ;
-   licence ;
-   politiques d'utilisation ;
-   limites du fournisseur cartographique utilisé.

================================================== 30. ABSTRACTION
CARTOGRAPHIQUE ==================================================

Mapbox doit être strictement isolé.

Architecture conceptuelle :

Features ↓ Wheris Map API ↓ Map implementation ↓ Mapbox

Les features ne doivent jamais connaître :

MapView

MapboxMap

CameraOptions

Point Mapbox

Style Mapbox

PointAnnotation

etc.

Une API neutre peut ressembler à :

@Composable fun WherisMap( userPosition: GeoPoint?, markers:
List`<WherisMapMarker>`{=html}, selectedMarkerId: String?, cameraTarget:
GeoPoint?, onMapReady: () -\> Unit, onMarkerClick: (String) -\> Unit,
onMapClick: (GeoPoint) -\> Unit, onCameraMovedByUser: () -\> Unit,
modifier: Modifier = Modifier )

La signature exacte pourra être adaptée.

Ne pas créer un moteur cartographique abstrait gigantesque. L'objectif
est uniquement d'empêcher Mapbox de contaminer toute l'architecture.

Comportements caméra/interactions canoniques de `MAP_001` :

-   à l'ouverture, dès qu'une position utilisateur exploitable est
    disponible, centrer automatiquement une fois la caméra sur cette
    position avec un niveau de zoom local utile, sauf si l'utilisateur a
    déjà déplacé volontairement la caméra ;
-   ne pas laisser l'expérience d'ouverture sur une vue globale de la
    Terre lorsqu'une position utilisateur exploitable est disponible ;
-   si la position utilisateur n'est pas disponible mais que des lieux
    existent, privilégier un cadrage utile des lieux enregistrés plutôt
    qu'une vue globale non informative ;
-   le contrôle Recentrer ramène explicitement à la position utilisateur
    lorsqu'elle est disponible ;
-   lorsqu'un lieu est sélectionné et `SURF_MAP_001` affiché, un tap sur
    une zone libre de la carte désélectionne le lieu et ferme la quick
    detail ;
-   un tap sur un autre lieu remplace la sélection courante ;
-   ces comportements ne doivent jamais modifier les coordonnées
    persistées d'un lieu.

Dans `ADD_001`, le repère candidat peut être déplacé par appui long +
drag avant confirmation. Ce geste manipule uniquement le brouillon de
position en cours d'ajout.

Cela doit permettre d'envisager plus tard :

MapLibre

PMTiles

offline

autre fournisseur.

================================================== 31. COÛTS
==================================================

La maîtrise des coûts est une contrainte d'architecture.

Chaque dépendance à un service facturé à l'usage doit être signalée.

En particulier :

-   cartographie ;
-   géocodage ;
-   routing ;
-   stockage ;
-   cloud ;
-   synchronisation ;
-   photos ;
-   analytics payants.

Ne jamais supposer qu'un free tier actuel sera permanent.

Lorsqu'un service externe est ajouté :

indiquer :

-   pourquoi ;
-   tarification pertinente ;
-   quota ;
-   dépendance fournisseur ;
-   alternative éventuelle.

Les hypothèses économiques globales, prix commerciaux, conversions, unit
economics, prévisionnels et seuils de rentabilité appartiennent à
`docs/reference/WHERIS_BUSINESS_REFERENCE.md` et à son modèle financier
associé.

Ne pas dupliquer ces chiffres ici sauf lorsqu'ils ont une conséquence
directe sur une décision d'architecture.

================================================== 32. PRIVACY
================================================== La confidentialité
doit être une caractéristique naturelle de Wheris.

Par défaut :

les lieux restent sur le téléphone.

Pas de compte obligatoire.

Pas d'envoi vers un backend Wheris.

Pas de tracking permanent.

Pas de stockage serveur nécessaire au fonctionnement principal.

Une future synchronisation cloud devra être :

-   volontaire ;
-   explicite ;
-   sécurisée ;
-   désactivable.

La présence d'un entitlement Plus ou Premium n'autorise jamais, à elle
seule, l'envoi de données géographiques vers un backend.

Toute fonctionnalité cloud payante doit également respecter
`docs/product/SECURITY_PRIVACY.md`.

================================================== 33. STACK ANDROID
==================================================

Utiliser :

-   Kotlin ;
-   Jetpack Compose ;
-   Material 3 ;
-   Coroutines ;
-   Flow ;
-   StateFlow ;
-   Navigation Compose ;
-   Koin ;
-   Room ;
-   DataStore ;
-   FusedLocationProviderClient ;
-   Mapbox Maps SDK Android ;
-   Gradle Kotlin DSL ;
-   Version Catalog ;
-   KSP ;
-   JUnit ;
-   tests Compose.

Architecture :

Clean Architecture MVVM

Modularisation

================================================== 34. ARCHITECTURE
GRADLE ==================================================

Structure cible :

:app

:core:common :core:model :core:designsystem :core:ui :core:navigation
:core:location :core:map :core:database :core:datastore

:domain :data

:feature:home :feature:addpin :feature:pins :feature:pindetail
:feature:categories :feature:settings :feature:onboarding

Ne pas ajouter des modules uniquement pour anticiper des fonctionnalités
hypothétiques.

================================================== 35. RESPONSABILITÉ
DES FEATURES ==================================================

feature:home

-   écran principal ;
-   carte ;
-   position ;
-   lieux ;
-   sélection marqueur ;
-   aperçu rapide ;
-   recentrage ;
-   bouton Ajouter.

feature:addpin

-   acquisition position ;
-   confirmation ;
-   précision ;
-   choix catégorie ;
-   création catégorie depuis le flow ;
-   détails ;
-   sauvegarde ;
-   confirmation.

feature:pins

-   Mes lieux ;
-   liste ;
-   favoris ;
-   recherche ;
-   filtres ;
-   tri.

feature:pindetail

-   détails ;
-   distance ;
-   direction ;
-   modification ;
-   suppression ;
-   navigation externe.

feature:categories

-   liste catégories ;
-   création ;
-   modification ;
-   suppression ;
-   icône ;
-   couleur.

feature:settings

-   préférences ;
-   thème ;
-   unités ;
-   navigation externe favorite ;
-   à propos ;
-   confidentialité.

feature:onboarding

-   introduction ;
-   explication du concept ;
-   permissions ;
-   premier lancement.

================================================== 36. CORE
==================================================

core:common

Utilitaires techniques réellement partagés.

Exemple :

Clock IdGenerator DispatcherProvider

Éviter le module fourre-tout.

------------------------------------------------------------------------

core:model

Modèles neutres réellement nécessaires à plusieurs couches.

Ne pas y déplacer arbitrairement tout le domaine.

------------------------------------------------------------------------

core:designsystem

Design System Wheris.

Aucune logique métier.

Aucune dépendance :

-   Room ;

-   Mapbox ;

-   location ;

-   ## Koin.

core:ui

Composants UI fonctionnels partagés.

------------------------------------------------------------------------

core:navigation

Navigation interne et contrats nécessaires.

------------------------------------------------------------------------

core:location

Implémentation Android de la localisation.

Aucun type Android ne doit sortir de son API publique si cela contamine
le domaine.

------------------------------------------------------------------------

core:map

Intégration Mapbox.

Aucun type Mapbox ne sort du module.

------------------------------------------------------------------------

core:database

Room.

------------------------------------------------------------------------

core:datastore

Préférences.

================================================== 37. DOMAIN
==================================================

:domain doit être Kotlin pur.

Il contient : - modèles métier ; - repository interfaces ; - use cases
; - règles métier ; - calculs géographiques.

Aucune dépendance Android.

Repositories conceptuels :

interface PinRepository {

    fun observePins(): Flow<List<Pin>>

    fun observePin(pinId: PinId): Flow<Pin?>

    suspend fun savePin(pin: Pin)

    suspend fun updatePin(pin: Pin)

    suspend fun deletePin(pinId: PinId)

}

interface CategoryRepository {

    fun observeCategories(): Flow<List<PinCategory>>

    fun observeCategory(
       categoryId: CategoryId
    ): Flow<PinCategory?>

    suspend fun createCategory(
      category: PinCategory
    )

    suspend fun updateCategory(
      category: PinCategory
    )

    suspend fun deleteCategory(
      categoryId: CategoryId,
      replacementCategoryId: CategoryId?
    )

}

interface UserLocationRepository {

    fun observeUserLocation(): Flow<UserLocationState>
    suspend fun getCurrentLocation(): Result<UserLocation>

}

Les interfaces exactes pourront évoluer si nécessaire.

================================================== 38. USE CASES
==================================================

Use cases possibles :

ObservePinsUseCase

ObservePinUseCase

CreatePinUseCase

UpdatePinUseCase

DeletePinUseCase

ObserveCategoriesUseCase

CreateCategoryUseCase

UpdateCategoryUseCase

DeleteCategoryUseCase

GetCurrentLocationUseCase

CalculateDistanceUseCase

CalculateBearingUseCase

GetCardinalDirectionUseCase

EvaluateLocationAccuracyUseCase

SearchPinsUseCase

FilterPinsUseCase

Ne pas créer mécaniquement un UseCase pour chaque méthode de Repository.

Un UseCase doit apporter : - une règle métier ; - une orchestration ; -
une validation ; - une intention métier utile.

================================================== 39. CRÉATION D'UN PIN
==================================================

CreatePinUseCase doit notamment pouvoir :

-   valider les coordonnées ;
-   nettoyer les textes ;
-   générer l'identifiant ;
-   obtenir l'heure ;
-   vérifier la catégorie ;
-   créer le Pin ;
-   demander sa persistance.

Utiliser des abstractions testables :

Clock

IdGenerator

Éviter :

System.currentTimeMillis()

UUID.randomUUID()

directement dispersés dans les ViewModels.

================================================== 40. ROOM
==================================================

Room est la source de vérité locale pour :

-   lieux ;
-   catégories ;
-   métadonnées persistantes associées.

Créer notamment :

WherisDatabase

PinEntity CategoryEntity

PinDao

CategoryDao

Mappers séparés.

Prévoir :

-   foreign keys ;
-   indices ;
-   transactions ;
-   migrations.

Les lectures réactives utilisent Flow lorsque pertinent.

Les écritures utilisent suspend.

Ne jamais exposer directement une Entity Room au domaine ou à une
feature.

Ne pas utiliser destructiveMigration comme stratégie de production
normale.

================================================== 41. DATASTORE
==================================================

DataStore sert uniquement aux préférences.

Exemples :

-   onboarding terminé ;
-   thème ;
-   système d'unités ;
-   application de navigation favorite ;
-   préférences d'affichage ;
-   paramètres de localisation.

Ne jamais stocker la liste des lieux dans DataStore.

================================================== 42. MVVM
==================================================

Chaque écran complexe suit le pattern :

XxxRoute.kt XxxScreen.kt

XxxViewModel.kt

XxxUiState.kt

XxxUiAction.kt

XxxUiEffect.kt

Route :

-   obtient le ViewModel ;
-   collecte UiState ;
-   collecte UiEffect ;
-   gère navigation ;
-   gère interactions système ;
-   gère permissions si nécessaire.

Screen :

-   stateless ;
-   reçoit UiState ;
-   reçoit callbacks ;
-   aucune injection ;
-   aucun repository ;
-   aucun stockage ;
-   aucun ViewModel ;
-   aucun Mapbox direct.

ViewModel :

-   StateFlow ;
-   use cases ;
-   aucune Activity ;
-   aucun Context ;
-   aucun NavController ;
-   aucun type Mapbox ;
-   aucune Entity Room.

================================================== 43. UI EFFECTS
==================================================

Utiliser UiEffect pour les événements ponctuels lorsque pertinent.

Exemples : NavigateToPinDetails

OpenExternalNavigation

ShowPermissionSettings

ShowSnackbar

PinCreated

Ne pas utiliser UiState pour stocker artificiellement des événements
consommables.

================================================== 44. DESIGN SYSTEM
==================================================

Créer un module dédié :

:core:designsystem

Structure cible :

component/ button/ WherisButton.kt WherisButtonDefaults.kt
WherisButtonSize.kt

card/ WherisCard.kt WherisPinCard.kt

chip/ WherisChip.kt WherisCategoryChip.kt WherisFilterChip.kt

input/ WherisTextField.kt WherisSearchField.kt

topbar/ WherisTopBar.kt

navigation/ WherisBottomBar.kt WherisFloatingActionButton.kt feedback/
WherisLoadingIndicator.kt WherisErrorState.kt WherisOfflineState.kt
WherisEmptyState.kt

location/ WherisAccuracyBadge.kt WherisDistanceIndicator.kt
WherisDirectionIndicator.kt

category/ WherisCategoryIcon.kt WherisCategoryCard.kt
WherisCategoryChip.kt

foundation/ WherisShapes.kt WherisBorders.kt WherisElevation.kt
WherisSpacing.kt

pattern/ WherisScreenContainer.kt WherisSectionBlock.kt
WherisBottomActionContainer.kt

icon/ WherisIcons.kt

theme/ WherisColorScheme.kt WherisTheme.kt WherisTypography.kt
WherisThemePreview.kt

Le Design System :

-   accepte Modifier ;

-   ne contient aucune logique métier ;

-   n'utilise pas Mapbox ;

-   n'utilise pas Room ;

-   n'utilise pas Koin ;

-   n'utilise pas FusedLocationProviderClient ;

-   possède des previews ;

-   supporte clair/sombre ;

-   # respecte l'accessibilité.

# 45. IDENTITÉ VISUELLE

Identité :

chaude

moderne

minimaliste

outdoor

premium sans être luxueuse

Couleur principale :

#F97316

Palette claire initiale :

Primary Orange #F97316

Secondary Orange #FB923C

Light Orange #FDBA74

Background #FFF7ED

Surface #FFFFFF

Primary Text #1C1917

Secondary Text #57534E

Border #E7E5E4

Success #22C55E

Error #DC2626

Information #2563EB

Créer une vraie palette sombre.

Ne pas simplement inverser les couleurs.

================================================== 46. ACCESSIBILITÉ
==================================================

Respecter :

-   touch targets ;
-   TalkBack ;
-   content descriptions ;
-   contraste ;
-   font scaling ;
-   états disabled ;
-   états loading ;
-   ordre de lecture ;
-   messages d'erreur compréhensibles.

Une catégorie ne doit pas être identifiable uniquement par sa couleur.

Utiliser :

icône + couleur + texte lorsque nécessaire.

================================================== 47.
INTERNATIONALISATION ==================================================

Langue initiale :

français.

Tous les textes métier doivent être dans :

strings.xml

Aucun texte métier codé directement dans les Composables. Préparer
l'anglais.

Les noms affichés des catégories système doivent être localisables.

================================================== 48. KOIN
==================================================

Séparer les modules DI.

Exemples :

databaseModule

datastoreModule

locationModule

dataModule

domainModule

mapModule

navigationModule

homeFeatureModule

addPinFeatureModule

pinsFeatureModule

pinDetailFeatureModule

categoriesFeatureModule

settingsFeatureModule

onboardingFeatureModule

Ne pas créer un AppModule gigantesque.

Le Design System ne nécessite pas d'injection.

================================================== 49. MAPBOX TOKENS
================================================== Ne jamais coder un
token secret directement dans Kotlin.

Distinguer :

PUBLIC ACCESS TOKEN

et

SECRET DOWNLOAD TOKEN

Le token public nécessaire au runtime peut être embarqué selon les
recommandations Mapbox.

Le secret de téléchargement éventuel ne doit jamais être embarqué dans
l'APK.

Utiliser lorsque pertinent :

local.properties

Gradle properties

BuildConfig

resValue

Manifest placeholders

.gitignore

Documenter précisément la configuration locale.

================================================== 50. TESTS
==================================================

Le projet doit être conçu pour être testé dès le début.

Tests unitaires prioritaires :

-   validation GeoPoint ;
-   distance ;
-   bearing ;
-   directions cardinales ;
-   précision GPS ;
-   création Pin ;
-   modification Pin ;
-   suppression Pin ;
-   création Category ;
-   modification Category ;
-   suppression Category ;
-   réaffectation des lieux ;
-   repositories ;
-   mappers ;
-   ViewModels ;
-   erreurs GPS ;
-   timeout ;
-   favoris.

Room :

-   DAO ;
-   relations ;
-   migrations ;
-   foreign keys ;
-   transactions.

Compose :

-   états loading ;
-   erreurs ;
-   permission ;
-   ajout ;
-   catégorie ;
-   création catégorie ;
-   liste ;
-   détails ;
-   suppression ;
-   carte indisponible ;
-   dark mode lorsque pertinent.

L'intégration cartographique doit être suffisamment isolée pour ne pas
rendre tous les tests dépendants de Mapbox.

================================================== 51. BUSINESS MODEL
==================================================

Le modèle économique de Wheris est défini canoniquement dans :

`docs/reference/WHERIS_BUSINESS_REFERENCE.md`

Le présent document n'en conserve que les principes structurants
nécessaires au cadrage produit et technique.

Architecture commerciale de référence :

FREE → PLUS → PREMIUM

FREE

-   doit être un vrai produit utilisable, pas une simple démo ;
-   permet de comprendre et répéter la boucle fondamentale de Wheris ;
-   peut comporter une limite de capacité locale ;
-   ne doit jamais supprimer, masquer ou rendre inaccessibles des lieux
    déjà enregistrés pour forcer un paiement.

PLUS

-   est un achat unique non récurrent ;
-   monétise principalement l'usage local intensif ;
-   son entitlement canonique initial est la capacité d'enregistrer des
    lieux locaux sans limite commerciale ;
-   un achat Plus acquis ne doit pas être révoqué à cause d'une
    évolution future de prix.

PREMIUM

-   est un abonnement destiné aux services à valeur ou coûts récurrents
    ;
-   inclut les droits locaux correspondant à Plus pendant la durée de
    l'abonnement ;
-   peut accueillir plus tard la sauvegarde cloud, la restauration
    cloud, la synchronisation multi-appareils, le
    stockage/synchronisation de photos et d'autres capacités nécessitant
    une infrastructure récurrente ;
-   ne doit pas devenir un prétexte pour retirer artificiellement des
    fonctions fondamentales déjà promises au produit local.

Principes business non négociables :

-   pas de publicité comme moteur du modèle actuel ;
-   pas de vente ou location de données personnelles ou de coordonnées ;
-   les données existantes de l'utilisateur ne sont jamais prises en
    otage par un paywall ;
-   une fin d'abonnement ne supprime jamais automatiquement les lieux
    locaux ;
-   la monétisation ne doit pas casser la philosophie local-first ;
-   le cloud reste explicite et opt-in ;
-   un paywall ne doit pas précéder la démonstration de la valeur
    fondamentale de Wheris ;
-   la restauration des achats doit utiliser les mécanismes appropriés
    de la plateforme sans imposer artificiellement un compte Wheris pour
    le produit local.

Les prix, limites Free, timings d'upsell, conversions et autres
paramètres commerciaux actifs sont définis exclusivement dans
`docs/reference/WHERIS_BUSINESS_REFERENCE.md`.

IMPORTANT :

Ces paramètres sont des décisions ou hypothèses business selon leur
statut dans le référentiel. Ils ne constituent pas des constantes
produit immuables et ne doivent pas être recopiés ici comme une deuxième
source de vérité.

Le référentiel business possède les valeurs commerciales actives, leur
statut, les règles de downgrade, les hypothèses financières, les KPIs et
le prévisionnel 36 mois.

En cas d'évolution d'un prix, d'une limite ou d'un entitlement, mettre à
jour d'abord `docs/reference/WHERIS_BUSINESS_REFERENCE.md`, puis
propager uniquement les conséquences nécessaires.

Google Play Billing, les paywalls et les flows d'achat/abonnement ne
font pas partie du MVP technique actif tant qu'une étape de monétisation
dédiée n'a pas été explicitement lancée.

================================================== 52. MVP
==================================================

Le MVP est piloté en deux niveaux complémentaires :

### 52.1 Slice de validation prioritaire

Avant d'élargir l'effort à toute la surface fonctionnelle, Wheris doit
d'abord prouver sa boucle fondamentale sur une slice cohérente :

-   ouverture / accès à la Carte ;
-   permission et acquisition de localisation foreground ;
-   confirmation honnête de la position et de sa précision ;
-   choix d'une catégorie ;
-   sauvegarde directe depuis la sélection de catégorie, sans détails
    obligatoires ;
-   persistance locale fiable ;
-   retour ultérieur dans Carte ou Lieux ;
-   reconnaissance du lieu ;
-   distance / direction lorsque disponibles ;
-   lancement volontaire d'une navigation externe ;
-   accès aux lieux sauvegardés même si la carte est indisponible.

Cette slice ne supprime pas les fonctionnalités du MVP fonctionnel
cible. Elle définit l'ordre de validation : la boucle
`enregistrer → retrouver` doit être convaincante avant d'investir
fortement dans les surfaces secondaires.

Les catégories personnalisées, les détails riches, les photos, la
recherche avancée, les filtres, le tri et les réglages étendus peuvent
être construits ensuite selon leur dépendance réelle, mais ne doivent
pas retarder la preuve du comportement fondamental.

### 52.2 Gate produit avant extension ou monétisation

La validation doit collecter au minimum :

-   taux `installation / utilisateur éligible → premier lieu enregistré`
    ;
-   **time-to-first-value** : temps
    `premier lancement → premier lieu enregistré`, afin de mesurer
    séparément la friction onboarding + permission + premier save ;
-   temps médian et p90 du happy path de sauvegarde lorsque la position
    est déjà exploitable ;
-   proportion `1er lieu → 3e lieu` ;
-   proportion `3e lieu → 10e lieu` ;
-   rétention D30 et D90 ;
-   fréquence de retour pour retrouver un lieu ;
-   échecs de sauvegarde et de récupération ;
-   signal qualitatif de réflexe d'usage : l'utilisateur pense
    spontanément à Wheris lorsqu'un endroit mérite d'être mémorisé.

Le **time-to-first-value** et le temps de **fast save** sont deux
mesures différentes : le premier couvre l'expérience de découverte
complète, le second mesure uniquement l'efficacité du cœur d'interaction
une fois une position exploitable disponible.

Le signal « réflexe Wheris » reste qualitatif par défaut. Il ne doit pas
être transformé artificiellement en pourcentage ou score tant qu'un
protocole de recherche utilisateur explicite n'a pas été défini.

Le seul seuil produit déjà canonique reste l'objectif d'un save simple
proche de moins de 10 secondes lorsque les conditions de localisation le
permettent. Les autres seuils doivent venir de données réelles plutôt
que d'être inventés dans la documentation.

Une décision de monétisation, de scale acquisition ou d'élargissement
important du scope ne doit pas reposer uniquement sur le nombre brut
d'installations.

### 52.3 MVP fonctionnel cible

Le MVP fonctionnel cible doit couvrir :

-   onboarding ;
-   permissions ;
-   carte ;
-   position actuelle ;
-   ajout d'un lieu ;
-   confirmation GPS ;
-   précision GPS ;
-   catégories système ;
-   catégories personnalisées ;
-   nom ;
-   note ;
-   favori ;
-   photo locale ;
-   sauvegarde Room ;
-   marqueurs ;
-   liste Mes lieux ;
-   détails ;
-   modification ;
-   suppression ;
-   distance ;
-   direction cardinale ;
-   navigation externe ;
-   fonctionnement dégradé sans carte ;
-   paramètres essentiels ;
-   thème clair/sombre ;
-   accessibilité.

Le MVP peut être développé, testé et validé sans monétisation active.

Le business model est défini en amont afin d'éviter une impasse
économique, mais l'intégration du billing et des paywalls reste une
étape produit/technique dédiée.

================================================== 53. HORS PÉRIMÈTRE
INITIAL ==================================================

Ne pas développer immédiatement :

-   authentification ;
-   compte utilisateur ;
-   backend ;
-   Google Play Billing dans le MVP initial ;
-   paywall actif ;
-   achat Plus actif ;
-   abonnement Premium actif ;
-   restauration d'achat/abonnement ;
-   backend d'entitlements propriétaire ;
-   synchronisation cloud ;
-   partage social ;
-   amis ;
-   commentaires ;
-   avis ;
-   routing interne ;
-   navigation turn-by-turn ;
-   Google Maps SDK ;
-   Google Places ;
-   Mapbox Navigation ;
-   Mapbox Search ;
-   cartes offline complètes ;
-   PMTiles ;
-   widgets ;
-   Wear OS ;
-   Apple Watch ;
-   iOS ;
-   KMP ;
-   IA ;
-   publicité ;
-   tracking permanent ;
-   localisation background.

================================================== 54. ÉVOLUTIONS
POSSIBLES ==================================================

L'architecture doit pouvoir accueillir raisonnablement plus tard :

-   activation du modèle Free / Plus / Premium défini dans
    `docs/reference/WHERIS_BUSINESS_REFERENCE.md` ;
-   Google Play Billing lorsque l'étape de monétisation est
    explicitement ouverte ;
-   restauration et gestion robuste des entitlements ;
-   cloud ;
-   synchronisation ;
-   multi-appareils ;
-   partage ;
-   collections ;
-   historique ;
-   import/export ;
-   cartes offline ;
-   MapLibre ;
-   PMTiles ;
-   boussole ;
-   capteurs ;
-   widget Android ;
-   Wear OS ;
-   iOS ;
-   Kotlin Multiplatform.

Ne pas construire aujourd'hui des abstractions complexes uniquement
parce que ces possibilités existent.

================================================== 55. PRINCIPES DE
DÉVELOPPEMENT ==================================================

Lorsque tu génères du code pour Wheris :

1.  travaille étape par étape ;

2.  ne génère jamais l'application entière d'un coup ;

3.  explique l'objectif de l'étape ;

4.  donne les fichiers concernés ;

5.  donne leurs chemins complets ;

6.  donne le contenu complet des fichiers ;

7.  inclus tous les imports ;

8.  n'utilise pas de pseudo-code ;

9.  n'utilise pas « ... » pour cacher du code ;

10. vérifie les packages ;

11. vérifie les dépendances Gradle ;

12. vérifie le graphe des modules ;

13. respecte Clean Architecture ;

14. respecte MVVM ;

15. évite la surarchitecture ;

16. privilégie les APIs officielles ;

17. vérifie les versions actuelles avant d'ajouter une dépendance ;

18. ne mets pas arbitrairement toutes les dépendances à jour ;

19. explique les changements de versions ;

20. signale les coûts potentiels ;

21. signale les risques de vendor lock-in ;

22. signale les conséquences pour iOS/KMP lorsque pertinentes ;

23. donne les tests ;

24. donne les commandes Gradle de validation ;

25. arrête-toi après chaque grande étape ;

26. attends ma validation avant de continuer.

================================================== 56. DÉPENDANCES
==================================================

Utiliser Version Catalog.

Ne jamais dupliquer inutilement une dépendance.

Compose doit utiliser Compose BOM lorsque pertinent.

Material 3 ne doit pas être déclaré plusieurs fois avec des versions
contradictoires.

Kotlin et KSP doivent être compatibles.

Room compiler doit être appliqué uniquement là où nécessaire.

Ajouter les plugins Android Library aux modules Android Library.

Vérifier systématiquement la compatibilité entre :

AGP Kotlin

Compose

KSP

Room

Koin

Navigation

Lifecycle

Mapbox

Google Play Services Location

Ne jamais inventer une version.

Pour les dépendances susceptibles d'avoir évolué, consulter la
documentation officielle au moment de leur intégration.

================================================== 57. ORDRE DE
DÉVELOPPEMENT ==================================================

L'ordre de développement est piloté par **deux rails**.

Le **Rail A --- Slice de validation** doit atteindre le plus vite
possible une version réellement testable de la boucle
`enregistrer → retrouver`, avec le niveau minimum de Design System, de
domaine et d'infrastructure nécessaire à sa fiabilité.

Le **Rail B --- MVP fonctionnel complet** enrichit ensuite le produit
sans rallonger ni fragiliser le happy path validé.

Principe de gouvernance : ne pas terminer une couche « pour être complet
» si cette complétude n'est pas nécessaire à la prochaine preuve
utilisateur. Une dépendance, un composant ou un écran secondaire peut
être anticipé uniquement lorsqu'il débloque concrètement le Rail A ou
protège une contrainte critique de sécurité, données, accessibilité ou
release.

  --------------------------------------------
  RAIL A --- SLICE DE VALIDATION PRIORITAIRE
  --------------------------------------------

ÉTAPE A1 --- Socle minimal compilable

-   architecture et modules réellement nécessaires ;
-   Gradle / Version Catalog / packages ;
-   Koin minimal ;
-   application vide compilable ;
-   Design System **minimal slice-ready**, pas bibliothèque complète ;
-   Room initialisée ;
-   location/map initialisées derrière leurs frontières ;
-   secrets et configuration locale propres ;
-   tests de smoke/build du socle.

ÉTAPE A2 --- Domaine et persistance minimum

-   `GeoPoint` ;
-   `Pin` ;
-   identité de catégorie système stable ;
-   repository minimal save/read ;
-   Room : `PinEntity`, catégorie système minimale, DAO nécessaires au
    save/retrieve ;
-   mappings ;
-   protection contre perte/corruption ;
-   tests unitaires et Room ciblés.

La création, modification, suppression et réaffectation complètes des
catégories personnalisées ne sont pas requises pour passer cette étape.

ÉTAPE A3 --- Localisation foreground et permission

-   permission education/récupération nécessaire au test ;
-   `FusedLocationProviderClient` isolé ;
-   acquisition bornée ;
-   précision ;
-   timeout ;
-   services désactivés ;
-   erreur technique ;
-   arrêt/cancellation corrects ;
-   aucun tracking background.

ÉTAPE A4 --- Carte minimale + save direct

Construire uniquement ce qui est nécessaire au happy path :

``` text
MAP_001
→ ADD_001
→ ADD_002
→ Enregistrer
→ ADD_004
```

Inclure :

-   carte / fallback utile ;
-   confirmation honnête de la position ;
-   sélection d'une catégorie système ;
-   `Enregistrer` comme action primaire ;
-   persistance réelle ;
-   prévention des doubles sauvegardes ;
-   erreur/retry sans perte du draft.

`ADD_003` et la création de catégorie personnalisée ne doivent pas
bloquer cette preuve.

ÉTAPE A5 --- Retrieval minimal complet

Construire la seconde moitié de la promesse :

``` text
MAP_001 ou PLACES_001
→ lieu sauvegardé
→ aperçu / PLACE_001
→ distance / direction lorsque disponibles
→ navigation externe volontaire
```

Inclure :

-   liste locale simple ;
-   identité compréhensible d'un lieu sans nom ;
-   marqueur et aperçu rapide lorsque la carte fonctionne ;
-   détail local ;
-   lancement de navigation externe ;
-   données accessibles hors connexion ;
-   carte indisponible ≠ lieu perdu.

ÉTAPE A6 --- Fiabilité et états dégradés critiques

Valider au minimum :

-   mauvaise précision avec choix utilisateur ;
-   timeout / GPS désactivé / permission refusée ;
-   carte indisponible avec GPS exploitable ;
-   offline ;
-   échec de persistance + retry ;
-   absence d'application de navigation compatible ;
-   conservation du draft sur détours récupérables ;
-   accessibilité critique et Light/Dark sur les écrans de la slice.

ÉTAPE A7 --- Instrumentation de validation privacy-minimized

Mesurer sans collecter de contenu géographique sensible :

-   activation → premier lieu ;
-   time-to-first-value `premier lancement → premier lieu` ;
-   fast save médian et p90 une fois la position exploitable ;
-   abandon / erreurs / retry ;
-   `1er lieu → 3e lieu` ;
-   `3e lieu → 10e lieu` ;
-   D30 / D90 ;
-   retours pour retrouver un lieu ;
-   signal qualitatif « réflexe Wheris ».

Le « réflexe Wheris » reste une observation qualitative tant qu'un
instrument de recherche explicite n'a pas été défini.

ÉTAPE A8 --- Test utilisateur / beta de la slice

La slice est prête pour validation lorsque :

-   `enregistrer → retrouver` fonctionne réellement sur appareil ;
-   le save direct ne nécessite ni nom, ni note, ni photo, ni catégorie
    personnalisée ;
-   les données survivent aux échecs récupérables ;
-   les états carte/GPS/offline sont honnêtes ;
-   les métriques de validation peuvent être observées sans violer
    `docs/product/SECURITY_PRIVACY.md`.

Aucun seuil d'activation/rétention supplémentaire n'est inventé ici. Le
seul objectif chiffré déjà canonique reste un save simple proche de
moins de 10 secondes lorsque la position est disponible.

  ------------------------------------
  RAIL B --- MVP FONCTIONNEL COMPLET
  ------------------------------------

Après que le Rail A est fonctionnel et testable, compléter
progressivement :

ÉTAPE B1 --- Design System et composants secondaires

-   compléter les composants réellement consommés par les écrans
    restants ;
-   stress cases, variantes, états et documentation ;
-   éviter de construire des composants spéculatifs sans écran
    consommateur.

ÉTAPE B2 --- Catégories personnalisées complètes

-   création depuis Add Place et gestion ;
-   modification ;
-   suppression ;
-   réaffectation transactionnelle ;
-   protection des catégories système ;
-   tests de non-perte de lieux.

ÉTAPE B3 --- Enrichissement facultatif

-   `ADD_003` ;
-   nom ;
-   note ;
-   favori ;
-   photo locale selon la politique d'acquisition approuvée ;
-   conservation du draft.

ÉTAPE B4 --- Gestion des lieux

-   modification ;
-   suppression ;
-   favoris ;
-   métadonnées complémentaires ;
-   comportements de liste/détail complets.

ÉTAPE B5 --- Recherche / filtres / tri selon scope approuvé

Ne pas introduire les contrôles avancés tant que leur utilité ou leur
inclusion MVP n'est pas confirmée.

ÉTAPE B6 --- Paramètres et préférences

-   apparence ;
-   unités lorsqu'approuvées ;
-   application de navigation préférée ;
-   confidentialité ;
-   à propos ;
-   autres réglages explicitement actifs.

ÉTAPE B7 --- Qualité release

-   gestion complète des erreurs restantes ;
-   tests ciblés + suites de régression ;
-   Dark theme complet ;
-   accessibilité ;
-   animations/polish ;
-   performances et batterie ;
-   validation release Android.

L'activation de la monétisation Free / Plus / Premium, Google Play
Billing, les paywalls, la restauration des achats, le cloud, les cartes
offline avancées, KMP et iOS font l'objet de phases dédiées après
décision explicite.

Les règles commerciales de ces futures phases doivent être lues depuis
`docs/reference/WHERIS_BUSINESS_REFERENCE.md` plutôt que réinventées
dans le code ou le design.

================================================== 58. PREMIÈRE MISSION
DU PROJET ==================================================

Lorsque je te demande de commencer le développement :

NE CODE PAS immédiatement toutes les fonctionnalités.

Commence uniquement par l'ÉTAPE 1.

Je veux d'abord :

1.  l'architecture exacte ;

2.  le graphe des dépendances ;

3.  l'arborescence ;

4.  les packages ;

5.  les dépendances module par module ;

6.  le Version Catalog ;

7.  settings.gradle.kts ;

8.  build.gradle.kts racine ;

9.  build.gradle.kts des modules ;

10. manifests minimaux ;

11. configuration Compose ;

12. configuration Koin minimale ;

13. configuration Room minimale ;

14. configuration Mapbox minimale ;

15. gestion propre des tokens ;

16. .gitignore ;

17. README setup ;

18. application vide compilable ;

19. commandes Gradle de validation ;

20. risques techniques ;

21. risques de coûts ;

22. décisions architecturales importantes.

À cette étape, ne développe pas encore :

-   GPS fonctionnel ;
-   carte fonctionnelle ;
-   lieux ;
-   catégories fonctionnelles ;
-   photos ;
-   marqueurs ;
-   écrans métier ;
-   repositories métier complets ;
-   use cases métier complets.

L'objectif de l'ÉTAPE 1 est :

UN SOCLE MODULAIRE VIDE, PROPRE ET COMPILABLE.

Puis attendre ma validation.

================================================== 59. RÈGLE DE DÉCISION
==================================================

Pour toute décision concernant Wheris, prioriser dans cet ordre :

1.  simplicité utilisateur ;
2.  fiabilité et sécurité des données ;
3.  rapidité d'utilisation ;
4.  vie privée ;
5.  fonctionnement local ;
6.  architecture propre ;
7.  testabilité ;
8.  maintenabilité ;
9.  maîtrise des coûts ;
10. portabilité future ;
11. sophistication technique. Si une architecture très élégante rend
    Wheris inutilement complexe, choisir la solution plus simple.

Si une fonctionnalité n'améliore pas réellement la capacité à :

ENREGISTRER

ORGANISER

RETROUVER

un lieu, remettre en question sa présence.

================================================== 60. NORTH STAR
==================================================

La North Star UX de Wheris est :

« Je veux me souvenir de cet endroit. »

Quelques secondes plus tard :

« C'est enregistré. »

Puis, plusieurs heures, jours, semaines ou mois plus tard :

« Je veux retrouver cet endroit. »

Quelques secondes plus tard :

« Le voilà. »

Tout le produit doit servir cette expérience.

================================================== 61. RÈGLE FINALE
==================================================

Wheris doit rester une application extrêmement simple malgré la richesse
potentielle de ses usages.

Nous ne construisons pas une application cartographique généraliste.

Nous construisons une mémoire géographique personnelle.

L'utilisateur décide de ce qui compte. Wheris se souvient d'où cela se
trouve.
