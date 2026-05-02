<?php
/**
 * Contrôleur réservations : /api/reservations, /api/reservations/trajet.
 * Équivalent PHP de com.ihecride.controllers.ReservationController.
 */

require_once __DIR__ . '/../dao/ReservationDAO.php';
require_once __DIR__ . '/../dao/TrajetDAO.php';
require_once __DIR__ . '/../models/Reservation.php';

class ReservationController {

    private ReservationDAO $dao;
    private TrajetDAO $trajetDAO;

    public function __construct() {
        $this->dao       = new ReservationDAO();
        $this->trajetDAO = new TrajetDAO();
    }

    public function mine(array $ctx): void {
        json_send(['success' => true, 'data' => $this->dao->findByPassager($ctx['userId'])]);
    }

    public function byTrajet(array $ctx): void {
        $trajetId = (int) ($_GET['trajetId'] ?? 0);
        json_send(['success' => true, 'data' => $this->dao->findByTrajet($trajetId)]);
    }

    public function create(array $ctx): void {
        $r = new Reservation();
        $r->passagerId = $ctx['userId'];
        $r->trajetId   = (int) ($_POST['trajetId'] ?? 0);
        $id = $this->dao->insert($r);
        if ($id > 0) {
            json_send(['success' => true, 'id' => $id, 'message' => 'Demande de réservation envoyée']);
        } else {
            http_response_code(409);
            json_send(['success' => false, 'message' => 'Vous avez déjà réservé ce trajet']);
        }
    }

    public function updateStatut(array $ctx): void {
        parse_str(file_get_contents('php://input'), $body);
        $id     = (int) ($body['id']     ?? 0);
        $statut = (string) ($body['statut'] ?? '');
        $ok = $this->dao->updateStatut($id, $statut);
        if ($ok && $statut === 'ACCEPTE') {
            $trajetId = (int) ($body['trajetId'] ?? 0);
            $this->trajetDAO->decrementPlaces($trajetId);
        }
        json_send(['success' => $ok]);
    }
}
