# WHERIS — BUSINESS REFERENCE

**Document canonique de référence business**  
**Version :** 0.3  
**Statut :** Référence business initiale — décisions actées + hypothèses à valider  
**Date :** 18 septembre 2026  
**Périmètre :** modèle économique, monétisation, entitlements, règles commerciales, unit economics, prévisionnel 36 mois et KPIs business  
**Document financier associé :** `docs/reference/WHERIS_PREVISIONNEL_FINANCIER_36M_V0_3.xlsx`

---

## 1. Objet du document

Ce document définit le cadre business de Wheris.

Il précise :

- comment Wheris crée de la valeur ;
- ce qui doit rester gratuit ;
- ce qui peut être monétisé ;
- la structure Free / Plus / Premium ;
- les règles de protection des utilisateurs et de leurs données lors de la monétisation ;
- les hypothèses de prix et de conversion utilisées pour le prévisionnel ;
- les règles de downgrade, d’expiration et de restauration des achats ;
- les principaux indicateurs économiques ;
- les hypothèses et décisions restant à valider ;
- le prévisionnel financier de référence sur 36 mois.

Ce document ne remplace pas :

- `docs/product/WHERIS_MASTER.md` pour la vision produit et le périmètre fonctionnel ;
- `docs/product/SECURITY_PRIVACY.md` pour les règles de sécurité et de confidentialité ;
- les documents Design pour les contrats UX/UI ;
- `RULES` et `AGENT.md` pour les règles d’implémentation.

En cas de conflit, une décision explicite doit être prise et les documents concernés doivent ensuite être réalignés. Aucun document ne doit être modifié silencieusement pour « faire rentrer » une décision business.

---

## 2. Niveaux de décision

Le présent document distingue trois niveaux.

### 2.1 RÈGLE BUSINESS

Une règle business est une décision canonique. Elle doit être respectée jusqu’à modification explicite du présent référentiel.

### 2.2 HYPOTHÈSE BUSINESS

Une hypothèse business est suffisamment définie pour être modélisée, prototypée ou testée, mais elle n’est pas considérée comme définitivement validée par le marché.

Elle peut concerner notamment :

- un prix ;
- une limite gratuite ;
- un taux de conversion ;
- un délai de conversion ;
- un taux de renouvellement ;
- un coût d’infrastructure ;
- un calendrier de lancement.

### 2.3 CANDIDAT

Un candidat est une option future envisagée mais non approuvée comme entitlement ou fonctionnalité commerciale.

Un candidat ne doit pas être présenté aux utilisateurs comme une promesse produit.

---

## 3. Thèse économique

Wheris est une application de mémoire géographique personnelle.

Sa valeur fondamentale est créée lorsque l’utilisateur peut :

> enregistrer un endroit important,  
> le conserver de manière fiable,  
> puis le retrouver lorsqu’il en a besoin.

Le modèle économique doit monétiser cette valeur sans dégrader la simplicité du produit, la confidentialité des données géographiques, la fiabilité du stockage local ou l’accès de l’utilisateur à ses propres lieux.

La stratégie business de référence repose sur deux moteurs de revenu complémentaires :

1. **Wheris Plus**, achat unique permettant de monétiser relativement tôt l’usage local intensif ;
2. **Wheris Premium**, abonnement destiné aux services à valeur et coûts récurrents, notamment les futurs services cloud.

Le modèle ne doit donc pas dépendre exclusivement d’un besoin de cloud susceptible d’apparaître plusieurs mois après l’installation.

---

## 4. Principes business non négociables

### 4.1 Pas de publicité comme moteur du modèle

Le modèle actuel de Wheris n’est pas publicitaire.

Aucune monétisation ne doit dépendre :

- de publicité ciblée ;
- de publicité basée sur la localisation ;
- de la vente de données personnelles ;
- de la vente ou location de coordonnées ;
- de la création d’un profil publicitaire à partir des lieux enregistrés.

Toute évolution vers un modèle publicitaire exigerait une décision produit et privacy explicite et une révision de ce référentiel.

#### 4.1.1 Piste future — extension Free par publicité récompensée

Une piste de monétisation **future, non actée et hors MVP** consiste à permettre à un utilisateur Free ayant atteint sa limite de capacité de débloquer ponctuellement l'enregistrement d'un nouveau lieu en choisissant volontairement de visionner une publicité récompensée (`rewarded ad`).

Cette piste n'est pas un troisième moteur économique de référence à ce stade. Elle doit être étudiée plus tard comme mécanisme éventuel de monétisation des utilisateurs qui n'achèteraient ni Plus ni Premium.

Hypothèse conceptuelle à explorer :

- l'utilisateur atteint la limite Free active ;
- Wheris continue de préserver l'accès, la modification, la suppression et la navigation vers tous les lieux déjà enregistrés ;
- pour un nouvel enregistrement, l'utilisateur peut se voir proposer soit l'offre commerciale pertinente, soit une option volontaire de publicité récompensée ;
- après complétion valide de la publicité, le nouvel enregistrement autorisé reste un lieu normal et ne doit jamais devenir inaccessible parce que l'utilisateur ne regarde plus de publicité ;
- aucune publicité ne doit être injectée de manière surprise dans les parcours fondamentaux `enregistrer → retrouver` ;
- aucune donnée géographique sensible, coordonnée exacte, nom de lieu, note, photo, catégorie personnalisée ou historique de déplacement ne doit être utilisée pour le ciblage publicitaire ;
- l'absence de réseau ou l'indisponibilité du fournisseur publicitaire ne doit jamais provoquer une perte de brouillon ou de données déjà enregistrées.

Avant toute activation, cette piste devra faire l'objet d'une étude dédiée couvrant au minimum :

- revenu publicitaire réel par impression/utilisateur ;
- taux de remplissage et disponibilité par pays ;
- impact sur la conversion Plus et risque de cannibalisation ;
- nombre maximal éventuel de déblocages récompensés par période ;
- comportement hors connexion et stratégie de reprise ;
- UX du choix `payer / regarder une publicité / revenir plus tard` ;
- consentement, SDK publicitaire, identifiants, privacy et obligations réglementaires ;
- coût technique, dépendance fournisseur et complexité de release ;
- perception de marque et compatibilité avec la promesse de simplicité et de confiance.

Cette piste ne change pas le principe actuel : **Wheris ne repose pas sur la publicité comme moteur principal du modèle économique.**

### 4.2 Les données de l’utilisateur ne sont jamais prises en otage

Une limite Free, une résiliation Premium ou l’expiration d’un abonnement ne doit jamais provoquer la suppression automatique d’un lieu enregistré.

L’utilisateur doit conserver l’accès aux lieux déjà présents sur son appareil.

Le modèle peut limiter la création de nouveaux lieux au-delà d’un quota gratuit, mais il ne doit pas bloquer l’accès aux données existantes pour forcer un paiement.

### 4.3 Le paiement ne doit pas casser la promesse local-first

La fonctionnalité locale essentielle de Wheris ne doit pas dépendre en permanence :

- d’un serveur Wheris ;
- d’un compte Wheris ;
- d’une connexion Internet ;
- d’une validation distante à chaque ouverture.

Les entitlements payants doivent pouvoir être mis en cache et restaurés de manière compatible avec cette philosophie, sous réserve des contraintes de la plateforme de paiement.

### 4.4 Le cloud doit être explicitement opt-in

Un futur service Premium de sauvegarde ou synchronisation ne doit jamais transformer silencieusement des données locales en données cloud.

L’activation doit être explicite, compréhensible et cohérente avec les exigences de `docs/product/SECURITY_PRIVACY.md`.

### 4.5 La monétisation doit intervenir après démonstration de valeur

Wheris ne doit pas imposer un paywall avant que l’utilisateur ait pu comprendre le bénéfice fondamental du produit.

Le premier objectif est :

> « Je veux me souvenir de cet endroit. »  
> « C’est enregistré. »

La monétisation doit s’insérer après cette compréhension, et non l’empêcher.

---

## 5. Architecture commerciale de référence

La structure commerciale de Wheris est :

**Wheris Free → Wheris Plus → Wheris Premium**

Plus et Premium répondent à deux besoins économiques différents.

- **Plus** monétise l’usage local avancé et la capacité.
- **Premium** monétise les services récurrents et l’extension de la mémoire Wheris au-delà d’un seul appareil.

### 5.1 Infrastructure de monétisation cible : RevenueCat

**RÈGLE BUSINESS / ARCHITECTURE CIBLE :** lorsque la phase de monétisation sera explicitement activée, Wheris utilisera **RevenueCat** comme infrastructure cible de gestion des achats, abonnements et entitlements.

RevenueCat ne remplace pas la plateforme de vente :

- sur Android, Google Play reste le store et le système de transaction ;
- RevenueCat synchronise et interprète les achats afin d’exposer un état d’entitlement fiable à Wheris ;
- Wheris ne doit pas faire dépendre son domaine métier de SKU Google Play ou d’objets SDK RevenueCat ;
- l’application consomme un état commercial neutre via une abstraction Wheris, par exemple `EntitlementRepository`.

La chaîne conceptuelle cible est :

> Google Play Product → RevenueCat Product → Offering / Package → Entitlement → Wheris EntitlementRepository → capacité produit

Les identifiants commerciaux exacts restent des détails d’implémentation/configuration. La sémantique canonique reste :

- entitlement **Plus** : droits locaux Plus acquis ;
- entitlement **Premium** : services Premium actifs ;
- un produit Premium peut accorder simultanément les droits Premium et les droits locaux correspondant à Plus pendant la durée de l’abonnement.

Pour Android, **Wheris Plus doit être configuré comme achat unique non-consommable** dans RevenueCat. Un produit consommable ne doit pas être utilisé pour simuler le déblocage Plus permanent.

RevenueCat peut fonctionner sans compte Wheris en générant un App User ID anonyme. Cette option est compatible avec l’approche local-first initiale, mais une désinstallation peut générer un nouvel identifiant anonyme au prochain lancement. Par conséquent, dès que la monétisation est active, Wheris doit fournir une action explicite **« Restaurer mes achats »** et tester la restauration sur le même compte Google Play.

Tant que Wheris repose sur des App User IDs anonymes, la configuration RevenueCat de transfert/restauration doit permettre à un achat légitime d’être rattaché au nouvel identifiant anonyme après réinstallation. Le comportement exact doit être revalidé au moment de l’intégration ; la configuration recommandée par RevenueCat pour une application sans login est actuellement le transfert vers le nouvel App User ID.

Un futur compte Wheris et un App User ID personnalisé ne doivent être introduits que si les besoins cloud/multi-appareils le justifient et après revue de `docs/product/SECURITY_PRIVACY.md`.

RevenueCat ne doit recevoir aucun contenu géographique Wheris comme attribut client : pas de coordonnées, noms de lieux, notes, photos, catégories personnalisées ni historique de déplacement.

La configuration Google Play / RevenueCat devra inclure les mécanismes recommandés pour la remontée fiable des changements d’achat, notamment les notifications serveur Google lorsque pertinentes, en particulier pour les remboursements et annulations d’achats uniques.

Références officielles à revalider au moment de l’implémentation :

- `https://www.revenuecat.com/docs/getting-started/entitlements`
- `https://www.revenuecat.com/docs/getting-started/entitlements/android-products`
- `https://www.revenuecat.com/docs/getting-started/restoring-purchases`
- `https://www.revenuecat.com/docs/customers/identifying-customers`
- `https://www.revenuecat.com/docs/platform-resources/server-notifications/google-server-notifications`
- `https://www.revenuecat.com/pricing`

---

## 6. Wheris Free

### 6.1 Rôle

Wheris Free est un vrai produit utilisable.

Son objectif est de permettre à un nouvel utilisateur de vivre plusieurs fois la boucle de valeur de Wheris avant de devoir décider s’il souhaite payer.

Free ne doit pas être une simple démo.

### 6.2 Limite de référence

**HYPOTHÈSE BUSINESS V0.2 : 10 lieux enregistrés maximum.**

Cette limite remplace l’ancienne hypothèse exploratoire de 3 lieux comme hypothèse de travail business.

Elle reste à valider par usage réel.

La question à tester n’est pas uniquement « quelle limite maximise les conversions ? », mais :

> quelle limite permet de créer une habitude réelle sans satisfaire durablement l’intégralité du besoin pour la majorité des utilisateurs ?

### 6.3 Comptabilisation

Dans la V0.2 :

- un lieu réellement enregistré dans la bibliothèque locale compte dans la limite ;
- la suppression définitive d’un lieu libère une place ;
- aucune notion d’archive gratuite ne doit être inventée uniquement pour contourner la limite ;
- les futurs historiques, archives ou restaurations devront définir leurs propres règles de comptabilisation avant implémentation.

### 6.4 Droits à la limite

Lorsque l’utilisateur atteint la limite Free, il doit toujours pouvoir :

- consulter ses lieux existants ;
- ouvrir leur fiche ;
- les modifier ;
- les supprimer ;
- utiliser les actions de récupération/navigation déjà disponibles ;
- utiliser les données locales déjà enregistrées.

La restriction commerciale porte prioritairement sur **la création de nouveaux lieux**.

### 6.5 Dépassement de limite

Si un utilisateur se retrouve au-dessus de la limite Free à la suite d’une expiration Premium ou d’une évolution future du quota :

- aucun lieu n’est supprimé ;
- aucun lieu n’est masqué ;
- la consultation continue ;
- la modification continue ;
- la suppression continue ;
- la création de nouveaux lieux peut être bloquée jusqu’à retour sous la limite ou acquisition d’un entitlement approprié.

---

## 7. Wheris Plus

### 7.1 Positionnement

Wheris Plus est le principal moteur de monétisation précoce.

Il doit permettre à un utilisateur ayant compris Wheris de payer une seule fois pour lever la contrainte de capacité locale.

### 7.2 Prix de référence

**HYPOTHÈSE BUSINESS V0.2 : 19,99 € TTC en achat unique.**

Le prix est un paramètre à tester.

La formulation commerciale recommandée est **« achat unique »**, plutôt qu’une promesse juridiquement absolue de type « à vie ».

### 7.3 Entitlement canonique V0.2

L’entitlement canonique de Plus est :

> **lieux locaux illimités.**

À ce stade, aucun autre entitlement local existant dans le MVP ne doit être retiré automatiquement de Free uniquement pour enrichir artificiellement Plus.

### 7.4 Candidats futurs pour Plus

Des fonctionnalités locales avancées peuvent ultérieurement être étudiées comme bénéfices Plus, par exemple :

- recherche ou filtres avancés ;
- outils d’organisation avancés ;
- capacités photo enrichies ;
- import/export enrichi ;
- fonctions power-user locales.

Ces éléments sont **CANDIDATS** et non des entitlements approuvés par la présente version.

Leur inclusion ne doit pas contredire une fonctionnalité déjà promise comme fondamentale dans le produit sans décision explicite.

### 7.5 Caractère permanent de l’achat

Une fois l’achat Plus validé, Wheris doit considérer l’utilisateur comme propriétaire de cet entitlement non récurrent conformément aux capacités de restauration de la plateforme de paiement.

Une évolution de tarif ne doit pas révoquer un achat Plus antérieur.

---

## 8. Wheris Premium

### 8.1 Positionnement

Premium est le moteur de revenu récurrent.

Il doit principalement monétiser des capacités ayant elles-mêmes :

- une valeur récurrente ;
- un coût récurrent ;
- ou une complexité d’exploitation récurrente.

### 8.2 Prix annuel de référence

**HYPOTHÈSE BUSINESS V0.2 : 29,99 € TTC / an pour un utilisateur n’ayant pas Plus.**

Premium inclut les droits Plus pendant toute la durée de l’abonnement.

### 8.3 Upgrade d’un propriétaire Plus

**HYPOTHÈSE BUSINESS V0.2 : 19,99 € TTC / an pour un propriétaire Plus.**

La logique est que l’utilisateur a déjà payé le déblocage local permanent et ne paie ensuite que la couche de services récurrents.

Cette structure doit être revalidée au moment de la mise en place réelle de la facturation.

### 8.4 Option mensuelle

**CANDIDAT : 3,99 € TTC / mois.**

Le prévisionnel financier V0.3 ne modélise pas cette option.

Elle ne doit donc pas être considérée comme un prix canonique tant qu’elle n’a pas été ajoutée au modèle financier et validée commercialement.

### 8.5 Entitlements Premium envisagés

Les capacités suivantes constituent la direction produit Premium, sous réserve de conception et de validation séparées :

- sauvegarde cloud ;
- restauration cloud ;
- synchronisation multi-appareils ;
- stockage/synchronisation de photos ;
- capacités offline avancées ;
- futures fonctions nécessitant un backend ou une infrastructure récurrente.

D’autres bénéfices peuvent être ajoutés ultérieurement, mais Premium ne doit pas devenir un regroupement arbitraire de fonctionnalités retirées artificiellement au produit de base.

---

## 9. Interaction Plus / Premium

### 9.1 Utilisateur Free → Premium

Pendant l’abonnement Premium :

- l’utilisateur bénéficie des capacités Premium ;
- la limite Free ne s’applique pas ;
- les droits locaux correspondant à Plus sont disponibles pendant la durée de l’abonnement.

À expiration sans renouvellement :

- l’utilisateur revient au niveau Free s’il n’a jamais acquis Plus ;
- aucun lieu local n’est supprimé ;
- s’il possède plus que la limite Free, il conserve l’accès mais ne peut plus ajouter de nouveaux lieux jusqu’à régularisation.

### 9.2 Utilisateur Plus → Premium

Pendant Premium :

- Plus reste acquis ;
- Premium ajoute les services récurrents.

À expiration de Premium :

- l’utilisateur revient à Plus ;
- ses lieux locaux restent illimités ;
- les services Premium cessent ou passent dans leur état de fin d’abonnement défini.

### 9.3 Données cloud après expiration

La durée de conservation, la période de grâce, le téléchargement des données et la suppression éventuelle des données cloud après expiration sont **UNE DÉCISION OUVERTE**.

Aucune politique de suppression cloud ne doit être inventée dans l’implémentation avant définition explicite et revue sécurité/privacy.

---

## 10. Règles de paywall et d’upsell

### 10.1 Pas de paywall au premier usage

La première utilisation doit servir à démontrer la valeur de Wheris.

Le produit ne doit pas exiger un paiement avant que l’utilisateur puisse effectuer la boucle fondamentale d’enregistrement et de récupération.

### 10.2 Upsell non bloquant

Wheris peut présenter une information Plus/Premium après que l’utilisateur a démontré un usage réel.

**HYPOTHÈSE D’EXPÉRIMENTATION : premier upsell doux entre le 3e et le 5e lieu enregistré.**

L’upsell :

- doit être fermable ;
- ne doit pas interrompre une sauvegarde déjà engagée ;
- ne doit pas utiliser de dark pattern ;
- ne doit pas prétendre qu’une fonction est indisponible lorsqu’elle ne l’est pas.

### 10.3 Paywall de limite

Avec une limite Free de 10 lieux, le paywall bloquant apparaît au moment où l’utilisateur tente d’enregistrer un **11e lieu**.

Le paywall ne doit pas bloquer la consultation des 10 lieux existants.

Si l’utilisateur avait déjà commencé un flux d’ajout, son brouillon et les informations utiles déjà acquises ne doivent pas être perdus simplement parce que le paywall est apparu.

### 10.4 Ton commercial

La communication doit expliquer la valeur supplémentaire plutôt que créer artificiellement de l’urgence ou de la peur.

Wheris ne doit pas utiliser les lieux personnels de l’utilisateur comme levier émotionnel de pression.

---

## 11. Fin d’abonnement, restauration et changements de prix

### 11.1 Fin Premium

Une fin d’abonnement ne doit jamais supprimer automatiquement des données locales.

### 11.2 Restauration d’achat

Les achats et abonnements doivent être restaurables selon les mécanismes de la plateforme de distribution utilisée.

Avec RevenueCat, Wheris doit exposer une action explicite **« Restaurer mes achats »** utilisant le mécanisme de restauration RevenueCat afin de resynchroniser les transactions du compte Google Play et de recalculer les entitlements.

Wheris Plus étant un achat unique, il doit être configuré comme **non-consommable**. Cette configuration est une condition de robustesse de la restauration Android et doit être vérifiée avant release.

Wheris ne doit pas imposer un compte propriétaire uniquement pour restaurer un achat local si Google Play + RevenueCat permettent de gérer correctement l’entitlement sans compte Wheris.

L’identité anonyme RevenueCat ne doit pas être présentée comme un compte Wheris ni comme une identité utilisateur durable garantie à travers une désinstallation. Toute évolution vers des App User IDs personnalisés exige une décision dédiée sur les comptes et la synchronisation.

La politique Android Backup ne doit pas être modifiée uniquement pour préserver un identifiant RevenueCat sans revue explicite de `docs/product/SECURITY_PRIVACY.md`.

### 11.3 Évolution de prix

Les prix peuvent évoluer pour de nouveaux achats.

Une augmentation future du prix Plus ne doit pas annuler les droits d’un propriétaire Plus existant.

Les règles de renouvellement Premium suivent les règles de la plateforme, les obligations légales applicables et les conditions affichées à l’utilisateur.

### 11.4 Évolution de la limite Free

Si la limite Free est réduite dans le futur, aucun lieu existant ne doit être supprimé.

Si la nouvelle limite est inférieure au nombre déjà enregistré, le modèle de dépassement décrit plus haut s’applique.

---

## 12. Phasage produit et monétisation

Le business model est défini dès maintenant, mais il ne doit pas forcer une intégration prématurée de billing dans le MVP technique.

Le MVP produit actuel reste local-first et peut être développé sans paywall ni facturation active.

L’activation de la monétisation constitue une étape dédiée comprenant au minimum :

- configuration Google Play des produits réels ;
- configuration RevenueCat des Products, Entitlements, Offerings et Packages ;
- validation du mapping Google Play ↔ RevenueCat ↔ entitlements Wheris ;
- configuration de Plus comme achat unique non-consommable ;
- UX paywall ;
- règles de restauration et test « Restaurer mes achats » ;
- tests de downgrade ;
- conformité store et notifications serveur Google/RevenueCat lorsque pertinentes ;
- conformité légale/fiscale ;
- analytics business respectueux de la confidentialité ;
- validation de la conservation des données ;
- revue sécurité si Premium cloud est activé.

Dans le prévisionnel financier, **M1 signifie le premier mois de version publique monétisée**, et non nécessairement le premier mois de développement ni la première build MVP interne.

---

## 13. Modèle financier de référence

Le fichier associé est :

`docs/reference/WHERIS_PREVISIONNEL_FINANCIER_36M_V0_3.xlsx`

Le modèle est un outil de décision et non une prévision statistique certaine.

Toutes les conversions, croissances et rétentions doivent être remplacées progressivement par des données réelles.

La V0.3 ajoute trois couches de discipline sans inventer de nouvelles performances produit :

1. une feuille **Validation produit** pour saisir activation, temps de save, `1 → 3`, `3 → 10`, D30/D90 et signaux de récupération ;
2. une feuille **Acquisition** qui sépare la trajectoire organique/earned de l'acquisition payante optionnelle (`budget → CPI → installations payées`) ;
3. une feuille **Cohortes MAU** qui permet de remplacer progressivement le proxy historique `MAU = 20 % des installations cumulées` par des taux actifs observés selon l'âge de cohorte.

Tant qu'aucune donnée réelle n'est saisie, le modèle conserve explicitement les hypothèses V0.2 comme fallback afin de ne pas fabriquer artificiellement une amélioration du prévisionnel.

La conversion commerciale principale reste encore modélisée directement à partir des installations. Elle devra être remplacée lorsque suffisamment de données existent par un funnel observé du type `installation → premier lieu → 3e lieu → 10e lieu / exposition limite → achat`.

### 13.1 Hypothèses commerciales globales

| Paramètre | Hypothèse active V0.3 | Statut |
|---|---:|---|
| Limite Free | 10 lieux | À tester |
| Plus | 19,99 € TTC achat unique | À tester |
| Premium annuel direct | 29,99 € TTC/an | À tester |
| Premium annuel pour propriétaire Plus | 19,99 € TTC/an | À tester |
| Premium mensuel | 3,99 € TTC/mois | Candidat, non modélisé |
| Lancement Premium | M10 | Hypothèse |
| Délai moyen achat Plus | 2 mois | Hypothèse |
| Délai moyen Premium direct | 3 mois | Hypothèse |
| TVA utilisée dans le modèle | 20 % | Hypothèse de planification |
| Commission store utilisée | 15 % du CA HT | Hypothèse simplifiée à revalider |
| RevenueCat | Gratuit jusqu’à 2 500 USD de MTR/mois, puis 1 % du MTR suivi | Coût externe actuel à revalider ; le MTR inclut abonnements, renouvellements et achats non-subscription |

### 13.2 Unit economics de planification

Avec les hypothèses fiscales/store du modèle :

- vente Plus 19,99 € TTC → environ **14,16 €** de revenu après TVA modélisée et commission store ;
- Premium direct 29,99 € TTC/an → environ **21,24 €** ;
- Premium upgrade 19,99 € TTC/an → environ **14,16 €**.

RevenueCat est modélisé comme un **coût variable séparé** afin de ne pas confondre revenu après store et coût d’infrastructure commerciale. Au tarif public vérifié le 18 septembre 2026, RevenueCat est gratuit jusqu’à **2 500 USD de Monthly Tracked Revenue par mois**, puis facture **1 % du MTR suivi**, le MTR étant calculé avant commission store et taxes. Le prévisionnel V0.3 utilise le CA brut TTC mensuel comme proxy prudent de ce MTR et applique le taux de change de planification du modèle.

Lorsque le seuil RevenueCat est dépassé, l’ordre de grandeur du coût par vente est donc approximativement 1 % du prix brut suivi, par exemple environ 0,20 € sur un achat Plus à 19,99 € ou environ 0,30 € sur un Premium annuel à 29,99 €, avant effet de change et définition exacte du MTR réel.

Ces montants ne constituent pas du bénéfice net.

Ils sont calculés avant :

- RevenueCat lorsque le seuil gratuit est dépassé ;
- infrastructure ;
- marketing ;
- outils ;
- comptabilité/juridique ;
- coût fondateur ou salaires ;
- impôt sur les sociétés ;
- coûts de financement.

---

## 14. Scénarios 36 mois

### 14.1 Hypothèses de traction

Les trajectoires d'installations des trois scénarios représentent désormais le **socle organique / earned de planification**. Une dépense payante n'ajoute des installations que si un budget et un CPI sont explicitement renseignés dans la feuille Acquisition du modèle financier. Par défaut, ce budget est nul : les résultats V0.3 ne supposent donc pas silencieusement qu'une campagne payante rentable existe.


| Hypothèse | Prudent | Base | Ambitieux |
|---|---:|---:|---:|
| Installations M1 | 1 000 | 2 000 | 3 000 |
| Croissance mensuelle A1 | 5 % | 8 % | 10 % |
| Croissance mensuelle A2 | 4 % | 6 % | 8 % |
| Croissance mensuelle A3 | 2 % | 4 % | 6 % |
| Conversion install → Plus | 1,5 % | 2,5 % | 4,0 % |
| Conversion install → Premium direct | 0,2 % | 0,5 % | 1,0 % |
| Upgrade Plus → Premium / an | 1 % | 2 % | 4 % |
| Renouvellement Premium annuel | 50 % | 60 % | 70 % |

Aucun de ces taux n’est présenté comme un résultat observé.

Les conversions `installations → Plus` et `installations → Premium direct` restent des **proxies de planification V0.3**. Elles ne signifient pas que le funnel réel est supposé direct. Dès que les données existent, l'évolution attendue du modèle est de relier explicitement :

`installation → premier lieu → usage répété → limite Free atteinte → exposition de l'offre → achat`

avec des taux observés par étape et, lorsque pertinent, par canal/cohorte. Cette évolution doit remplacer progressivement les proxies directs sans réécrire rétroactivement les données historiques.

---

## 15. Prévisionnel 36 mois — scénario Base

Le scénario Base constitue la référence de planification V0.3.

### 15.1 Résultats cumulés

| KPI | 36 mois |
|---|---:|
| Installations | **267 977** |
| Achats Plus | **5 963** |
| Nouveaux Premium / upgrades | **1 162** |
| Revenu net après TVA modélisée et commission store | **114 097 €** |
| Contribution produit avant coût fondateur | **70 734 €** |
| Coût fondateur modélisé | **60 000 €** |
| Résultat opérationnel avant IS | **+10 734 €** |
| Besoin de financement maximal modélisé | **1 793 €** |
| MAU estimés au M36 | **53 595** |
| Revenu net mensuel au M36 | **7 150 €** |
| Résultat mensuel au M36 | **2 040 €** |
| Revenu net cumulé / installation sur l’horizon | **0,43 €** |
| Coût RevenueCat cumulé | **1 476 €** |

### 15.2 Lecture annuelle

| KPI | Année 1 | Année 2 | Année 3 |
|---|---:|---:|---:|
| Installations | 37 954 | 83 389 | 146 633 |
| Revenu net | 11 388 € | 34 749 € | 67 959 € |
| Contribution avant fondateur | 2 277 € | 21 863 € | 46 594 € |
| Coût fondateur | 0 € | 24 000 € | 36 000 € |
| Résultat opérationnel | +2 277 € | -2 137 € | +10 594 € |

Le coût fondateur est une enveloppe de coût société utilisée pour tester la capacité du projet à supporter progressivement une rémunération. Il ne représente pas un salaire net garanti.

---

## 16. Comparaison des scénarios

| Scénario | Installations 36m | Revenu net 36m | Résultat opérationnel 36m |
|---|---:|---:|---:|
| Prudent | ~80 105 | ~19 066 € | **-48 950 €** |
| Base | ~267 977 | ~114 097 € | **+10 734 €** |
| Ambitieux | ~625 009 | ~445 177 € | **+246 146 €** |

Le modèle montre que la faible infrastructure nécessaire à Wheris ne suffit pas à rendre automatiquement l’entreprise rentable.

La rentabilité dépend principalement de :

- la capacité à acquérir des installations à coût raisonnable ;
- la proportion d’utilisateurs qui adoptent réellement Wheris ;
- la conversion Plus ;
- la conversion Premium ;
- la rétention Premium ;
- la capacité à ne pas surinvestir trop tôt en coûts fixes ou acquisition payante.

---

## 17. Coûts de planification

Le modèle V0.3 prévoit notamment :

- RevenueCat selon le Monthly Tracked Revenue ;
- infrastructure cloud fixe à partir du lancement Premium ;
- provision variable par utilisateur Premium ;
- coût Mapbox dépendant des MAU ;
- marketing variable et budget minimum ;
- logiciels/outils ;
- comptabilité/juridique ;
- autres frais fixes ;
- coût fondateur progressif.

Les tarifs réels des fournisseurs externes ne sont jamais considérés comme constants.

Ils doivent être revalidés avant :

- lancement commercial ;
- changement d’échelle significatif ;
- activation d’une fonction cloud ;
- ajout d’un nouveau fournisseur ;
- changement de tarification ou de plan RevenueCat ;
- signature d’un engagement de coût récurrent.

---

## 18. Politique d’acquisition

Le modèle de référence produit environ **0,43 € de revenu net cumulé par installation sur les 36 mois modélisés** dans le scénario Base avec les hypothèses actuelles.

Ce chiffre n’est pas une LTV définitive.

Il indique néanmoins que Wheris ne doit pas supposer qu’une stratégie d’acquisition payante agressive sera rentable au lancement.

Avant de scaler une acquisition payante, Wheris doit connaître au minimum :

- le coût d’acquisition réel ;
- la conversion en premier lieu ;
- la rétention ;
- la conversion Plus ;
- la conversion Premium ;
- le revenu net par cohorte ;
- la contribution après coûts variables ;
- le CPI/CAC par canal ;
- la part réellement organique / earned ;
- la contribution nette par cohorte après coût d'acquisition.

Un canal payant ne doit pas être déclaré rentable en comparant son CPI au chiffre d'affaires brut. Il faut le comparer à une contribution par cohorte sur un horizon explicite, avec suffisamment de recul sur la rétention.

La croissance initiale doit privilégier les canaux capables de produire une acquisition organique ou faiblement coûteuse tant que l’économie par installation n’est pas démontrée.

---

## 19. KPIs business de référence

Les KPIs business prioritaires sont :

### Activation

- installation / utilisateur éligible → premier lieu enregistré ;
- **time-to-first-value** : premier lancement → premier lieu enregistré ;
- temps médian et p90 du happy path une fois une position exploitable disponible ;

Le time-to-first-value mesure la friction complète de découverte/onboarding/permission/first save et doit rester distinct du fast save, qui mesure uniquement le cœur d'interaction une fois la position exploitable.
- taux d'abandon avant sauvegarde ;
- taux d'échec de persistance et taux de retry réussi.

### Habitude

- premier lieu → 3e lieu ;
- 3e lieu → 10e lieu ;
- délai médian jusqu’au 3e lieu ;
- délai médian jusqu’à la limite Free ;
- rétention D30 et D90 ;
- fréquence de retour pour retrouver un lieu ;
- signal qualitatif "réflexe Wheris" : l'utilisateur pense spontanément à Wheris lorsqu'un endroit mérite d'être mémorisé.

Le "réflexe Wheris" n'est pas un pourcentage par défaut. Il peut devenir un indicateur quantifié uniquement si une méthode explicite est définie (question standardisée, échantillon, fenêtre d'observation et règle de calcul).

### Monétisation

- exposition upsell → ouverture de l’offre ;
- limite atteinte → achat Plus ;
- limite atteinte → Premium ;
- installation → Plus ;
- installation → Premium ;
- délai moyen avant achat ;
- taux de remboursement ;
- taux de restauration réussie ;
- erreurs de synchronisation d’entitlements RevenueCat.

### Premium

- nombre d’abonnés actifs ;
- upgrade Plus → Premium ;
- renouvellement annuel ;
- churn ;
- coût cloud par Premium actif.

### Économie globale

- revenu net par installation ;
- revenu net par utilisateur actif ;
- revenu net par payeur ;
- coûts variables par payeur ;
- contribution avant coûts fixes ;
- CAC lorsque l’acquisition payante est utilisée ;
- résultat opérationnel mensuel ;
- trésorerie.

---

## 20. Analytics et confidentialité

Les besoins business ne justifient pas la collecte de coordonnées exactes ou de contenu géographique sensible.

Les analytics business doivent privilégier des événements non sensibles, par exemple :

- nombre de lieux enregistrés sous forme de compteurs ;
- passage de seuils ;
- affichage d’un paywall ;
- achat ;
- renouvellement ;
- erreurs de facturation ;
- rétention produit.

Les analytics ne doivent pas transmettre :

- latitude/longitude exactes ;
- noms de lieux ;
- notes ;
- photos ;
- historique de mouvement ;
- contenu permettant de reconstruire les habitudes géographiques personnelles.

Toute solution analytics doit respecter `docs/product/SECURITY_PRIVACY.md`.

Les Customer Attributes RevenueCat ne doivent jamais être utilisés pour transporter du contenu de lieu ou des données géographiques sensibles. Les identifiants publicitaires ou attributs d'attribution optionnels ne doivent pas être collectés par défaut uniquement parce que RevenueCat le permet.

---

## 21. Règles d’expérimentation

Les éléments suivants peuvent être testés sans changer la thèse business :

- limite Free ;
- prix Plus ;
- prix Premium ;
- timing du premier upsell ;
- copy du paywall ;
- présentation annuelle ou mensuelle ;
- upgrade Plus → Premium ;
- bénéfices Premium ;
- bundle promotionnel temporaire ;
- mécanisme futur de publicité récompensée après atteinte de la limite Free, uniquement dans le cadre d'une expérimentation explicitement approuvée.

Une expérimentation ne doit jamais :

- supprimer silencieusement des données ;
- retirer rétroactivement un achat acquis ;
- transmettre des données sensibles pour optimiser la conversion ;
- introduire un dark pattern ;
- dégrader volontairement la fiabilité fondamentale de Wheris ;
- inventer une fausse urgence ;
- bloquer l’accès aux lieux déjà sauvegardés.

---

## 22. Décisions ouvertes

Les décisions suivantes restent ouvertes après la V0.3 :

1. validation réelle de la limite Free à 10 lieux ;
2. validation du prix Plus à 19,99 € ;
3. validation du Premium annuel à 29,99 € ;
4. validation du tarif d’upgrade Plus → Premium à 19,99 €/an ;
5. présence ou non d’un abonnement mensuel ;
6. liste exacte des entitlements locaux supplémentaires de Plus ;
7. liste exacte des entitlements Premium au lancement ;
8. fournisseur et architecture cloud ;
9. politique de conservation des données cloud après expiration ;
10. calendrier exact entre lancement MVP et lancement de la monétisation ;
11. politique RevenueCat d’identité/transfer behavior et éventuel passage futur d’identifiants anonymes vers un App User ID Wheris ;
12. décision d’utiliser ou non les Paywalls/Experiments RevenueCat plutôt qu’une UI Wheris pilotée par Offerings ;
13. métriques réelles permettant de remplacer les hypothèses de conversion du prévisionnel ;
14. seuils de validation activation/rétention après observation d'un volume suffisant, sans les inventer avant données ;
15. mix d'acquisition organique / earned / payante et CPI réel par canal ;
16. définition opérationnelle des cohortes MAU et fenêtre d'activité retenue ;
17. remplacement progressif des proxies `install → paid` par un funnel observé `activation → habitude → limite → offre → achat`, y compris par canal lorsque le volume le permet ;
18. étude d'une éventuelle extension Free par publicité récompensée après atteinte de la limite : règle de déblocage, fréquence, revenu réel, cannibalisation de Plus, fonctionnement offline, privacy/consentement et choix du fournisseur.

Aucune de ces décisions ne doit être résolue implicitement dans le code.

---

## 23. Critères de validation du business model

La validation est séquentielle : **preuve d'usage → preuve d'habitude → preuve de monétisation → scale acquisition**. Le modèle Free / Plus / Premium sera considéré comme validé progressivement si les données réelles montrent simultanément que :

- les utilisateurs comprennent et utilisent la boucle fondamentale de Wheris, avec un save direct réellement court ;
- une proportion significative atteint plusieurs lieux enregistrés et revient réellement pour les retrouver ;
- la limite Free intervient après démonstration de valeur et non avant ;
- Plus génère des achats sans détériorer excessivement l’activation ou la rétention ;
- Premium apporte une valeur perçue suffisante pour générer des abonnements et renouvellements ;
- le coût d’infrastructure reste cohérent avec la marge ;
- le modèle ne provoque pas de conflit majeur avec la promesse privacy/local-first.

Aucun taux cible définitif n'est imposé dans cette version avant collecte de données réelles, hormis l'objectif produit déjà établi d'un save simple proche de moins de 10 secondes lorsque la position est disponible.

Le nombre d'installations seul ne constitue pas une preuve de product-market fit, de rétention ni de capacité à monétiser.

---

## 24. Relation avec le MVP

Le présent document définit le modèle économique cible et les règles à respecter lorsqu’il sera activé.

Il ne modifie pas automatiquement le périmètre du MVP technique existant.

En particulier :

- Google Play Billing et le SDK RevenueCat ne doivent pas être ajoutés uniquement parce que ce référentiel existe ;
- les écrans Premium/paywall ne deviennent pas automatiquement des écrans MVP ;
- le cloud et les comptes ne deviennent pas des prérequis pour enregistrer un lieu ;
- les fonctionnalités locales existantes ne doivent pas être retirées silencieusement du MVP pour fabriquer artificiellement un paywall.

Une étape de monétisation dédiée devra explicitement faire évoluer les documents produit, UX, sécurité et architecture concernés.

---

## 24.1 Évolution V0.2 — RevenueCat

La V0.2 acte RevenueCat comme infrastructure cible de purchases/entitlements pour la future phase de monétisation Android, ajoute son coût au prévisionnel, impose Wheris Plus comme produit non-consommable, formalise l’action de restauration d’achats et renforce la séparation entre Google Play, RevenueCat et le domaine Wheris.

Cette décision ne change pas le périmètre du MVP actif.

---

## 24.2 Évolution V0.3 — validation produit, acquisition et cohortes

La V0.3 ne change ni les prix candidats ni l'architecture commerciale. Elle corrige la priorité de décision : Wheris doit d'abord démontrer que la boucle `enregistrer → retrouver` devient un usage répété.

Le prévisionnel associé sépare désormais la trajectoire organique de l'acquisition payante optionnelle et devient compatible avec un calcul MAU par âge de cohorte. Le fallback historique est conservé tant que les données réelles nécessaires ne sont pas disponibles.

La monétisation ne doit pas être optimisée en avance sur une absence de rétention démontrée.

---

## 25. Règle de décision business

Lorsqu’une décision business oppose revenu immédiat et intégrité du produit, l’ordre de priorité est :

1. préserver les données de l’utilisateur ;
2. préserver la capacité fondamentale à retrouver ses lieux ;
3. préserver la confiance et la confidentialité ;
4. préserver la simplicité de l’expérience ;
5. démontrer la valeur avant de demander un paiement ;
6. construire une monétisation durable ;
7. maintenir des coûts d’exploitation maîtrisés ;
8. optimiser ensuite le revenu et la conversion.

Wheris ne doit pas maximiser le revenu à court terme au prix de la destruction de la confiance qui fait la valeur du produit.

---

## 26. North Star business

La North Star produit reste :

> « Je veux me souvenir de cet endroit. »  
> « C’est enregistré. »  
> Plus tard :  
> « Le voilà. »

La North Star business associée est :

> **Créer suffisamment de valeur et de confiance pour qu’une partie des utilisateurs choisisse volontairement de payer afin d’étendre et de protéger sa mémoire géographique.**

Le business model doit servir le produit.

Le produit ne doit pas être déformé pour servir le paywall.

---

## 27. Références internes

Ce référentiel s’appuie sur les principes et contraintes déjà définis dans :

- `docs/product/WHERIS_MASTER.md` — vision, MVP, architecture commerciale Free / Plus / Premium et évolutions futures ;
- `docs/product/SECURITY_PRIVACY.md` — local-first, données géographiques sensibles, cloud explicite, protection des données ;
- `AGENT.md` — simplicité, intégrité des données, vie privée, résilience offline et maîtrise des coûts ;
- `docs/design/01_DESIGN_FOUNDATIONS.md` — Premium hors MVP actif, protection de l’usage local fondamental ;
- `docs/design/04_SCREEN_CATALOG.md` — surfaces Premium futures et état de limite Free ;
- `docs/design/05_USER_FLOWS.md` — Premium/billing hors flux MVP canonique ;
- `docs/reference/WHERIS_PREVISIONNEL_FINANCIER_36M_V0_3.xlsx` — hypothèses chiffrées et calculs 36 mois.

---

## 28. Versioning

Toute modification significative de l’un des éléments suivants doit incrémenter la version de ce document :

- structure Free / Plus / Premium ;
- prix de référence ;
- limite Free ;
- entitlement permanent ;
- règle de downgrade ;
- politique de conservation des données ;
- architecture économique Premium ;
- hypothèses centrales du scénario Base ;
- canal de monétisation principal.

Les changements purement rédactionnels peuvent être effectués sans changement majeur de version.

**Version actuelle : 0.3 — RevenueCat reste l’infrastructure cible de purchases/entitlements ; la priorité de validation est désormais explicitement usage → habitude → monétisation → acquisition, et le modèle financier V0.3 est préparé pour acquisition par canal et cohortes MAU sans inventer de données réelles.**
