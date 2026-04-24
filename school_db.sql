-- ============================================================
-- Système de Gestion Scolaire — Schéma exact du professeur
-- Importer dans phpMyAdmin (XAMPP)
-- ============================================================

CREATE DATABASE IF NOT EXISTS school_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE school_db;

-- ELEVE
CREATE TABLE IF NOT EXISTS eleve (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nom             VARCHAR(50)  NOT NULL,
    prenom          VARCHAR(50)  NOT NULL,
    date_naissance  DATE         NOT NULL,
    adresse         VARCHAR(100),
    tel_parent      VARCHAR(8)   NOT NULL
);

-- ANNEE SCOLAIRE
CREATE TABLE IF NOT EXISTS annee_scolaire (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    annee       VARCHAR(20) NOT NULL,
    date_debut  DATE        NOT NULL,
    date_fin    DATE        NOT NULL
);

-- NIVEAU
CREATE TABLE IF NOT EXISTS niveau (
    id  INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL
);

-- INSCRIPTION (un élève, une seule fois par année)
CREATE TABLE IF NOT EXISTS inscription (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    eleve_id    INT NOT NULL,
    annee_id    INT NOT NULL,
    niveau_id   INT NOT NULL,
    UNIQUE KEY unique_inscription (eleve_id, annee_id),
    FOREIGN KEY (eleve_id)  REFERENCES eleve(id)          ON DELETE CASCADE,
    FOREIGN KEY (annee_id)  REFERENCES annee_scolaire(id) ON DELETE CASCADE,
    FOREIGN KEY (niveau_id) REFERENCES niveau(id)         ON DELETE CASCADE
);

-- CLASSE
CREATE TABLE IF NOT EXISTS classe (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nom          VARCHAR(50) NOT NULL,
    capacite_max INT NOT NULL DEFAULT 20,
    niveau_id    INT NOT NULL,
    FOREIGN KEY (niveau_id) REFERENCES niveau(id) ON DELETE CASCADE
);

-- AFFECTATION (inscription → classe)
CREATE TABLE IF NOT EXISTS affectation (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    inscription_id   INT  NOT NULL,
    classe_id        INT  NOT NULL,
    date_affectation DATE NOT NULL,
    FOREIGN KEY (inscription_id) REFERENCES inscription(id) ON DELETE CASCADE,
    FOREIGN KEY (classe_id)      REFERENCES classe(id)      ON DELETE CASCADE
);

-- MATIERE
CREATE TABLE IF NOT EXISTS matiere (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nom       VARCHAR(50) NOT NULL,
    niveau_id INT NOT NULL,
    FOREIGN KEY (niveau_id) REFERENCES niveau(id) ON DELETE CASCADE
);

-- ENSEIGNANT
CREATE TABLE IF NOT EXISTS enseignant (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nom         VARCHAR(50) NOT NULL,
    prenom      VARCHAR(50) NOT NULL,
    telephone   VARCHAR(8)  NOT NULL,
    username    VARCHAR(50) NOT NULL UNIQUE,
    password    VARCHAR(50) NOT NULL
);

-- ENSEIGNER (enseignant ↔ matière ↔ classe)
CREATE TABLE IF NOT EXISTS enseigner (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    enseignant_id  INT NOT NULL,
    matiere_id     INT NOT NULL,
    classe_id      INT NOT NULL,
    FOREIGN KEY (enseignant_id) REFERENCES enseignant(id) ON DELETE CASCADE,
    FOREIGN KEY (matiere_id)    REFERENCES matiere(id)    ON DELETE CASCADE,
    FOREIGN KEY (classe_id)     REFERENCES classe(id)     ON DELETE CASCADE
);

-- NOTE (une note par élève, matière et trimestre)
CREATE TABLE IF NOT EXISTS note (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    eleve_id    INT   NOT NULL,
    matiere_id  INT   NOT NULL,
    trimestre   INT   NOT NULL CHECK (trimestre BETWEEN 1 AND 3),
    valeur      FLOAT NOT NULL,
    coefficient INT   NOT NULL,
    FOREIGN KEY (eleve_id)   REFERENCES eleve(id)   ON DELETE CASCADE,
    FOREIGN KEY (matiere_id) REFERENCES matiere(id) ON DELETE CASCADE
);

-- BULLETIN
CREATE TABLE IF NOT EXISTS bulletin (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    eleve_id     INT   NOT NULL,
    annee_id     INT   NOT NULL,
    moyenne      FLOAT,
    rang         INT,
    appreciation TEXT,
    FOREIGN KEY (eleve_id)  REFERENCES eleve(id)          ON DELETE CASCADE,
    FOREIGN KEY (annee_id)  REFERENCES annee_scolaire(id) ON DELETE CASCADE
);

-- ============================================================
-- Compte enseignant par défaut (admin)
-- ============================================================
INSERT IGNORE INTO enseignant (nom, prenom, telephone, username, password)
VALUES ('Admin', 'System', '12345678', 'admin', 'admin');
