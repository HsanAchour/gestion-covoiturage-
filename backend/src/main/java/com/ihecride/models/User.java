package com.ihecride.models;

import java.sql.Timestamp;

/**
 * Entité User : représente un utilisateur (passager, conducteur ou admin).
 */
public class User {

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String motDePasseHash;
    private String photo;
    private String role;           // PASSAGER, CONDUCTEUR, ADMIN
    private double noteMoyenne;
    private String statut;         // ACTIF, BANNI, SUSPENDU
    private String contactConfiance;
    private Timestamp createdAt;

    public User() {}

    public User(String nom, String prenom, String email, String telephone,
                String motDePasseHash, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.motDePasseHash = motDePasseHash;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getMotDePasseHash() { return motDePasseHash; }
    public void setMotDePasseHash(String motDePasseHash) { this.motDePasseHash = motDePasseHash; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public double getNoteMoyenne() { return noteMoyenne; }
    public void setNoteMoyenne(double noteMoyenne) { this.noteMoyenne = noteMoyenne; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getContactConfiance() { return contactConfiance; }
    public void setContactConfiance(String contactConfiance) { this.contactConfiance = contactConfiance; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
