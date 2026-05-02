<?php
/**
 * Hachage et vérification des mots de passe avec BCrypt (cost=12).
 * Équivalent PHP de com.ihecride.utils.PasswordUtil.
 */

class PasswordUtil {

    const COST = 12;

    public static function hash(string $clearPassword): string {
        return password_hash($clearPassword, PASSWORD_BCRYPT, ['cost' => self::COST]);
    }

    public static function verify(?string $clearPassword, ?string $hash): bool {
        if ($clearPassword === null || $hash === null) return false;
        try {
            return password_verify($clearPassword, $hash);
        } catch (\Throwable $e) {
            return false;
        }
    }

    /**
     * Robustesse : min. 8 caractères, 1 majuscule, 1 chiffre, 1 caractère spécial.
     */
    public static function isStrong(?string $pwd): bool {
        if ($pwd === null || strlen($pwd) < 8) return false;
        return preg_match('/[A-Z]/', $pwd)
            && preg_match('/\d/', $pwd)
            && preg_match('/[!@#$%^&*(),.?":{}|<>]/', $pwd);
    }
}
