-- ============================================================
-- SEED DATA FOR SCHOOL MANAGEMENT SYSTEM (EduScale Pro)
-- Run this in phpMyAdmin or your MySQL workbench to populate the app
-- ============================================================

USE school_db;

-- 1. ANNEES SCOLAIRES
INSERT INTO annee_scolaire (annee, date_debut, date_fin) VALUES 
('2023/2024', '2023-09-15', '2024-06-30'),
('2024/2025', '2024-09-15', '2025-06-30');

-- 2. NIVEAUX
INSERT INTO niveau (nom) VALUES 
('7ème Année de Base'),
('8ème Année de Base'),
('9ème Année de Base');

-- 3. CLASSES
INSERT INTO classe (nom, capacite_max, niveau_id) VALUES 
('7ème B1', 20, 1),
('7ème B2', 20, 1),
('8ème B1', 20, 2),
('9ème B1', 20, 3);

-- 4. MATIERES
INSERT INTO matiere (nom, niveau_id) VALUES 
('Mathématiques', 1),
('Physique', 1),
('Français', 1),
('Anglais', 1),
('Mathématiques', 2),
('Physique', 3);

-- 5. ELEVES
INSERT INTO eleve (nom, prenom, date_naissance, adresse, tel_parent) VALUES 
('Trabelsi', 'Ahmed', '2010-05-12', 'Tunis, Centre Ville', '98123456'),
('Ben Ali', 'Sarra', '2011-03-22', 'Ariana, Ennasr', '22445566'),
('Gharbi', 'Yassine', '2010-11-05', 'Bizerte', '55112233'),
('Mahmoudi', 'Mariem', '2012-01-15', 'Sousse', '44778899'),
('Kacem', 'Omar', '2010-08-30', 'Kairouan', '21334455');

-- 6. ENSEIGNANTS (Username: nom.prenom, Pass: 1234)
-- Admin already exists (admin/admin)
INSERT INTO enseignant (nom, prenom, telephone, username, password) VALUES 
('Mansouri', 'Hassen', '50667788', 'mansouri.h', '1234'),
('Selmi', 'Ines', '40556677', 'selmi.i', '1234');

-- 7. INSCRIPTIONS (Ahmed et Sarra en 2024/2025)
INSERT INTO inscription (eleve_id, annee_id, niveau_id) VALUES 
(1, 2, 1), -- Ahmed en 7ème (2024/2025)
(2, 2, 1), -- Sarra en 7ème (2024/2025)
(3, 1, 2); -- Yassine en 8ème (2023/2024)

-- 8. NOTES (Ahmed)
INSERT INTO note (eleve_id, matiere_id, trimestre, valeur, coefficient) VALUES 
(1, 1, 1, 15.5, 2), -- Math (Ahmed)
(1, 2, 1, 12.0, 1), -- Physique (Ahmed)
(1, 3, 1, 14.0, 1), -- Français (Ahmed)
(2, 1, 1, 18.0, 2); -- Math (Sarra)
