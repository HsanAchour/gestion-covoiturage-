package com.ihecride.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Hachage et vérification des mots de passe avec BCrypt (facteur 12).
 */
public class PasswordUtil {

    private static final int SALT_ROUNDS = 12;

    public static String hash(String clearPassword) {
        return BCrypt.hashpw(clearPassword, BCrypt.gensalt(SALT_ROUNDS));
    }

    public static boolean verify(String clearPassword, String hash) {
        if (clearPassword == null || hash == null) return false;
        try {
            return BCrypt.checkpw(clearPassword, hash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Vérifie la robustesse :
     * min. 8 caractères, 1 majuscule, 1 chiffre, 1 caractère spécial.
     */
    public static boolean isStrong(String pwd) {
        if (pwd == null || pwd.length() < 8) return false;
        return pwd.matches(".*[A-Z].*") &&
               pwd.matches(".*\\d.*") &&
               pwd.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }
}
