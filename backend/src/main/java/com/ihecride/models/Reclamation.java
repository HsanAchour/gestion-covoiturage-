package com.ihecride.models;

import java.sql.Timestamp;

public class Reclamation {
    private int id;
    private int declarantId;
    private Integer cibleId;
    private String declarantNom;
    private String type;
    private String description;
    private String statut;   // OUVERT, EN_TRAITEMENT, CLOTURE
    private Timestamp dateSoumission;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getDeclarantId() { return declarantId; }
    public void setDeclarantId(int declarantId) { this.declarantId = declarantId; }
    public Integer getCibleId() { return cibleId; }
    public void setCibleId(Integer cibleId) { this.cibleId = cibleId; }
    public String getDeclarantNom() { return declarantNom; }
    public void setDeclarantNom(String declarantNom) { this.declarantNom = declarantNom; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Timestamp getDateSoumission() { return dateSoumission; }
    public void setDateSoumission(Timestamp dateSoumission) { this.dateSoumission = dateSoumission; }
}
