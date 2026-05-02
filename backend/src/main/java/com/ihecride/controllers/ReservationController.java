package com.ihecride.controllers;

import com.ihecride.dao.ReservationDAO;
import com.ihecride.dao.TrajetDAO;
import com.ihecride.models.Reservation;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;

@WebServlet({"/api/reservations", "/api/reservations/trajet", "/api/reservations/statut"})
public class ReservationController extends HttpServlet {

    private final ReservationDAO dao = new ReservationDAO();
    private final TrajetDAO trajetDAO = new TrajetDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain; charset=UTF-8");
        int userId = (int) req.getAttribute("userId");
        String path = req.getRequestURI();
        List<Reservation> list;

        if (path.endsWith("/trajet")) {
            int trajetId = Integer.parseInt(req.getParameter("trajetId"));
            list = dao.findByTrajet(trajetId);
        } else {
            list = dao.findByPassager(userId);
        }

        try (PrintWriter out = resp.getWriter()) {
            out.println("id|trajetId|passagerId|passagerNom|statut|createdAt");
            for (Reservation r : list) {
                out.println(r.getId() + "|" + r.getTrajetId() + "|" + r.getPassagerId() + "|" +
                            safe(r.getPassagerNom()) + "|" + r.getStatut() + "|" + r.getCreatedAt());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int userId = (int) req.getAttribute("userId");
        Reservation r = new Reservation();
        r.setPassagerId(userId);
        r.setTrajetId(Integer.parseInt(req.getParameter("trajetId")));
        int id = dao.insert(r);
        if (id > 0) {
            resp.getWriter().write("success=true&id=" + id + "&message=" + encode("Demande de réservation envoyée"));
        } else {
            resp.getWriter().write("success=false&message=" + encode("Vous avez déjà réservé ce trajet"));
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int id = Integer.parseInt(req.getParameter("id"));
        String statut = req.getParameter("statut");
        boolean ok = dao.updateStatut(id, statut);
        if (ok && "ACCEPTE".equals(statut)) {
            int trajetId = Integer.parseInt(req.getParameter("trajetId"));
            trajetDAO.decrementPlaces(trajetId);
        }
        resp.getWriter().write("success=" + ok);
    }

    private String safe(String s) { return s == null ? "" : s.replace("|", "/"); }
    private String encode(String s) {
        try { return URLEncoder.encode(s == null ? "" : s, "UTF-8"); } catch (Exception e) { return ""; }
    }
}
