<?php
/**
 * Contrôleur admin : /api/admin/{stats,users,trajets,reclamations,ban,delete}.
 * Équivalent PHP de com.ihecride.controllers.AdminController.
 */

require_once __DIR__ . '/../dao/UserDAO.php';
require_once __DIR__ . '/../dao/TrajetDAO.php';
require_once __DIR__ . '/../dao/ReclamationDAO.php';
require_once __DIR__ . '/../utils/DBConnection.php';
require_once __DIR__ . '/../filters/AuthFilter.php';

class AdminController {

    private UserDAO $userDAO;
    private TrajetDAO $trajetDAO;
    private ReclamationDAO $reclamationDAO;

    public function __construct() {
        $this->userDAO        = new UserDAO();
        $this->trajetDAO      = new TrajetDAO();
        $this->reclamationDAO = new ReclamationDAO();
    }

    public function stats(array $ctx): void {
        AuthFilter::requireRole($ctx, 'ADMIN');
        try {
            $sql = "SELECT
                (SELECT COUNT(*) FROM users) AS users_total,
                (SELECT COUNT(*) FROM users WHERE role='PASSAGER') AS passagers,
                (SELECT COUNT(*) FROM users WHERE role='CONDUCTEUR') AS conducteurs,
                (SELECT COUNT(*) FROM trajets) AS trajets_total,
                (SELECT COUNT(*) FROM trajets WHERE statut='TERMINE') AS trajets_termines,
                (SELECT COUNT(*) FROM reservations) AS reservations_total,
                (SELECT COUNT(*) FROM reclamations WHERE statut='OUVERT') AS reclamations_ouvertes";
            $row = DBConnection::getConnection()->query($sql)->fetch();
            json_send([
                'success'              => true,
                'usersTotal'           => (int) ($row['users_total'] ?? 0),
                'passagers'            => (int) ($row['passagers'] ?? 0),
                'conducteurs'          => (int) ($row['conducteurs'] ?? 0),
                'trajetsTotal'         => (int) ($row['trajets_total'] ?? 0),
                'trajetsTermines'      => (int) ($row['trajets_termines'] ?? 0),
                'reservationsTotal'    => (int) ($row['reservations_total'] ?? 0),
                'reclamationsOuvertes' => (int) ($row['reclamations_ouvertes'] ?? 0),
            ]);
        } catch (\Throwable $e) {
            http_response_code(500);
            json_send(['success' => false, 'message' => $e->getMessage()]);
        }
    }

    public function users(array $ctx): void {
        AuthFilter::requireRole($ctx, 'ADMIN');
        json_send(['success' => true, 'data' => $this->userDAO->findAll()]);
    }

    public function trajets(array $ctx): void {
        AuthFilter::requireRole($ctx, 'ADMIN');
        json_send(['success' => true, 'data' => $this->trajetDAO->findAll()]);
    }

    public function reclamations(array $ctx): void {
        AuthFilter::requireRole($ctx, 'ADMIN');
        json_send(['success' => true, 'data' => $this->reclamationDAO->findAll()]);
    }

    public function ban(array $ctx): void {
        AuthFilter::requireRole($ctx, 'ADMIN');
        $id = (int) ($_POST['id'] ?? 0);
        $ok = $this->userDAO->updateStatut($id, 'BANNI');
        json_send(['success' => $ok]);
    }

    public function deleteUser(array $ctx): void {
        AuthFilter::requireRole($ctx, 'ADMIN');
        $id = (int) ($_POST['id'] ?? 0);
        $ok = $this->userDAO->delete($id);
        json_send(['success' => $ok]);
    }
}
