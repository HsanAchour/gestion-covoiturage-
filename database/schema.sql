-- ==========================================================
-- IHEC Ride - Base de données MySQL (WAMP port 3301)
-- ==========================================================

DROP DATABASE IF EXISTS ihec_ride;
CREATE DATABASE ihec_ride CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ihec_ride;

-- ----------------------------------------------------------
-- Table des utilisateurs
-- ----------------------------------------------------------
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(80) NOT NULL,
    prenom VARCHAR(80) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telephone VARCHAR(20) NOT NULL,
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    photo VARCHAR(255) DEFAULT 'default.png',
    role ENUM('PASSAGER','CONDUCTEUR','ADMIN') NOT NULL,
    note_moyenne DECIMAL(3,2) DEFAULT 0.00,
    statut ENUM('ACTIF','BANNI','SUSPENDU') DEFAULT 'ACTIF',
    contact_confiance VARCHAR(20) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB;

-- ----------------------------------------------------------
-- Table des trajets
-- ----------------------------------------------------------
CREATE TABLE trajets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    conducteur_id INT NOT NULL,
    depart VARCHAR(150) NOT NULL,
    destination VARCHAR(150) NOT NULL,
    date_heure DATETIME NOT NULL,
    prix DECIMAL(6,2) NOT NULL,
    places_total INT NOT NULL,
    places_dispo INT NOT NULL,
    description TEXT,
    statut ENUM('OUVERT','COMPLET','EN_COURS','TERMINE','ANNULE') DEFAULT 'OUVERT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conducteur_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_depart (depart),
    INDEX idx_destination (destination),
    INDEX idx_date (date_heure)
) ENGINE=InnoDB;

-- ----------------------------------------------------------
-- Table des réservations
-- ----------------------------------------------------------
CREATE TABLE reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    trajet_id INT NOT NULL,
    passager_id INT NOT NULL,
    statut ENUM('EN_ATTENTE','ACCEPTE','REFUSE','ANNULE') DEFAULT 'EN_ATTENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trajet_id) REFERENCES trajets(id) ON DELETE CASCADE,
    FOREIGN KEY (passager_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_reservation (trajet_id, passager_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------
-- Table des messages privés
-- ----------------------------------------------------------
CREATE TABLE messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    expediteur_id INT NOT NULL,
    destinataire_id INT NOT NULL,
    contenu TEXT NOT NULL,
    date_envoi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lu BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (expediteur_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (destinataire_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_destinataire (destinataire_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------
-- Groupes de discussion
-- ----------------------------------------------------------
CREATE TABLE groupes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    trajet_id INT NOT NULL,
    conducteur_id INT NOT NULL,
    nom VARCHAR(150) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trajet_id) REFERENCES trajets(id) ON DELETE CASCADE,
    FOREIGN KEY (conducteur_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE membres_groupe (
    groupe_id INT NOT NULL,
    user_id INT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (groupe_id, user_id),
    FOREIGN KEY (groupe_id) REFERENCES groupes(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE messages_groupe (
    id INT AUTO_INCREMENT PRIMARY KEY,
    groupe_id INT NOT NULL,
    expediteur_id INT NOT NULL,
    contenu TEXT NOT NULL,
    date_envoi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (groupe_id) REFERENCES groupes(id) ON DELETE CASCADE,
    FOREIGN KEY (expediteur_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------
-- Évaluations
-- ----------------------------------------------------------
CREATE TABLE evaluations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    trajet_id INT NOT NULL,
    evaluateur_id INT NOT NULL,
    evalue_id INT NOT NULL,
    note INT NOT NULL CHECK (note BETWEEN 1 AND 5),
    commentaire TEXT,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trajet_id) REFERENCES trajets(id) ON DELETE CASCADE,
    FOREIGN KEY (evaluateur_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (evalue_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------
-- Réclamations
-- ----------------------------------------------------------
CREATE TABLE reclamations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    declarant_id INT NOT NULL,
    cible_id INT DEFAULT NULL,
    type VARCHAR(80) NOT NULL,
    description TEXT NOT NULL,
    statut ENUM('OUVERT','EN_TRAITEMENT','CLOTURE') DEFAULT 'OUVERT',
    date_soumission TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (declarant_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (cible_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ----------------------------------------------------------
-- Alertes SOS
-- ----------------------------------------------------------
CREATE TABLE sos_alerts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('ENVOYE','TRAITE') DEFAULT 'ENVOYE',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------
-- Notifications
-- ----------------------------------------------------------
CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    titre VARCHAR(150) NOT NULL,
    contenu TEXT,
    type VARCHAR(40),
    lu BOOLEAN DEFAULT FALSE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user (user_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------
-- Suivi GPS temps réel
-- ----------------------------------------------------------
CREATE TABLE suivi_gps (
    id INT AUTO_INCREMENT PRIMARY KEY,
    trajet_id INT NOT NULL,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trajet_id) REFERENCES trajets(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================================================
-- Base vide : aucune donnée pré-remplie.
-- Les comptes seront créés via le formulaire d'inscription.
-- ==========================================================
