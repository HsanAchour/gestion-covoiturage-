package com.ihecride.dao;

import com.ihecride.models.Trajet;
import com.ihecride.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO : accès à la table trajets.
 */
public class TrajetDAO {

    public int insert(Trajet t) {
        String sql = "INSERT INTO trajets(conducteur_id, depart, destination, date_heure, prix, places_total, places_dispo, description) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getConducteurId());
            ps.setString(2, t.getDepart());
            ps.setString(3, t.getDestination());
            ps.setTimestamp(4, t.getDateHeure());
            ps.setBigDecimal(5, t.getPrix());
            ps.setInt(6, t.getPlacesTotal());
            ps.setInt(7, t.getPlacesTotal());  // dispo = total au départ
            ps.setString(8, t.getDescription());
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public Trajet findById(int id) {
        String sql = "SELECT t.*, CONCAT(u.prenom,' ',u.nom) AS conducteur_nom FROM trajets t JOIN users u ON t.conducteur_id = u.id WHERE t.id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Trajet> search(String depart, String destination, String date) {
        StringBuilder sql = new StringBuilder(
            "SELECT t.*, CONCAT(u.prenom,' ',u.nom) AS conducteur_nom FROM trajets t " +
            "JOIN users u ON t.conducteur_id = u.id WHERE t.statut='OUVERT' AND t.places_dispo > 0");
        List<Object> params = new ArrayList<>();

        if (depart != null && !depart.isEmpty()) {
            sql.append(" AND t.depart LIKE ?");
            params.add("%" + depart + "%");
        }
        if (destination != null && !destination.isEmpty()) {
            sql.append(" AND t.destination LIKE ?");
            params.add("%" + destination + "%");
        }
        if (date != null && !date.isEmpty()) {
            sql.append(" AND DATE(t.date_heure) = ?");
            params.add(date);
        }
        sql.append(" ORDER BY t.date_heure ASC");

        List<Trajet> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Trajet> findByConducteur(int conducteurId) {
        List<Trajet> list = new ArrayList<>();
        String sql = "SELECT t.*, CONCAT(u.prenom,' ',u.nom) AS conducteur_nom FROM trajets t JOIN users u ON t.conducteur_id=u.id WHERE conducteur_id = ? ORDER BY date_heure DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, conducteurId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Trajet> findAll() {
        List<Trajet> list = new ArrayList<>();
        String sql = "SELECT t.*, CONCAT(u.prenom,' ',u.nom) AS conducteur_nom FROM trajets t JOIN users u ON t.conducteur_id=u.id ORDER BY date_heure DESC";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateStatut(int id, String statut) {
        String sql = "UPDATE trajets SET statut = ? WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statut);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean decrementPlaces(int trajetId) {
        String sql = "UPDATE trajets SET places_dispo = places_dispo - 1 WHERE id = ? AND places_dispo > 0";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, trajetId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM trajets WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Trajet map(ResultSet rs) throws SQLException {
        Trajet t = new Trajet();
        t.setId(rs.getInt("id"));
        t.setConducteurId(rs.getInt("conducteur_id"));
        try { t.setConducteurNom(rs.getString("conducteur_nom")); } catch (Exception ignored) {}
        t.setDepart(rs.getString("depart"));
        t.setDestination(rs.getString("destination"));
        t.setDateHeure(rs.getTimestamp("date_heure"));
        t.setPrix(rs.getBigDecimal("prix"));
        t.setPlacesTotal(rs.getInt("places_total"));
        t.setPlacesDispo(rs.getInt("places_dispo"));
        t.setDescription(rs.getString("description"));
        t.setStatut(rs.getString("statut"));
        t.setCreatedAt(rs.getTimestamp("created_at"));
        return t;
    }
}
