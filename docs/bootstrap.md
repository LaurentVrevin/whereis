# Initialisation du Projet (Bootstrap) 🚀

Ce guide détaille comment transformer ce template en une application réelle possédant sa propre identité (nom, package, thème).

---

## ⚠️ Avertissement de Sécurité

> [!IMPORTANT]
> **Ne lance jamais le bootstrap sur le dépôt d'origine.**
> Cette opération est destructive et irréversible localement. Elle ne doit être lancée que sur un nouveau dépôt créé via "Use this template".

---

## 🛠️ Le Processus pas à pas

### 1. Dry Run (Simulation)
Avant d'appliquer les changements, lance toujours une simulation pour vérifier qu'aucun conflit n'est détecté.

```powershell
.\gradlew.bat bootstrapProject `
  -PprojectName=MonApp `
  -PappDisplayName="Mon App" `
  -PpackageName=com.exemple.monapp `
  -PdryRun=true
```

### 2. Mode Interactif
Une fois la simulation validée, lance la commande simple :

```powershell
.\gradlew.bat bootstrapProject
```

Le script te posera 3 questions :
1.  **Project Name** : Nom technique PascalCase (ex: `Wheris`). Utilisé pour le dossier root et le thème.
2.  **Display Name** : Nom visible sous l'icône de l'app (ex: `Wheris App`). Peut contenir des espaces.
3.  **Package Name** : Identifiant unique (ex: `com.laurentvrevin.wheris`).

### 3. Confirmation
Un résumé s'affiche. Tape **`y`** pour lancer la transformation.

---

## 🔄 Ce qui est transformé

La tâche automatise les modifications les plus pénibles du renommage manuel :
-   **Remplacement textuel** : Mise à jour de `applicationId`, `namespace`, déclarations `package` et `import` dans tous les fichiers texte.
-   **Déplacement physique** : Les dossiers sources sont déplacés pour correspondre au nouveau package (ex: `com/laurentvrevin/androidstarter` devient `com/monapp/package`).
-   **Schémas Room** : Les répertoires de schémas exportés par Room sont également renommés pour inclure le nouveau package.
-   **Ressources** : Mise à jour de `app_name` (avec échappement XML automatique) et des styles de thèmes XML.
-   **Nettoyage** : Suppression automatique du fichier `starter.properties`.

---

## 🛡️ Sécurités intégrées

1.  **Protection contre la double exécution** : Si un fichier `.bootstrap-complete` existe, la tâche refuse de se lancer.
2.  **Validation du Package** : Le script refuse les noms de package invalides ou trop courts.
3.  **Détection de conflits** : Si un dossier de destination existe déjà et n'est pas vide, la tâche s'arrête immédiatement.

---

## 🆘 En cas d'erreur

Si le processus échoue ou si tu as fait une faute de frappe :
1.  **Ne tente pas** de réparer manuellement les packages renommés à moitié.
2.  **Supprime** ton dossier local.
3.  **Reclonne** ton dépôt GitHub (qui est resté dans l'état initial du template).
4.  **Relance** la tâche.

---
[Retour au README](../README.md) | [Suivant : Dépannage](troubleshooting.md)
