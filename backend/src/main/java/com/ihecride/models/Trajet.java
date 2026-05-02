package com.ihecride.models;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Entité Trajet : un trajet proposé par un conducteur.
 */
public class Trajet {

    private int id;
    private int conducteurId;
    private String conducteurNom;
    private String depart;
    private String destination;
    private Timestamp dateHeure;
    private BigDecimal prix;
    private int placesTotal;
    private int placesDispo;
    private String description;
    private String statut;   // OUVERT, COMPLET, EN_COURS, TERMINE, ANNULE
    private Timestamp createdAt;

    public Trajet() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getConducteurId() { return conducteurId; }
    public void setConducteurId(int conducteurId) { this.conducteurId = conducteurId; }

    public String getConducteurNom() { return conducteurNom; }
    public void setConducteurNom(String conducteurNom) { this.conducteurNom = conducteurNom; }

    public String getDepart() { return depart; }
    public void setDepart(String depart) { this.depart = depart; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Timestamp getDateHeure() { return dateHeure; }
    public void setDateHeure(Timestamp dateHeure) { this.dateHeure = dateHeure; }

    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }

    public int getPlacesTotal() { return placesTotal; }
    public void setPlacesTotal(int placesTotal) { this.placesTotal = placesTotal; }

    public int getPlacesDispo() { return placesDispo; }
    public void setPlacesDispo(int placesDispo) { this.placesDispo = placesDispo; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
