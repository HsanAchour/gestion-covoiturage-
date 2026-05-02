package com.ihecride.models;

import java.sql.Timestamp;

public class Evaluation {
    private int id;
    private int trajetId;
    private int evaluateurId;
    private int evalueId;
    private String evaluateurNom;
    private int note;
    private String commentaire;
    private Timestamp date;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTrajetId() { return trajetId; }
    public void setTrajetId(int trajetId) { this.trajetId = trajetId; }
    public int getEvaluateurId() { return evaluateurId; }
    public void setEvaluateurId(int evaluateurId) { this.evaluateurId = evaluateurId; }
    public int getEvalueId() { return evalueId; }
    public void setEvalueId(int evalueId) { this.evalueId = evalueId; }
    public String getEvaluateurNom() { return evaluateurNom; }
    public void setEvaluateurNom(String evaluateurNom) { this.evaluateurNom = evaluateurNom; }
    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }
}
