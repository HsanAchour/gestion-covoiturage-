package com.ihecride.controllers;

import com.ihecride.utils.DBConnection;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * POST /api/sos — enregistre une alerte SOS avec géolocalisation.
 * Rate-limited et loggé. Confirmation < 3s.
 */
@WebServlet("/api/sos")
public class SosController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int userId = (int) req.getAttribute("userId");
        try {
            BigDecimal lat = new BigDecimal(req.getParameter("latitude"));
            BigDecimal lon = new BigDecimal(req.getParameter("longitude"));
            String sql = "INSERT INTO sos_alerts(user_id, latitude, longitude) VALUES(?,?,?)";
            try (Connection c = DBConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setBigDecimal(2, lat);
                ps.setBigDecimal(3, lon);
                ps.executeUpdate();
            }
            resp.getWriter().write("success=true&message=Alerte SOS envoyée à l'administration");
        } catch (Exception e) {
            resp.getWriter().write("success=false&message=Erreur envoi SOS");
        }
    }
}
