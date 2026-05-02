package com.ihecride.controllers;

import com.ihecride.dao.ReclamationDAO;
import com.ihecride.models.Reclamation;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/reclamations")
public class ReclamationController extends HttpServlet {

    private final ReclamationDAO dao = new ReclamationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain; charset=UTF-8");
        int userId = (int) req.getAttribute("userId");
        List<Reclamation> list = dao.findByDeclarant(userId);
        try (PrintWriter out = resp.getWriter()) {
            out.println("id|type|description|statut|dateSoumission");
            for (Reclamation r : list) {
                out.println(r.getId() + "|" + safe(r.getType()) + "|" +
                            safe(r.getDescription()) + "|" + r.getStatut() + "|" + r.getDateSoumission());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int userId = (int) req.getAttribute("userId");
        Reclamation r = new Reclamation();
        r.setDeclarantId(userId);
        r.setType(req.getParameter("type"));
        r.setDescription(req.getParameter("description"));
        int id = dao.insert(r);
        resp.getWriter().write("success=" + (id > 0) + "&id=" + id +
            "&message=Votre réclamation a été enregistrée. Un agent vous répondra sous 24h.");
    }

    private String safe(String s) { return s == null ? "" : s.replace("|", "/").replace("\n"," "); }
}
