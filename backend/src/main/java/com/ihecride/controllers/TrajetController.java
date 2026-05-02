package com.ihecride.controllers;

import com.ihecride.dao.TrajetDAO;
import com.ihecride.models.Trajet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.List;

/**
 * Contrôleur REST : /api/trajets
 * Retourne un format pseudo-CSV pour éviter JSON.
 */
@WebServlet({"/api/trajets", "/api/trajets/search", "/api/trajets/mine"})
public class TrajetController extends HttpServlet {

    private final TrajetDAO dao = new TrajetDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain; charset=UTF-8");
        String path = req.getRequestURI();
        List<Trajet> list;

        if (path.endsWith("/search")) {
            list = dao.search(
                req.getParameter("depart"),
                req.getParameter("dest"),
                req.getParameter("date"));
        } else if (path.endsWith("/mine")) {
            int userId = (int) req.getAttribute("userId");
            list = dao.findByConducteur(userId);
        } else {
            list = dao.findAll();
        }

        try (PrintWriter out = resp.getWriter()) {
            out.println("id|conducteurId|conducteurNom|depart|destination|dateHeure|prix|placesDispo|placesTotal|statut|description");
            for (Trajet t : list) {
                out.println(escape(t.getId()) + "|" +
                            escape(t.getConducteurId()) + "|" +
                            escape(t.getConducteurNom()) + "|" +
                            escape(t.getDepart()) + "|" +
                            escape(t.getDestination()) + "|" +
                            escape(t.getDateHeure()) + "|" +
                            escape(t.getPrix()) + "|" +
                            escape(t.getPlacesDispo()) + "|" +
                            escape(t.getPlacesTotal()) + "|" +
                            escape(t.getStatut()) + "|" +
                            escape(t.getDescription()));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String role = (String) req.getAttribute("role");
        if (!"CONDUCTEUR".equals(role)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("success=false&message=" + encode("Réservé aux conducteurs"));
            return;
        }

        try {
            Trajet t = new Trajet();
            t.setConducteurId((int) req.getAttribute("userId"));
            t.setDepart(req.getParameter("depart"));
            t.setDestination(req.getParameter("destination"));
            t.setDateHeure(Timestamp.valueOf(req.getParameter("dateHeure").replace("T", " ") + ":00"));
            t.setPrix(new BigDecimal(req.getParameter("prix")));
            t.setPlacesTotal(Integer.parseInt(req.getParameter("places")));
            t.setDescription(req.getParameter("description"));

            int id = dao.insert(t);
            if (id > 0) {
                resp.getWriter().write("success=true&id=" + id + "&message=" + encode("Trajet créé avec succès"));
            } else {
                resp.getWriter().write("success=false&message=" + encode("Erreur de création"));
            }
        } catch (Exception e) {
            resp.getWriter().write("success=false&message=" + encode("Données invalides : " + e.getMessage()));
        }
    }

    private String escape(Object o) { return o == null ? "" : o.toString().replace("|", "/"); }
    private String encode(String s) {
        try { return URLEncoder.encode(s == null ? "" : s, "UTF-8"); } catch (Exception e) { return ""; }
    }
}
