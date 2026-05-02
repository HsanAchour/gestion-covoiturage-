package com.ihecride.controllers;

import com.ihecride.dao.ReclamationDAO;
import com.ihecride.dao.TrajetDAO;
import com.ihecride.dao.UserDAO;
import com.ihecride.models.Reclamation;
import com.ihecride.models.Trajet;
import com.ihecride.models.User;
import com.ihecride.utils.DBConnection;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

@WebServlet({"/api/admin/stats", "/api/admin/users", "/api/admin/trajets",
             "/api/admin/reclamations", "/api/admin/ban", "/api/admin/delete"})
public class AdminController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final TrajetDAO trajetDAO = new TrajetDAO();
    private final ReclamationDAO reclamationDAO = new ReclamationDAO();

    private boolean isAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!"ADMIN".equals(req.getAttribute("role"))) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("error=Accès refusé");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isAdmin(req, resp)) return;
        resp.setContentType("text/plain; charset=UTF-8");
        String path = req.getRequestURI();

        if (path.endsWith("/stats")) {
            writeStats(resp);
        } else if (path.endsWith("/users")) {
            writeUsers(resp);
        } else if (path.endsWith("/trajets")) {
            writeTrajets(resp);
        } else if (path.endsWith("/reclamations")) {
            writeReclamations(resp);
        }
    }

    private void writeStats(HttpServletResponse resp) throws IOException {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             PrintWriter out = resp.getWriter()) {

            ResultSet rs = st.executeQuery("SELECT " +
                "(SELECT COUNT(*) FROM users) AS users_total," +
                "(SELECT COUNT(*) FROM users WHERE role='PASSAGER') AS passagers," +
                "(SELECT COUNT(*) FROM users WHERE role='CONDUCTEUR') AS conducteurs," +
                "(SELECT COUNT(*) FROM trajets) AS trajets_total," +
                "(SELECT COUNT(*) FROM trajets WHERE statut='TERMINE') AS trajets_termines," +
                "(SELECT COUNT(*) FROM reservations) AS reservations_total," +
                "(SELECT COUNT(*) FROM reclamations WHERE statut='OUVERT') AS reclamations_ouvertes");
            if (rs.next()) {
                out.write("usersTotal=" + rs.getInt("users_total"));
                out.write("&passagers=" + rs.getInt("passagers"));
                out.write("&conducteurs=" + rs.getInt("conducteurs"));
                out.write("&trajetsTotal=" + rs.getInt("trajets_total"));
                out.write("&trajetsTermines=" + rs.getInt("trajets_termines"));
                out.write("&reservationsTotal=" + rs.getInt("reservations_total"));
                out.write("&reclamationsOuvertes=" + rs.getInt("reclamations_ouvertes"));
            }
        } catch (Exception e) {
            resp.getWriter().write("error=" + e.getMessage());
        }
    }

    private void writeUsers(HttpServletResponse resp) throws IOException {
        List<User> users = userDAO.findAll();
        try (PrintWriter out = resp.getWriter()) {
            out.println("id|nom|prenom|email|telephone|role|noteMoyenne|statut|createdAt");
            for (User u : users) {
                out.println(u.getId() + "|" + safe(u.getNom()) + "|" + safe(u.getPrenom()) + "|" +
                            safe(u.getEmail()) + "|" + safe(u.getTelephone()) + "|" +
                            u.getRole() + "|" + u.getNoteMoyenne() + "|" + u.getStatut() + "|" + u.getCreatedAt());
            }
        }
    }

    private void writeTrajets(HttpServletResponse resp) throws IOException {
        List<Trajet> list = trajetDAO.findAll();
        try (PrintWriter out = resp.getWriter()) {
            out.println("id|conducteurNom|depart|destination|dateHeure|prix|places|statut");
            for (Trajet t : list) {
                out.println(t.getId() + "|" + safe(t.getConducteurNom()) + "|" +
                            safe(t.getDepart()) + "|" + safe(t.getDestination()) + "|" +
                            t.getDateHeure() + "|" + t.getPrix() + "|" +
                            t.getPlacesDispo() + "/" + t.getPlacesTotal() + "|" + t.getStatut());
            }
        }
    }

    private void writeReclamations(HttpServletResponse resp) throws IOException {
        List<Reclamation> list = reclamationDAO.findAll();
        try (PrintWriter out = resp.getWriter()) {
            out.println("id|declarantNom|type|description|statut|dateSoumission");
            for (Reclamation r : list) {
                out.println(r.getId() + "|" + safe(r.getDeclarantNom()) + "|" +
                            safe(r.getType()) + "|" + safe(r.getDescription()) + "|" +
                            r.getStatut() + "|" + r.getDateSoumission());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isAdmin(req, resp)) return;
        req.setCharacterEncoding("UTF-8");
        String path = req.getRequestURI();
        int id = Integer.parseInt(req.getParameter("id"));

        if (path.endsWith("/ban")) {
            boolean ok = userDAO.updateStatut(id, "BANNI");
            resp.getWriter().write("success=" + ok);
        } else if (path.endsWith("/delete")) {
            boolean ok = userDAO.delete(id);
            resp.getWriter().write("success=" + ok);
        }
    }

    private String safe(String s) { return s == null ? "" : s.replace("|", "/"); }
}
