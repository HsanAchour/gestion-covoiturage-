<?php
/**
 * DAO : accès à la table reclamations.
 * Équivalent PHP de com.ihecride.dao.ReclamationDAO.
 */

require_once __DIR__ . '/../utils/DBConnection.php';
require_once __DIR__ . '/../models/Reclamation.php';

class ReclamationDAO {

    public function insert(Reclamation $r): int {
        $pdo = DBConnection::getConnection();
        try {
            $stmt = $pdo->prepare(
                "INSERT INTO reclamations(declarant_id, cible_id, type, description) VALUES(?,?,?,?)"
            );
            $ok = $stmt->execute([
                $r->declarantId,
                $r->cibleId,
                $r->type,
                $r->description
            ]);
            if ($ok && $stmt->rowCount() > 0) return (int) $pdo->lastInsertId();
        } catch (PDOException $e) {
            error_log('ReclamationDAO::insert ' . $e->getMessage());
        }
        return -1;
    }

    /** @return Reclamation[] */
    public function findAll(): array {
        $sql = "SELECT r.*, CONCAT(u.prenom,' ',u.nom) AS declarant_nom
                FROM reclamations r JOIN users u ON r.declarant_id=u.id
                ORDER BY date_soumission DESC";
        $stmt = DBConnection::getConnection()->query($sql);
        $list = [];
        foreach ($stmt->fetchAll() as $row) $list[] = $this->map($row);
        return $list;
    }

    /** @return Reclamation[] */
    public function findByDeclarant(int $declarantId): array {
        $sql = "SELECT r.*, CONCAT(u.prenom,' ',u.nom) AS declarant_nom
                FROM reclamations r JOIN users u ON r.declarant_id=u.id
                WHERE declarant_id = ? ORDER BY date_soumission DESC";
        $stmt = DBConnection::getConnection()->prepare($sql);
        $stmt->execute([$declarantId]);
        $list = [];
        foreach ($stmt->fetchAll() as $row) $list[] = $this->map($row);
        return $list;
    }

    public function updateStatut(int $id, string $statut): bool {
        $stmt = DBConnection::getConnection()->prepare("UPDATE reclamations SET statut = ? WHERE id = ?");
        return $stmt->execute([$statut, $id]) && $stmt->rowCount() > 0;
    }

    private function map(array $rs): Reclamation {
        $r = new Reclamation();
        $r->id              = isset($rs['id']) ? (int) $rs['id'] : null;
        $r->declarantId     = isset($rs['declarant_id']) ? (int) $rs['declarant_id'] : null;
        $r->cibleId         = (isset($rs['cible_id']) && $rs['cible_id'] !== null) ? (int) $rs['cible_id'] : null;
        $r->declarantNom    = $rs['declarant_nom'] ?? null;
        $r->type            = $rs['type'] ?? null;
        $r->description     = $rs['description'] ?? null;
        $r->statut          = $rs['statut'] ?? null;
        $r->dateSoumission  = $rs['date_soumission'] ?? null;
        return $r;
    }
}
