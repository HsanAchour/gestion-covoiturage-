package com.ihecride.controllers;

import com.ihecride.dao.MessageDAO;
import com.ihecride.models.Message;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet({"/api/messages", "/api/messages/conversation"})
public class MessageController extends HttpServlet {

    private final MessageDAO dao = new MessageDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain; charset=UTF-8");
        int userId = (int) req.getAttribute("userId");
        int other = Integer.parseInt(req.getParameter("userId"));
        List<Message> list = dao.findConversation(userId, other);
        dao.marquerLu(userId, other);
        try (PrintWriter out = resp.getWriter()) {
            out.println("id|expediteurId|expediteurNom|contenu|dateEnvoi|lu");
            for (Message m : list) {
                out.println(m.getId() + "|" + m.getExpediteurId() + "|" +
                            safe(m.getExpediteurNom()) + "|" +
                            safe(m.getContenu()) + "|" + m.getDateEnvoi() + "|" + m.isLu());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int userId = (int) req.getAttribute("userId");
        Message m = new Message();
        m.setExpediteurId(userId);
        m.setDestinataireId(Integer.parseInt(req.getParameter("destinataireId")));
        m.setContenu(req.getParameter("contenu"));
        int id = dao.insert(m);
        resp.getWriter().write("success=" + (id > 0) + "&id=" + id);
    }

    private String safe(String s) { return s == null ? "" : s.replace("|", "/").replace("\n"," "); }
}
