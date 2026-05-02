<?php
/**
 * Contrôleur d'authentification.
 *   POST /api/register
 *   POST /api/login
 *   POST /api/refresh
 * (les chemins /api/auth/* sont conservés en alias par le routeur).
 *
 * Équivalent PHP de com.ihecride.controllers.AuthController.
 */

require_once __DIR__ . '/../services/AuthService.php';

class AuthController {

    private AuthService $authService;

    public function __construct() {
        $this->authService = new AuthService();
    }

    public function register(): void {
        $r = $this->authService->register(
            $_POST['nom']        ?? null,
            $_POST['prenom']     ?? null,
            $_POST['email']      ?? null,
            $_POST['telephone']  ?? null,
            $_POST['motDePasse'] ?? null,
            $_POST['role']       ?? null
        );
        if (!$r['success']) http_response_code(400);
        $this->writeAuthResult($r);
    }

    public function login(): void {
        $r = $this->authService->login(
            $_POST['email']      ?? null,
            $_POST['motDePasse'] ?? null
        );
        if (!$r['success']) http_response_code(401);
        $this->writeAuthResult($r);
    }

    public function refresh(): void {
        http_response_code(501);
        json_send(['success' => false, 'message' => 'Rafraîchissement non implémenté']);
    }

    private function writeAuthResult(array $r): void {
        $payload = [
            'success' => (bool) $r['success'],
            'message' => $r['message'] ?? '',
        ];
        if ($r['success']) {
            /** @var User $u */
            $u = $r['user'];
            $payload['token']   = $r['token']   ?? '';
            $payload['refresh'] = $r['refresh'] ?? '';
            $payload['userId']  = $u->id;
            $payload['role']    = $u->role;
            $payload['prenom']  = $u->prenom;
            $payload['nom']     = $u->nom;
            $payload['email']   = $u->email;
        }
        json_send($payload);
    }
}
