<?php
/**
 * Service d'authentification : validation email IHEC, BCrypt, génération JWT.
 * Équivalent PHP de com.ihecride.services.AuthService.
 */

require_once __DIR__ . '/../dao/UserDAO.php';
require_once __DIR__ . '/../utils/PasswordUtil.php';
require_once __DIR__ . '/../utils/JwtUtil.php';
require_once __DIR__ . '/../models/User.php';

class AuthService {

    private const EMAIL_IHEC = '/^[A-Za-z0-9._%+-]+@ihec\.ucar\.tn$/';

    private UserDAO $userDAO;

    public function __construct() {
        $this->userDAO = new UserDAO();
    }

    /**
     * Inscription. Retourne ['success', 'message', 'token', 'refresh', 'user'].
     */
    public function register(?string $nom, ?string $prenom, ?string $email, ?string $telephone,
                             ?string $motDePasse, ?string $role): array {
        if ($email === null || !preg_match(self::EMAIL_IHEC, $email)) {
            return ['success' => false, 'message' => 'Email universitaire @ihec.ucar.tn requis'];
        }
        if (!PasswordUtil::isStrong($motDePasse)) {
            return ['success' => false, 'message' => 'Mot de passe : min. 8 caractères, 1 majuscule, 1 chiffre, 1 caractère spécial'];
        }
        if ($this->userDAO->findByEmail($email) !== null) {
            return ['success' => false, 'message' => 'Email déjà enregistré'];
        }

        $hash = PasswordUtil::hash($motDePasse);
        $u = new User($nom, $prenom, $email, $telephone, $hash, $role);
        $id = $this->userDAO->insert($u);
        if ($id < 0) {
            return ['success' => false, 'message' => "Erreur lors de l'inscription"];
        }
        $u->id = $id;

        return [
            'success' => true,
            'message' => 'Inscription réussie',
            'token'   => JwtUtil::generateAccessToken($id, $email, $role),
            'refresh' => JwtUtil::generateRefreshToken($id, $email, $role),
            'user'    => $u,
        ];
    }

    public function login(?string $email, ?string $motDePasse): array {
        $u = $this->userDAO->findByEmail($email);
        if ($u === null || !PasswordUtil::verify($motDePasse, $u->motDePasseHash)) {
            return ['success' => false, 'message' => 'Identifiants invalides'];
        }
        if ($u->statut === 'BANNI') {
            return ['success' => false, 'message' => 'Compte banni, contactez l\'administration'];
        }
        return [
            'success' => true,
            'message' => 'Connexion réussie',
            'token'   => JwtUtil::generateAccessToken($u->id, $u->email, $u->role),
            'refresh' => JwtUtil::generateRefreshToken($u->id, $u->email, $u->role),
            'user'    => $u,
        ];
    }
}
