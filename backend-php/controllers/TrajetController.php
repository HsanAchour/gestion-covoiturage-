<?php
/**
 * Contrôleur trajets : /api/trajets, /api/trajets/search, /api/trajets/mine.
 * Équivalent PHP de com.ihecride.controllers.TrajetController.
 */

require_once __DIR__ . '/../dao/TrajetDAO.php';
require_once __DIR__ . '/../models/Trajet.php';

class TrajetController {

    private TrajetDAO $dao;

    public function __construct() {
        $this->dao = new TrajetDAO();
    }

    public function findAll(array $ctx): void {
        json_send(['success' => true, 'data' => $this->dao->findAll()]);
    }

    public function search(array $ctx): void {
        $list = $this->dao->search(
            $_GET['depart'] ?? null,
            $_GET['dest']   ?? null,
            $_GET['date']   ?? null
        );
        json_send(['success' => true, 'data' => $list]);
    }

    public function mine(array $ctx): void {
        $list = $this->dao->findByConducteur($ctx['userId']);
        json_send(['success' => true, 'data' => $list]);
    }

    public function create(array $ctx): void {
        if ($ctx['role'] !== 'CONDUCTEUR') {
            http_response_code(403);
            json_send(['success' => false, 'message' => 'Réservé aux conducteurs']);
            return;
        }
        try {
            $t = new Trajet();
            $t->conducteurId = $ctx['userId'];
            $t->depart       = $_POST['depart']      ?? null;
            $t->destination  = $_POST['destination'] ?? null;
            $dh              = $_POST['dateHeure']   ?? '';
            $t->dateHeure    = str_replace('T', ' ', $dh) . (strlen($dh) === 16 ? ':00' : '');
            $t->prix         = isset($_POST['prix'])   ? (float) $_POST['prix']   : null;
            $t->placesTotal  = isset($_POST['places']) ? (int)   $_POST['places'] : null;
            $t->description  = $_POST['description'] ?? null;

            $id = $this->dao->insert($t);
            if ($id > 0) {
                json_send(['success' => true, 'id' => $id, 'message' => 'Trajet créé avec succès']);
            } else {
                http_response_code(500);
                json_send(['success' => false, 'message' => 'Erreur de création']);
            }
        } catch (\Throwable $e) {
            http_response_code(400);
            json_send(['success' => false, 'message' => 'Données invalides : ' . $e->getMessage()]);
        }
    }
}
