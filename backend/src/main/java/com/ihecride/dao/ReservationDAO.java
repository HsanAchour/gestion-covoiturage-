package com.ihecride.dao;

import com.ihecride.models.Reservation;
import com.ihecride.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public int insert(Reservation r) {
        String sql = "INSERT INTO reservations(trajet_id, passager_id) VALUES(?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getTrajetId());
            ps.setInt(2, r.getPassagerId());
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public List<Reservation> findByPassager(int passagerId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE passager_id = ? ORDER BY created_at DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, passagerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Reservation> findByTrajet(int trajetId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, CONCAT(u.prenom,' ',u.nom) AS passager_nom FROM reservations r JOIN users u ON r.passager_id=u.id WHERE r.trajet_id = ? ORDER BY r.created_at DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, trajetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateStatut(int id, String statut) {
        String sql = "UPDATE reservations SET statut = ? WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statut);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Reservation map(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));
        r.setTrajetId(rs.getInt("trajet_id"));
        r.setPassagerId(rs.getInt("passager_id"));
        try { r.setPassagerNom(rs.getString("passager_nom")); } catch (Exception ignored) {}
        r.setStatut(rs.getString("statut"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }
}
