# 📚 Système de Gestion Scolaire — Documentation du Projet

> **Projet académique** — Programmation Orientée Objet Avancée  
> **Classe :** 2ème LIG BI / EB — 2025/2026  
> **Enseignant :** Achraf Chaâbane  
> **Technologie :** JavaFX + JDBC + MySQL (XAMPP)

---

## 🎯 But du Projet

Ce projet est une **application de bureau** (desktop) qui permet à un établissement scolaire de :

- Gérer les fiches des **élèves**
- Organiser les **inscriptions** par année scolaire
- Répartir les élèves dans des **classes** selon leur niveau
- Gérer les **matières** et les **enseignants**
- Saisir les **notes** par trimestre
- Générer les **bulletins scolaires** avec moyenne, rang et appréciation

---

## 🗂️ Structure du Projet

```
src/application/
│
├── Main.java                    ← Point d'entrée de l'application
├── Login.java                   ← Écran de connexion (sécurisé)
├── cnx.java                     ← Connexion à la base de données MySQL
├── application.css              ← Style visuel (thème sombre moderne)
│
├── model/                       ← Les données (ce que le système stocke)
│   ├── Eleve.java
│   ├── AnneeScolaire.java
│   ├── Niveau.java
│   ├── Inscription.java
│   ├── Classe.java
│   ├── Affectation.java
│   ├── Matiere.java
│   ├── Enseignant.java
│   ├── Note.java
│   └── Bulletin.java
│
├── dao/                         ← Accès base de données (requêtes SQL)
│   ├── EleveDAO.java
│   ├── AnneeScolaireDAO.java
│   ├── NiveauDAO.java
│   ├── InscriptionDAO.java
│   ├── ClasseDAO.java
│   ├── MatiereDAO.java
│   ├── EnseignantDAO.java
│   ├── NoteDAO.java
│   └── BulletinDAO.java
│
├── util/                        ← Outils partagés
│   ├── Validator.java           ← Toutes les règles de validation
│   └── UIHelper.java           ← Helpers visuels (alertes, bordures rouges)
│
└── view/                        ← Écrans de l'application (interfaces)
    ├── Dashboard.java
    ├── EleveView.java
    ├── AnneeScolaireView.java
    ├── NiveauView.java
    ├── ClasseView.java
    ├── MatiereView.java
    ├── EnseignantView.java
    ├── InscriptionView.java
    ├── NoteView.java
    ├── BulletinView.java
    └── RankingView.java
```

---

## 📁 Explication de Chaque Fichier

### 🔧 Fichiers Principaux

#### `Main.java`
**Rôle :** Le premier fichier qui s'exécute quand on lance l'application.  
**Pourquoi :** JavaFX a besoin d'un point d'entrée unique. Ce fichier ouvre la fenêtre de connexion.

---

#### `Login.java`
**Rôle :** L'écran de connexion avec nom d'utilisateur et mot de passe.  
**Pourquoi :** La sécurité — seuls les enseignants enregistrés peuvent accéder au système.  
**Ce qu'il fait :**
- Vérifie que les champs ne sont pas vides
- Vérifie que le mot de passe fait au moins 4 caractères
- Cherche l'enseignant dans la base de données (table `enseignant`)
- Affiche un message d'erreur en rouge si les identifiants sont incorrects

---

#### `cnx.java`
**Rôle :** Établit la connexion à la base de données MySQL (XAMPP).  
**Pourquoi :** Toutes les classes qui ont besoin de la base de données utilisent ce fichier. Un seul endroit pour changer l'URL, l'utilisateur ou le mot de passe.

---

#### `application.css`
**Rôle :** Le style visuel de toute l'application.  
**Pourquoi :** JavaFX supporte CSS. Ce fichier donne un thème sombre professionnel avec des couleurs, des bordures et des animations cohérentes dans toutes les fenêtres.

---

### 📦 Dossier `model/` — Les Données

> Les classes `model` sont des **conteneurs de données**. Elles représentent exactement les tables de la base de données. Chaque champ en Java correspond à une colonne SQL.

| Fichier | Table SQL | Ce qu'il représente |
|---|---|---|
| `Eleve.java` | `eleve` | Un élève (nom, prénom, date de naissance, adresse, téléphone du parent) |
| `AnneeScolaire.java` | `annee_scolaire` | Une année scolaire (ex: 2025-2026, date début, date fin) |
| `Niveau.java` | `niveau` | Un niveau d'étude (ex: 1ère année, 2ème année) |
| `Inscription.java` | `inscription` | L'inscription d'un élève dans une année scolaire et un niveau |
| `Classe.java` | `classe` | Une classe (nom, capacité max 20, niveau associé) |
| `Affectation.java` | `affectation` | L'affectation d'un élève inscrit dans une classe |
| `Matiere.java` | `matiere` | Une matière d'étude associée à un niveau |
| `Enseignant.java` | `enseignant` | Un enseignant (nom, prénom, téléphone, username, password) |
| `Note.java` | `note` | Une note donnée à un élève pour une matière et un trimestre |
| `Bulletin.java` | `bulletin` | Le bulletin de fin de trimestre (moyenne, rang, appréciation) |

**Pourquoi tous ces fichiers séparés ?**  
C'est le principe de la **Programmation Orientée Objet** : chaque entité réelle du monde scolaire a sa propre classe Java. Cela rend le code lisible, modifiable et maintenable.

---

### 🗄️ Dossier `dao/` — Accès à la Base de Données

> **DAO = Data Access Object** — Ce sont les classes qui font les requêtes SQL (SELECT, INSERT, UPDATE, DELETE). Elles font le pont entre Java et MySQL.

**Pourquoi séparer les DAO ?**  
Pour ne jamais mélanger la logique SQL avec l'affichage. Si la base de données change, on modifie uniquement le DAO, pas l'interface.

**Chaque DAO contient :**
- `findAll()` — récupère tous les enregistrements
- `insert()` — ajoute un nouvel enregistrement
- `update()` — modifie un enregistrement existant
- `delete()` — supprime un enregistrement

**DAO spéciaux :**

| Fichier | Fonctions spéciales |
|---|---|
| `EnseignantDAO.java` | `findByCredentials()` pour la connexion, `isUsernameTaken()` unicité du username |
| `InscriptionDAO.java` | `isDuplicate()` empêche un élève de s'inscrire deux fois la même année |
| `ClasseDAO.java` | `countEleves()` compte les élèves dans une classe pour respecter la limite de 20 |
| `NoteDAO.java` | `getMoyenne()` calcule la moyenne pondérée, `getRanking()` classe les élèves |
| `BulletinDAO.java` | `generateForAnnee()` génère automatiquement les bulletins de tous les élèves d'une année |

**Protection SQL :** Tous les DAO utilisent des `PreparedStatement` (jamais de concaténation de chaînes) ce qui protège contre les **injections SQL**.

---

### 🛡️ Dossier `util/` — Outils Partagés

#### `Validator.java`
**Rôle :** Contient **toutes les règles de validation** du projet en un seul endroit.  
**Pourquoi :** Si la règle change (ex: téléphone passe à 9 chiffres), on modifie un seul fichier.

| Méthode | Règle |
|---|---|
| `validateName()` | Lettres uniquement, minimum 2 caractères |
| `validateDate()` | Date valide, âge entre 5 et 25 ans |
| `validatePhone()` | Chiffres uniquement, exactement 8 chiffres (format tunisien) |
| `validateCapacity()` | Entre 1 et 20 élèves |
| `validateGrade()` | Entre 0.0 et 20.0 |
| `validateCoefficient()` | Entier positif |
| `validatePassword()` | Minimum 4 caractères |
| `validateYear()` | Entre 2000 et 2100 |
| `notEmpty()` | Champ non vide après espaces |
| `sanitize()` | Nettoie les caractères dangereux pour SQL |

Chaque méthode retourne un `ValidationResult` avec `valid` (true/false) et `message` (le message d'erreur à afficher).

---

#### `UIHelper.java`
**Rôle :** Outils visuels réutilisables pour tous les formulaires.  
**Pourquoi :** Éviter de répéter le même code d'affichage d'erreur dans chaque vue.

| Méthode | Ce qu'elle fait |
|---|---|
| `markError(field)` | Ajoute une bordure rouge au champ |
| `markOk(field)` | Ajoute une bordure verte au champ |
| `showFieldError()` | Affiche le message d'erreur sous le champ |
| `hideFieldError()` | Cache le message d'erreur |
| `showErrors()` | Fenêtre d'alerte avec la liste des erreurs |
| `showSuccess()` | Fenêtre de succès |
| `confirmDelete()` | Demande confirmation avant suppression |
| `fieldBox()` | Crée un groupe label + champ + message d'erreur |

---

### 🖥️ Dossier `view/` — Les Interfaces Utilisateur

> Les vues sont les **fenêtres JavaFX** que l'utilisateur voit et utilise.

#### `Dashboard.java`
**Rôle :** Le menu principal après connexion.  
**Ce qu'il affiche :** 9 tuiles (boutons) pour accéder à chaque module.

---

#### `EleveView.java`
**Rôle :** Gérer les fiches élèves (ajouter, modifier, supprimer, consulter).  
**Validations actives :**
- Nom et Prénom : lettres uniquement, min 2 caractères
- Date de naissance : format jj/MM/aaaa, âge 5–25 ans
- Téléphone parent : 8 chiffres exactement

---

#### `AnneeScolaireView.java`
**Rôle :** Gérer les années scolaires (ex: 2025-2026).  
**Validations actives :**
- Libellé non vide
- Date de fin doit être après la date de début

---

#### `NiveauView.java`
**Rôle :** Gérer les niveaux d'étude (1ère année, 2ème année…).  
**Simple :** Un seul champ texte, validation : non vide.

---

#### `ClasseView.java`
**Rôle :** Gérer les classes scolaires.  
**Validations actives :**
- Capacité maximale = **20 élèves** (règle du prof)
- Niveau obligatoire (liste déroulante)

---

#### `MatiereView.java`
**Rôle :** Gérer les matières d'enseignement.  
**Validations actives :** Nom non vide + niveau sélectionné.

---

#### `EnseignantView.java`
**Rôle :** Gérer les comptes enseignants.  
**Validations actives :**
- Nom/Prénom : lettres uniquement
- Téléphone : 8 chiffres
- Username : unique dans la base de données
- Mot de passe : minimum 4 caractères

---

#### `InscriptionView.java`
**Rôle :** Inscrire un élève dans une année scolaire et un niveau.  
**Règle critique :** Un élève ne peut être inscrit **qu'une seule fois** par année scolaire. Toute tentative en double est bloquée avec un message d'erreur.

---

#### `NoteView.java`
**Rôle :** Saisir les notes des élèves par matière et par trimestre (T1, T2, T3).  
**Validations actives :**
- Note : entre 0 et 20
- Coefficient : entier positif
**Fonctionnalité bonus :** Affiche la **moyenne actuelle** de l'élève sélectionné en temps réel.

---

#### `BulletinView.java`
**Rôle :** Générer et consulter les bulletins scolaires.  
**Fonctionnement :** Sélectionner une année scolaire → cliquer **"Générer Bulletins"** → le système calcule automatiquement pour chaque élève inscrit :
- La **moyenne pondérée** `SUM(note × coef) / SUM(coef)`
- Le **rang** dans la classe
- L'**appréciation** : Très Bien (≥16), Bien (≥14), Assez Bien (≥12), Passable (≥10), Insuffisant (<10)
- Les **top 3** sont affichés en 🥇 Or, 🥈 Argent, 🥉 Bronze

---

#### `RankingView.java`
**Rôle :** Afficher le classement des élèves par moyenne pondérée décroissante.  
**Accès :** Depuis le bouton "Voir Classement" dans NoteView.

---

## 🗄️ Base de Données — `school_db.sql`

Contient le schéma exact demandé par le professeur avec **11 tables** :

```
eleve → inscription → affectation → classe → niveau
                   ↘ annee_scolaire
matiere → niveau
enseignant
note → eleve, matiere
bulletin → eleve, annee_scolaire
```

**Table `enseignant`** — Compte par défaut créé automatiquement :
- **Username :** `admin`
- **Password :** `admin`

---

## 🚀 Instructions pour Lancer le Projet

### Étape 1 — Préparer la Base de Données
1. Ouvrir **XAMPP** → Démarrer **Apache** et **MySQL**
2. Aller sur `http://localhost/phpmyadmin`
3. Cliquer sur **Importer** → Sélectionner `school_db.sql`
4. Cliquer **Exécuter**

### Étape 2 — Configurer Eclipse
1. Télécharger `mysql-connector-j-8.x.x.jar` depuis [mysql.com](https://dev.mysql.com/downloads/connector/j/)
2. Dans Eclipse : Clic droit sur le projet → **Build Path** → **Add External JARs**
3. Sélectionner le fichier `.jar` téléchargé

### Étape 3 — Lancer l'Application
1. Ouvrir `Main.java`
2. Clic droit → **Run As** → **Java Application**
3. La fenêtre de connexion s'affiche
4. Entrer : **admin** / **admin**

---

## 🔒 Règles de Validation (Résumé)

| Champ | Règle |
|---|---|
| Nom / Prénom | Lettres uniquement, minimum 2 caractères |
| Date de naissance | Format jj/MM/aaaa, âge entre 5 et 25 ans |
| Téléphone | 8 chiffres exactement (format tunisien) |
| Capacité classe | Maximum **20 élèves** |
| Note | Entre **0** et **20** |
| Coefficient | Entier **> 0** |
| Mot de passe | Minimum **4 caractères** |
| Inscription | Un élève **une seule fois** par année scolaire |
| Username | **Unique** dans la base de données |

**Comportement en cas d'erreur :**
- Le champ devient rouge 🔴
- Un message explicatif apparaît sous le champ
- Le formulaire **ne peut pas être soumis** tant qu'il y a des erreurs

---

## 🧱 Architecture (Pourquoi ces Couches ?)

```
View (JavaFX)
    ↓  appelle
Validator (util)   ← vérifie les données AVANT d'appeler la BD
    ↓  si valide
DAO (JDBC)
    ↓  requête SQL
MySQL (XAMPP)
```

| Couche | Responsabilité unique |
|---|---|
| `model` | Définir la structure des données |
| `dao` | Parler à la base de données |
| `util` | Valider et aider l'interface |
| `view` | Afficher et interagir avec l'utilisateur |

Cette séparation est appelée **Architecture en Couches**. Si demain on veut utiliser PostgreSQL à la place de MySQL, on modifie uniquement les DAO — rien d'autre ne change.

---

*Projet développé dans le cadre du cours de Programmation Orientée Objet Avancée — 2025/2026*
