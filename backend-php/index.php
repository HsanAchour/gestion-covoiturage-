<?php
/**
 * Routeur frontal du backend PHP IHEC Ride.
 *
 *   1. CORS + preflight OPTIONS
 *   2. Détermine le chemin relatif à backend-php/
 *   3. Match route -> contrôleur::méthode
 *   4. Auth JWT pour les routes protégées
 *
 * Toutes les réponses sont en application/json.
 */

declare(strict_types=1);

require_once __DIR__ . '/filters/AuthFilter.php';

// ---------- En-têtes globaux ----------
header('Content-Type: application/json; charset=UTF-8');
AuthFilter::cors();
AuthFilter::handlePreflight();

// ---------- Helpers JSON globaux ----------
function json_send(array $payload): void {
    echo json_encode($payload, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
}

function json_fail(int $status, string $message): void {
    http_response_code($status);
    json_send(['success' => false, 'message' => $message]);
    exit;
}

// ---------- Détermination du chemin ----------
$method = strtoupper($_SERVER['REQUEST_METHOD'] ?? 'GET');
$uri    = parse_url($_SERVER['REQUEST_URI'] ?? '/', PHP_URL_PATH) ?: '/';

// On retire le préfixe d'installation (ex. /ihec-ride/backend-php) pour
// ne garder que /api/login, /api/trajets, etc.
$basePath = rtrim(dirname($_SERVER['SCRIPT_NAME']), '/\\');
if ($basePath !== '' && strpos($uri, $basePath) === 0) {
    $uri = substr($uri, strlen($basePath));
}
if ($uri === '' || $uri === false) $uri = '/';

// ---------- Table de routes ----------
//   [METHOD, PATH] => [Controller, method, requiresAuth]
$routes = [
    'POST /api/register'              => ['AuthController',        'register',     false],
    'POST /api/login'                 => ['AuthController',        'login',        false],
    'POST /api/refresh'               => ['AuthController',        'refresh',      false],
    'POST /api/auth/register'         => ['AuthController',        'register',     false],
    'POST /api/auth/login'            => ['AuthController',        'login',        false],
    'POST /api/auth/refresh'          => ['AuthController',        'refresh',      false],

    'GET  /api/trajets'               => ['TrajetController',      'findAll',      true],
    'POST /api/trajets'               => ['TrajetController',      'create',       true],
    'GET  /api/trajets/search'        => ['TrajetController',      'search',       true],
    'GET  /api/trajets/mine'          => ['TrajetController',      'mine',         true],

    'GET  /api/reservations'          => ['ReservationController', 'mine',         true],
    'POST /api/reservations'          => ['ReservationController', 'create',       true],
    'PATCH /api/reservations'         => ['ReservationController', 'updateStatut', true],
    'GET  /api/reservations/trajet'   => ['ReservationController', 'byTrajet',     true],

    'GET  /api/messages/conversation' => ['MessageController',     'conversation', true],
    'POST /api/messages'              => ['MessageController',     'send',         true],

    'GET  /api/evaluations'           => ['EvaluationController',  'byEvalue',     true],
    'POST /api/evaluations'           => ['EvaluationController',  'create',       true],

    'POST /api/sos'                   => ['SosController',         'send',         true],

    'GET  /api/reclamations'          => ['ReclamationController', 'mine',         true],
    'POST /api/reclamations'          => ['ReclamationController', 'create',       true],

    'GET  /api/admin/stats'           => ['AdminController',       'stats',        true],
    'GET  /api/admin/users'           => ['AdminController',       'users',        true],
    'GET  /api/admin/trajets'         => ['AdminController',       'trajets',      true],
    'GET  /api/admin/reclamations'    => ['AdminController',       'reclamations', true],
    'POST /api/admin/ban'             => ['AdminController',       'ban',          true],
    'POST /api/admin/delete'          => ['AdminController',       'deleteUser',   true],
];

// Normalisation : on accepte "GET  /api/x" (espaces multiples) dans la table.
$lookup = [];
foreach ($routes as $key => $route) {
    $parts = preg_split('/\s+/', trim($key), 2);
    $lookup[strtoupper($parts[0]) . ' ' . $parts[1]] = $route;
}
$routeKey = $method . ' ' . $uri;

if (!isset($lookup[$routeKey])) {
    json_fail(404, "Endpoint inconnu : $method $uri");
}

[$controllerName, $action, $requiresAuth] = $lookup[$routeKey];

// ---------- Authentification ----------
$ctx = ['userId' => 0, 'role' => '', 'email' => ''];
if ($requiresAuth) {
    $ctx = AuthFilter::requireAuth();
}

// ---------- Dispatch ----------
require_once __DIR__ . "/controllers/$controllerName.php";
try {
    $controller = new $controllerName();
    $controller->$action($ctx);
} catch (\Throwable $e) {
    error_log("Router error on $routeKey : " . $e->getMessage());
    if (!headers_sent()) http_response_code(500);
    json_send(['success' => false, 'message' => 'Erreur serveur : ' . $e->getMessage()]);
}
