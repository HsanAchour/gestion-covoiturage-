<?php
/**
 * Filtre CORS + authentification JWT.
 * Équivalent PHP de com.ihecride.filters.AuthFilter.
 *
 * Mode d'emploi :
 *   AuthFilter::cors();             // toujours, en tout début de requête
 *   AuthFilter::handlePreflight();  // court-circuit OPTIONS
 *   $ctx = AuthFilter::requireAuth();  // sur les routes protégées : retourne ['userId','role','email']
 */

require_once __DIR__ . '/../utils/JwtUtil.php';

class AuthFilter {

    /** Pose les en-têtes CORS sur la réponse. */
    public static function cors(): void {
        $origin = $_SERVER['HTTP_ORIGIN'] ?? '';
        if ($origin !== '') {
            header("Access-Control-Allow-Origin: $origin");
            header('Vary: Origin');
        } else {
            header('Access-Control-Allow-Origin: *');
        }
        header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS');
        header('Access-Control-Allow-Headers: Authorization, Content-Type, Accept, X-Requested-With');
        header('Access-Control-Max-Age: 3600');
    }

    /** Si la méthode est OPTIONS, répond 204 et termine. */
    public static function handlePreflight(): void {
        if (($_SERVER['REQUEST_METHOD'] ?? '') === 'OPTIONS') {
            http_response_code(204);
            exit;
        }
    }

    /**
     * Sur une route protégée : valide le JWT, renvoie le contexte utilisateur.
     * En cas d'échec : 401 JSON et exit.
     * @return array{userId:int, role:string, email:string}
     */
    public static function requireAuth(): array {
        $auth = self::extractAuthHeader();
        if ($auth === null || strpos($auth, 'Bearer ') !== 0) {
            self::deny('Token manquant');
        }
        $token  = substr($auth, 7);
        $claims = JwtUtil::validate($token);
        if ($claims === null) {
            self::deny('Token invalide ou expiré');
        }
        return [
            'userId' => (int) $claims['sub'],
            'role'   => (string) ($claims['role'] ?? ''),
            'email'  => (string) ($claims['email'] ?? ''),
        ];
    }

    /** Renvoie 403 si le rôle ne correspond pas. */
    public static function requireRole(array $ctx, string $expectedRole): void {
        if (($ctx['role'] ?? '') !== $expectedRole) {
            http_response_code(403);
            header('Content-Type: application/json; charset=UTF-8');
            echo json_encode(['success' => false, 'message' => 'Accès refusé']);
            exit;
        }
    }

    private static function extractAuthHeader(): ?string {
        if (isset($_SERVER['HTTP_AUTHORIZATION'])) return $_SERVER['HTTP_AUTHORIZATION'];
        if (function_exists('apache_request_headers')) {
            $h = apache_request_headers();
            foreach ($h as $k => $v) {
                if (strtolower($k) === 'authorization') return $v;
            }
        }
        return $_SERVER['REDIRECT_HTTP_AUTHORIZATION'] ?? null;
    }

    private static function deny(string $msg): void {
        http_response_code(401);
        header('Content-Type: application/json; charset=UTF-8');
        echo json_encode(['success' => false, 'message' => $msg]);
        exit;
    }
}
