<?php
/**
 * Contrôleur SOS : /api/sos.
 * Équivalent PHP de com.ihecride.controllers.SosController.
 */

require_once __DIR__ . '/../utils/DBConnection.php';

class SosController {

    public function send(array $ctx): void {
        try {
            $lat = isset($_POST['latitude'])  ? (float) $_POST['latitude']  : null;
            $lon = isset($_POST['longitude']) ? (float) $_POST['longitude'] : null;
            if ($lat === null || $lon === null) {
                throw new InvalidArgumentException('Coordonnées GPS manquantes');
            }
            $stmt = DBConnection::getConnection()->prepare(
                "INSERT INTO sos_alerts(user_id, latitude, longitude) VALUES(?,?,?)"
            );
            $stmt->execute([$ctx['userId'], $lat, $lon]);
            json_send(['success' => true, 'message' => "Alerte SOS envoyée à l'administration"]);
        } catch (\Throwable $e) {
            error_log('SOS error: ' . $e->getMessage());
            http_response_code(500);
            json_send(['success' => false, 'message' => 'Erreur envoi SOS']);
        }
    }
}
