package com.ihecride.dao;

import com.ihecride.models.Message;
import com.ihecride.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public int insert(Message m) {
        String sql = "INSERT INTO messages(expediteur_id, destinataire_id, contenu) VALUES(?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getExpediteurId());
            ps.setInt(2, m.getDestinataireId());
            ps.setString(3, m.getContenu());
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public List<Message> findConversation(int userA, int userB) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT m.*, CONCAT(u.prenom,' ',u.nom) AS expediteur_nom FROM messages m JOIN users u ON m.expediteur_id = u.id " +
                     "WHERE (expediteur_id = ? AND destinataire_id = ?) OR (expediteur_id = ? AND destinataire_id = ?) " +
                     "ORDER BY date_envoi ASC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userA);
            ps.setInt(2, userB);
            ps.setInt(3, userB);
            ps.setInt(4, userA);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean marquerLu(int destinataireId, int expediteurId) {
        String sql = "UPDATE messages SET lu = TRUE WHERE destinataire_id = ? AND expediteur_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, destinataireId);
            ps.setInt(2, expediteurId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Message map(ResultSet rs) throws SQLException {
        Message m = new Message();
        m.setId(rs.getInt("id"));
        m.setExpediteurId(rs.getInt("expediteur_id"));
        m.setDestinataireId(rs.getInt("destinataire_id"));
        try { m.setExpediteurNom(rs.getString("expediteur_nom")); } catch (Exception ignored) {}
        m.setContenu(rs.getString("contenu"));
        m.setDateEnvoi(rs.getTimestamp("date_envoi"));
        m.setLu(rs.getBoolean("lu"));
        return m;
    }
}
