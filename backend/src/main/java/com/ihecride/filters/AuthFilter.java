package com.ihecride.filters;

import com.ihecride.utils.JwtUtil;
import io.jsonwebtoken.Claims;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Filtre CORS + authentification JWT.
 *
 * Chemins publics (sans token) :
 *   /api/register, /api/login, /api/refresh
 *   /api/auth/register, /api/auth/login, /api/auth/refresh   (rétro-compat)
 *
 * CORS : autorise localhost (port 80, 8080, file://) en dev.
 * Sur OPTIONS, court-circuit immédiat avec 204 No Content.
 */
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = new HashSet<>(Arrays.asList(
        "/api/register",      "/api/login",      "/api/refresh",
        "/api/auth/register", "/api/auth/login", "/api/auth/refresh"
    ));

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request   = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // ---- En-têtes CORS sur toutes les réponses ----
        String origin = request.getHeader("Origin");
        if (origin != null && !origin.isEmpty()) {
            // Echo the Origin (works for http://localhost, http://localhost:80, file://, etc.)
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Vary", "Origin");
        } else {
            response.setHeader("Access-Control-Allow-Origin", "*");
        }
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, X-Requested-With");
        response.setHeader("Access-Control-Max-Age", "3600");

        // ---- Preflight CORS : court-circuit ----
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        // ---- Routes publiques ----
        String path = request.getRequestURI();
        String ctx  = request.getContextPath();
        String relative = (ctx != null && path.startsWith(ctx)) ? path.substring(ctx.length()) : path;
        if (PUBLIC_PATHS.contains(relative)) {
            chain.doFilter(req, res);
            return;
        }

        // ---- Authentification JWT ----
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"Token manquant\"}");
            return;
        }

        String token = auth.substring(7);
        Claims claims = JwtUtil.validate(token);
        if (claims == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"Token invalide ou expiré\"}");
            return;
        }

        request.setAttribute("userId", Integer.parseInt(claims.getSubject()));
        request.setAttribute("role",   claims.get("role"));
        request.setAttribute("email",  claims.get("email"));
        chain.doFilter(req, res);
    }
}
