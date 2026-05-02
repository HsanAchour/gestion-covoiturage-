package com.ihecride.dao;

import com.ihecride.models.Evaluation;
import com.ihecride.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvaluationDAO {

    public int insert(Evaluation e) {
        String sql = "INSERT INTO evaluations(trajet_id, evaluateur_id, evalue_id, note, commentaire) VALUES(?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getTrajetId());
            ps.setInt(2, e.getEvaluateurId());
            ps.setInt(3, e.getEvalueId());
            ps.setInt(4, e.getNote());
            ps.setString(5, e.getCommentaire());
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return -1;
    }

    public List<Evaluation> findByEvalue(int evalueId) {
        List<Evaluation> list = new ArrayList<>();
        String sql = "SELECT e.*, CONCAT(u.prenom,' ',u.nom) AS evaluateur_nom FROM evaluations e JOIN users u ON e.evaluateur_id = u.id WHERE e.evalue_id = ? ORDER BY e.date DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, evalueId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public double moyenne(int userId) {
        String sql = "SELECT AVG(note) FROM evaluations WHERE evalue_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    private Evaluation map(ResultSet rs) throws SQLException {
        Evaluation e = new Evaluation();
        e.setId(rs.getInt("id"));
        e.setTrajetId(rs.getInt("trajet_id"));
        e.setEvaluateurId(rs.getInt("evaluateur_id"));
        e.setEvalueId(rs.getInt("evalue_id"));
        try { e.setEvaluateurNom(rs.getString("evaluateur_nom")); } catch (Exception ignored) {}
        e.setNote(rs.getInt("note"));
        e.setCommentaire(rs.getString("commentaire"));
        e.setDate(rs.getTimestamp("date"));
        return e;
    }
}
