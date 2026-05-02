package com.ihecride.models;

import java.sql.Timestamp;

/**
 * Entité Reservation : lien passager ↔ trajet.
 */
public class Reservation {

    private int id;
    private int trajetId;
    private int passagerId;
    private String passagerNom;
    private String statut;      // EN_ATTENTE, ACCEPTE, REFUSE, ANNULE
    private Timestamp createdAt;

    public Reservation() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTrajetId() { return trajetId; }
    public void setTrajetId(int trajetId) { this.trajetId = trajetId; }

    public int getPassagerId() { return passagerId; }
    public void setPassagerId(int passagerId) { this.passagerId = passagerId; }

    public String getPassagerNom() { return passagerNom; }
    public void setPassagerNom(String passagerNom) { this.passagerNom = passagerNom; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
