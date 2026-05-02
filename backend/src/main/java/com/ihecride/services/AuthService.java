package com.ihecride.services;

import com.ihecride.dao.UserDAO;
import com.ihecride.models.User;
import com.ihecride.utils.PasswordUtil;
import com.ihecride.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Service d'authentification : validation email IHEC, hachage BCrypt, génération JWT.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private static final Pattern EMAIL_IHEC = Pattern.compile("^[A-Za-z0-9._%+-]+@ihec\\.ucar\\.tn$");

    /**
     * Inscription d'un nouvel utilisateur.
     * @return Map contenant "success", "message", "token", "refresh", "user"
     */
    public Map<String,Object> register(String nom, String prenom, String email, String telephone,
                                       String motDePasse, String role) {
        Map<String,Object> result = new HashMap<>();

        // Validation email IHEC
        if (email == null || !EMAIL_IHEC.matcher(email).matches()) {
            result.put("success", false);
            result.put("message", "Email universitaire @ihec.ucar.tn requis");
            return result;
        }
        // Validation mot de passe fort
        if (!PasswordUtil.isStrong(motDePasse)) {
            result.put("success", false);
            result.put("message", "Mot de passe : min. 8 caractères, 1 majuscule, 1 chiffre, 1 caractère spécial");
            return result;
        }
        // Email déjà utilisé ?
        if (userDAO.findByEmail(email) != null) {
            result.put("success", false);
            result.put("message", "Email déjà enregistré");
            return result;
        }

        String hash = PasswordUtil.hash(motDePasse);
        User u = new User(nom, prenom, email, telephone, hash, role);
        int id = userDAO.insert(u);
        if (id < 0) {
            result.put("success", false);
            result.put("message", "Erreur lors de l'inscription");
            return result;
        }

        u.setId(id);
        result.put("success", true);
        result.put("message", "Inscription réussie");
        result.put("token", JwtUtil.generateAccessToken(id, email, role));
        result.put("refresh", JwtUtil.generateRefreshToken(id, email, role));
        result.put("user", u);
        return result;
    }

    public Map<String,Object> login(String email, String motDePasse) {
        Map<String,Object> result = new HashMap<>();
        User u = userDAO.findByEmail(email);
        if (u == null || !PasswordUtil.verify(motDePasse, u.getMotDePasseHash())) {
            result.put("success", false);
            result.put("message", "Identifiants invalides");
            return result;
        }
        if ("BANNI".equals(u.getStatut())) {
            result.put("success", false);
            result.put("message", "Compte banni — contactez l'administration");
            return result;
        }

        result.put("success", true);
        result.put("message", "Connexion réussie");
        result.put("token", JwtUtil.generateAccessToken(u.getId(), u.getEmail(), u.getRole()));
        result.put("refresh", JwtUtil.generateRefreshToken(u.getId(), u.getEmail(), u.getRole()));
        result.put("user", u);
        return result;
    }
}
