<?php
/**
 * DAO : accès à la table trajets.
 * Équivalent PHP de com.ihecride.dao.TrajetDAO.
 */

require_once __DIR__ . '/../utils/DBConnection.php';
require_once __DIR__ . '/../models/Trajet.php';

class TrajetDAO {

    public function insert(Trajet $t): int {
        $sql = "INSERT INTO trajets(conducteur_id, depart, destination, date_heure, prix, places_total, places_dispo, description) VALUES(?,?,?,?,?,?,?,?)";
        $pdo = DBConnection::getConnection();
        try {
            $stmt = $pdo->prepare($sql);
            $ok = $stmt->execute([
                $t->conducteurId, $t->depart, $t->destination, $t->dateHeure,
                $t->prix, $t->placesTotal, $t->placesTotal, $t->description
            ]);
            if ($ok && $stmt->rowCount() > 0) return (int) $pdo->lastInsertId();
        } catch (PDOException $e) {
            error_log('TrajetDAO::insert ' . $e->getMessage());
        }
        return -1;
    }

    public function findById(int $id): ?Trajet {
        $sql = "SELECT t.*, CONCAT(u.prenom,' ',u.nom) AS conducteur_nom
                FROM trajets t JOIN users u ON t.conducteur_id = u.id WHERE t.id = ?";
        $stmt = DBConnection::getConnection()->prepare($sql);
        $stmt->execute([$id]);
        $row = $stmt->fetch();
        return $row ? $this->map($row) : null;
    }

    /** @return Trajet[] */
    public function search(?string $depart, ?string $destination, ?string $date): array {
        $sql = "SELECT t.*, CONCAT(u.prenom,' ',u.nom) AS conducteur_nom
                FROM trajets t JOIN users u ON t.conducteur_id = u.id
                WHERE t.statut='OUVERT' AND t.places_dispo > 0";
        $params = [];
        if (!empty($depart)) {
            $sql .= " AND t.depart LIKE ?";
            $params[] = '%' . $depart . '%';
        }
        if (!empty($destination)) {
            $sql .= " AND t.destination LIKE ?";
            $params[] = '%' . $destination . '%';
        }
        if (!empty($date)) {
            $sql .= " AND DATE(t.date_heure) = ?";
            $params[] = $date;
        }
        $sql .= " ORDER BY t.date_heure ASC";

        $stmt = DBConnection::getConnection()->prepare($sql);
        $stmt->execute($params);
        $list = [];
        foreach ($stmt->fetchAll() as $row) $list[] = $this->map($row);
        return $list;
    }

    /** @return Trajet[] */
    public function findByConducteur(int $conducteurId): array {
        $sql = "SELECT t.*, CONCAT(u.prenom,' ',u.nom) AS conducteur_nom
                FROM trajets t JOIN users u ON t.conducteur_id=u.id
                WHERE conducteur_id = ? ORDER BY date_heure DESC";
        $stmt = DBConnection::getConnection()->prepare($sql);
        $stmt->execute([$conducteurId]);
        $list = [];
        foreach ($stmt->fetchAll() as $row) $list[] = $this->map($row);
        return $list;
    }

    /** @return Trajet[] */
    public function findAll(): array {
        $sql = "SELECT t.*, CONCAT(u.prenom,' ',u.nom) AS conducteur_nom
                FROM trajets t JOIN users u ON t.conducteur_id=u.id ORDER BY date_heure DESC";
        $stmt = DBConnection::getConnection()->query($sql);
        $list = [];
        foreach ($stmt->fetchAll() as $row) $list[] = $this->map($row);
        return $list;
    }

    public function updateStatut(int $id, string $statut): bool {
        $stmt = DBConnection::getConnection()->prepare("UPDATE trajets SET statut = ? WHERE id = ?");
        return $stmt->execute([$statut, $id]) && $stmt->rowCount() > 0;
    }

    public function decrementPlaces(int $trajetId): bool {
        $stmt = DBConnection::getConnection()->prepare(
            "UPDATE trajets SET places_dispo = places_dispo - 1 WHERE id = ? AND places_dispo > 0"
        );
        return $stmt->execute([$trajetId]) && $stmt->rowCount() > 0;
    }

    public function delete(int $id): bool {
        $stmt = DBConnection::getConnection()->prepare("DELETE FROM trajets WHERE id = ?");
        return $stmt->execute([$id]) && $stmt->rowCount() > 0;
    }

    private function map(array $rs): Trajet {
        $t = new Trajet();
        $t->id             = isset($rs['id']) ? (int) $rs['id'] : null;
        $t->conducteurId   = isset($rs['conducteur_id']) ? (int) $rs['conducteur_id'] : null;
        $t->conducteurNom  = $rs['conducteur_nom'] ?? null;
        $t->depart         = $rs['depart'] ?? null;
        $t->destination    = $rs['destination'] ?? null;
        $t->dateHeure      = $rs['date_heure'] ?? null;
        $t->prix           = isset($rs['prix']) ? (float) $rs['prix'] : null;
        $t->placesTotal    = isset($rs['places_total']) ? (int) $rs['places_total'] : null;
        $t->placesDispo    = isset($rs['places_dispo']) ? (int) $rs['places_dispo'] : null;
        $t->description    = $rs['description'] ?? null;
        $t->statut         = $rs['statut'] ?? null;
        $t->createdAt      = $rs['created_at'] ?? null;
        return $t;
    }
}
