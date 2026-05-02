package com.ihecride.controllers;

import com.ihecride.models.User;
import com.ihecride.services.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Contrôleur d'authentification.
 *   POST /api/register   inscription
 *   POST /api/login      connexion
 *   POST /api/refresh    rafraîchissement JWT (placeholder)
 *
 * Anciens chemins /api/auth/* conservés pour compatibilité.
 * Réponses : application/json (UTF-8).
 */
@WebServlet({
    "/api/register", "/api/login", "/api/refresh",
    "/api/auth/register", "/api/auth/login", "/api/auth/refresh"
})
public class AuthController extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");
        String path = req.getRequestURI();

        try (PrintWriter out = resp.getWriter()) {
            try {
                if (path.endsWith("/register")) {
                    Map<String,Object> r = authService.register(
                        req.getParameter("nom"),
                        req.getParameter("prenom"),
                        req.getParameter("email"),
                        req.getParameter("telephone"),
                        req.getParameter("motDePasse"),
                        req.getParameter("role"));
                    if (!Boolean.TRUE.equals(r.get("success"))) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                    writeAuthResult(out, r);
                } else if (path.endsWith("/login")) {
                    Map<String,Object> r = authService.login(
                        req.getParameter("email"),
                        req.getParameter("motDePasse"));
                    if (!Boolean.TRUE.equals(r.get("success"))) {
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                    writeAuthResult(out, r);
                } else if (path.endsWith("/refresh")) {
                    resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                    out.write("{\"success\":false,\"message\":\"Rafraîchissement non implémenté\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.write("{\"success\":false,\"message\":\"Endpoint inconnu\"}");
                }
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"success\":false,\"message\":" + jsonStr("Erreur serveur : " + e.getMessage()) + "}");
            }
        }
    }

    private void writeAuthResult(PrintWriter out, Map<String,Object> r) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"success\":").append(Boolean.TRUE.equals(r.get("success")) ? "true" : "false");
        sb.append(",\"message\":").append(jsonStr((String) r.get("message")));
        if (Boolean.TRUE.equals(r.get("success"))) {
            sb.append(",\"token\":").append(jsonStr((String) r.get("token")));
            sb.append(",\"refresh\":").append(jsonStr((String) r.get("refresh")));
            User u = (User) r.get("user");
            sb.append(",\"userId\":").append(u.getId());
            sb.append(",\"role\":").append(jsonStr(u.getRole()));
            sb.append(",\"prenom\":").append(jsonStr(u.getPrenom()));
            sb.append(",\"nom\":").append(jsonStr(u.getNom()));
            sb.append(",\"email\":").append(jsonStr(u.getEmail()));
        }
        sb.append("}");
        out.write(sb.toString());
    }

    private String jsonStr(String s) {
        if (s == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.append("\"").toString();
    }
}
