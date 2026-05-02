<?php
/**
 * Contrôleur réclamations : /api/reclamations.
 * Équivalent PHP de com.ihecride.controllers.ReclamationController.
 */

require_once __DIR__ . '/../dao/ReclamationDAO.php';
require_once __DIR__ . '/../models/Reclamation.php';

class ReclamationController {

    private ReclamationDAO $dao;

    public function __construct() {
        $this->dao = new ReclamationDAO();
    }

    public function mine(array $ctx): void {
        json_send(['success' => true, 'data' => $this->dao->findByDeclarant($ctx['userId'])]);
    }

    public function create(array $ctx): void {
        $r = new Reclamation();
        $r->declarantId = $ctx['userId'];
        $r->type        = $_POST['type']        ?? null;
        $r->description = $_POST['description'] ?? null;
        $id = $this->dao->insert($r);
        json_send([
            'success' => $id > 0,
            'id'      => $id,
            'message' => 'Votre réclamation a été enregistrée. Un agent vous répondra sous 24h.'
        ]);
    }
}
