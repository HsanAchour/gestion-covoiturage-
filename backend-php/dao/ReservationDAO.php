<?php
/**
 * DAO : accès à la table reservations.
 * Équivalent PHP de com.ihecride.dao.ReservationDAO.
 */

require_once __DIR__ . '/../utils/DBConnection.php';
require_once __DIR__ . '/../models/Reservation.php';

class ReservationDAO {

    public function insert(Reservation $r): int {
        $pdo = DBConnection::getConnection();
        try {
            $stmt = $pdo->prepare("INSERT INTO reservations(trajet_id, passager_id) VALUES(?,?)");
            $ok = $stmt->execute([$r->trajetId, $r->passagerId]);
            if ($ok && $stmt->rowCount() > 0) return (int) $pdo->lastInsertId();
        } catch (PDOException $e) {
            error_log('ReservationDAO::insert ' . $e->getMessage());
        }
        return -1;
    }

    /** @return Reservation[] */
    public function findByPassager(int $passagerId): array {
        $stmt = DBConnection::getConnection()->prepare(
            "SELECT * FROM reservations WHERE passager_id = ? ORDER BY created_at DESC"
        );
        $stmt->execute([$passagerId]);
        $list = [];
        foreach ($stmt->fetchAll() as $row) $list[] = $this->map($row);
        return $list;
    }

    /** @return Reservation[] */
    public function findByTrajet(int $trajetId): array {
        $sql = "SELECT r.*, CONCAT(u.prenom,' ',u.nom) AS passager_nom
                FROM reservations r JOIN users u ON r.passager_id=u.id
                WHERE r.trajet_id = ? ORDER BY r.created_at DESC";
        $stmt = DBConnection::getConnection()->prepare($sql);
        $stmt->execute([$trajetId]);
        $list = [];
        foreach ($stmt->fetchAll() as $row) $list[] = $this->map($row);
        return $list;
    }

    public function updateStatut(int $id, string $statut): bool {
        $stmt = DBConnection::getConnection()->prepare("UPDATE reservations SET statut = ? WHERE id = ?");
        return $stmt->execute([$statut, $id]) && $stmt->rowCount() > 0;
    }

    public function delete(int $id): bool {
        $stmt = DBConnection::getConnection()->prepare("DELETE FROM reservations WHERE id = ?");
        return $stmt->execute([$id]) && $stmt->rowCount() > 0;
    }

    private function map(array $rs): Reservation {
        $r = new Reservation();
        $r->id           = isset($rs['id']) ? (int) $rs['id'] : null;
        $r->trajetId     = isset($rs['trajet_id']) ? (int) $rs['trajet_id'] : null;
        $r->passagerId   = isset($rs['passager_id']) ? (int) $rs['passager_id'] : null;
        $r->passagerNom  = $rs['passager_nom'] ?? null;
        $r->statut       = $rs['statut'] ?? null;
        $r->createdAt    = $rs['created_at'] ?? null;
        return $r;
    }
}
