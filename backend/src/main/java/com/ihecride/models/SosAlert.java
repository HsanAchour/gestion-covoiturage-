package com.ihecride.models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class SosAlert {
    private int id;
    private int userId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Timestamp timestamp;
    private String statut;  // ENVOYE, TRAITE

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
