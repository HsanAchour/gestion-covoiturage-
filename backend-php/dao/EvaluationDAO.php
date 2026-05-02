<?php
/**
 * DAO : accès à la table evaluations.
 * Équivalent PHP de com.ihecride.dao.EvaluationDAO.
 */

require_once __DIR__ . '/../utils/DBConnection.php';
require_once __DIR__ . '/../models/Evaluation.php';

class EvaluationDAO {

    public function insert(Evaluation $e): int {
        $pdo = DBConnection::getConnection();
        try {
            $stmt = $pdo->prepare(
                "INSERT INTO evaluations(trajet_id, evaluateur_id, evalue_id, note, commentaire) VALUES(?,?,?,?,?)"
            );
            $ok = $stmt->execute([$e->trajetId, $e->evaluateurId, $e->evalueId, $e->note, $e->commentaire]);
            if ($ok && $stmt->rowCount() > 0) return (int) $pdo->lastInsertId();
        } catch (PDOException $ex) {
            error_log('EvaluationDAO::insert ' . $ex->getMessage());
        }
        return -1;
    }

    /** @return Evaluation[] */
    public function findByEvalue(int $evalueId): array {
        $sql = "SELECT e.*, CONCAT(u.prenom,' ',u.nom) AS evaluateur_nom
                FROM evaluations e JOIN users u ON e.evaluateur_id = u.id
                WHERE e.evalue_id = ? ORDER BY e.date DESC";
        $stmt = DBConnection::getConnection()->prepare($sql);
        $stmt->execute([$evalueId]);
        $list = [];
        foreach ($stmt->fetchAll() as $row) $list[] = $this->map($row);
        return $list;
    }

    public function moyenne(int $userId): float {
        $stmt = DBConnection::getConnection()->prepare(
            "SELECT AVG(note) AS avg FROM evaluations WHERE evalue_id = ?"
        );
        $stmt->execute([$userId]);
        $row = $stmt->fetch();
        return $row && $row['avg'] !== null ? (float) $row['avg'] : 0.0;
    }

    private function map(array $rs): Evaluation {
        $e = new Evaluation();
        $e->id             = isset($rs['id']) ? (int) $rs['id'] : null;
        $e->trajetId       = isset($rs['trajet_id']) ? (int) $rs['trajet_id'] : null;
        $e->evaluateurId   = isset($rs['evaluateur_id']) ? (int) $rs['evaluateur_id'] : null;
        $e->evalueId       = isset($rs['evalue_id']) ? (int) $rs['evalue_id'] : null;
        $e->evaluateurNom  = $rs['evaluateur_nom'] ?? null;
        $e->note           = isset($rs['note']) ? (int) $rs['note'] : null;
        $e->commentaire    = $rs['commentaire'] ?? null;
        $e->date           = $rs['date'] ?? null;
        return $e;
    }
}
