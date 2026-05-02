package com.ihecride.dao;

import com.ihecride.models.Reclamation;
import com.ihecride.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationDAO {

    public int insert(Reclamation r) {
        String sql = "INSERT INTO reclamations(declarant_id, cible_id, type, description) VALUES(?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getDeclarantId());
            if (r.getCibleId() != null) ps.setInt(2, r.getCibleId()); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, r.getType());
            ps.setString(4, r.getDescription());
            if (ps.executeUpdate() > 0) {
                try (ResultSet k = ps.getGeneratedKeys()) {
                    if (k.next()) return k.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public List<Reclamation> findAll() {
        List<Reclamation> list = new ArrayList<>();
        String sql = "SELECT r.*, CONCAT(u.prenom,' ',u.nom) AS declarant_nom FROM reclamations r JOIN users u ON r.declarant_id=u.id ORDER BY date_soumission DESC";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Reclamation> findByDeclarant(int declarantId) {
        List<Reclamation> list = new ArrayList<>();
        String sql = "SELECT r.*, CONCAT(u.prenom,' ',u.nom) AS declarant_nom FROM reclamations r JOIN users u ON r.declarant_id=u.id WHERE declarant_id = ? ORDER BY date_soumission DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, declarantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateStatut(int id, String statut) {
        String sql = "UPDATE reclamations SET statut = ? WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statut);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Reclamation map(ResultSet rs) throws SQLException {
        Reclamation r = new Reclamation();
        r.setId(rs.getInt("id"));
        r.setDeclarantId(rs.getInt("declarant_id"));
        int cid = rs.getInt("cible_id");
        if (!rs.wasNull()) r.setCibleId(cid);
        try { r.setDeclarantNom(rs.getString("declarant_nom")); } catch (Exception ignored) {}
        r.setType(rs.getString("type"));
        r.setDescription(rs.getString("description"));
        r.setStatut(rs.getString("statut"));
        r.setDateSoumission(rs.getTimestamp("date_soumission"));
        return r;
    }
}
