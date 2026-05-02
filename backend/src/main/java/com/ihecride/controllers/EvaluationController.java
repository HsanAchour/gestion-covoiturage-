package com.ihecride.controllers;

import com.ihecride.dao.EvaluationDAO;
import com.ihecride.dao.UserDAO;
import com.ihecride.models.Evaluation;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet({"/api/evaluations", "/api/evaluations/conducteur"})
public class EvaluationController extends HttpServlet {

    private final EvaluationDAO dao = new EvaluationDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain; charset=UTF-8");
        int evalueId = Integer.parseInt(req.getParameter("id"));
        List<Evaluation> list = dao.findByEvalue(evalueId);
        try (PrintWriter out = resp.getWriter()) {
            out.println("id|note|commentaire|evaluateurNom|date");
            for (Evaluation e : list) {
                out.println(e.getId() + "|" + e.getNote() + "|" +
                            safe(e.getCommentaire()) + "|" + safe(e.getEvaluateurNom()) + "|" + e.getDate());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int userId = (int) req.getAttribute("userId");
        Evaluation e = new Evaluation();
        e.setEvaluateurId(userId);
        e.setEvalueId(Integer.parseInt(req.getParameter("evalueId")));
        e.setTrajetId(Integer.parseInt(req.getParameter("trajetId")));
        e.setNote(Integer.parseInt(req.getParameter("note")));
        e.setCommentaire(req.getParameter("commentaire"));
        int id = dao.insert(e);
        if (id > 0) userDAO.updateNoteMoyenne(e.getEvalueId());
        resp.getWriter().write("success=" + (id > 0));
    }

    private String safe(String s) { return s == null ? "" : s.replace("|", "/").replace("\n"," "); }
}
