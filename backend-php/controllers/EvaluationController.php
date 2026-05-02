<?php
/**
 * Contrôleur évaluations : /api/evaluations, /api/evaluations/conducteur.
 * Équivalent PHP de com.ihecride.controllers.EvaluationController.
 */

require_once __DIR__ . '/../dao/EvaluationDAO.php';
require_once __DIR__ . '/../dao/UserDAO.php';
require_once __DIR__ . '/../models/Evaluation.php';

class EvaluationController {

    private EvaluationDAO $dao;
    private UserDAO $userDAO;

    public function __construct() {
        $this->dao     = new EvaluationDAO();
        $this->userDAO = new UserDAO();
    }

    public function byEvalue(array $ctx): void {
        $evalueId = (int) ($_GET['id'] ?? 0);
        json_send(['success' => true, 'data' => $this->dao->findByEvalue($evalueId)]);
    }

    public function create(array $ctx): void {
        $e = new Evaluation();
        $e->evaluateurId = $ctx['userId'];
        $e->evalueId     = (int) ($_POST['evalueId'] ?? 0);
        $e->trajetId     = (int) ($_POST['trajetId'] ?? 0);
        $e->note         = (int) ($_POST['note']     ?? 0);
        $e->commentaire  = $_POST['commentaire'] ?? '';
        $id = $this->dao->insert($e);
        if ($id > 0) $this->userDAO->updateNoteMoyenne($e->evalueId);
        json_send(['success' => $id > 0]);
    }
}
