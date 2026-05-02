<?php
/**
 * DAO : accès à la table users.
 * Équivalent PHP de com.ihecride.dao.UserDAO.
 */

require_once __DIR__ . '/../utils/DBConnection.php';
require_once __DIR__ . '/../models/User.php';

class UserDAO {

    public function findByEmail(?string $email): ?User {
        if ($email === null) return null;
        $stmt = DBConnection::getConnection()->prepare("SELECT * FROM users WHERE email = ?");
        $stmt->execute([$email]);
        $row = $stmt->fetch();
        return $row ? $this->map($row) : null;
    }

    public function findById(int $id): ?User {
        $stmt = DBConnection::getConnection()->prepare("SELECT * FROM users WHERE id = ?");
        $stmt->execute([$id]);
        $row = $stmt->fetch();
        return $row ? $this->map($row) : null;
    }

    public function insert(User $u): int {
        $sql = "INSERT INTO users(nom, prenom, email, telephone, mot_de_passe_hash, role) VALUES(?,?,?,?,?,?)";
        $pdo = DBConnection::getConnection();
        try {
            $stmt = $pdo->prepare($sql);
            $ok = $stmt->execute([
                $u->nom, $u->prenom, $u->email, $u->telephone,
                $u->motDePasseHash, $u->role
            ]);
            if ($ok && $stmt->rowCount() > 0) {
                return (int) $pdo->lastInsertId();
            }
        } catch (PDOException $e) {
            error_log('UserDAO::insert ' . $e->getMessage());
        }
        return -1;
    }

    /** @return User[] */
    public function findByRole(string $role): array {
        $stmt = DBConnection::getConnection()->prepare(
            "SELECT * FROM users WHERE role = ? ORDER BY created_at DESC"
        );
        $stmt->execute([$role]);
        $list = [];
        foreach ($stmt->fetchAll() as $row) $list[] = $this->map($row);
        return $list;
    }

    /** @return User[] */
    public function findAll(): array {
        $stmt = DBConnection::getConnection()->query("SELECT * FROM users ORDER BY created_at DESC");
        $list = [];
        foreach ($stmt->fetchAll() as $row) $list[] = $this->map($row);
        return $list;
    }

    public function updateStatut(int $id, string $statut): bool {
        $stmt = DBConnection::getConnection()->prepare("UPDATE users SET statut = ? WHERE id = ?");
        return $stmt->execute([$statut, $id]) && $stmt->rowCount() > 0;
    }

    public function update(User $u): bool {
        $stmt = DBConnection::getConnection()->prepare(
            "UPDATE users SET nom=?, prenom=?, telephone=?, photo=?, contact_confiance=? WHERE id=?"
        );
        return $stmt->execute([
            $u->nom, $u->prenom, $u->telephone, $u->photo, $u->contactConfiance, $u->id
        ]) && $stmt->rowCount() > 0;
    }

    public function delete(int $id): bool {
        $stmt = DBConnection::getConnection()->prepare("DELETE FROM users WHERE id = ?");
        return $stmt->execute([$id]) && $stmt->rowCount() > 0;
    }

    public function updateNoteMoyenne(int $userId): bool {
        $stmt = DBConnection::getConnection()->prepare(
            "UPDATE users SET note_moyenne = (SELECT AVG(note) FROM evaluations WHERE evalue_id = ?) WHERE id = ?"
        );
        return $stmt->execute([$userId, $userId]) && $stmt->rowCount() > 0;
    }

    private function map(array $rs): User {
        $u = new User();
        $u->id                = isset($rs['id']) ? (int) $rs['id'] : null;
        $u->nom               = $rs['nom'] ?? null;
        $u->prenom            = $rs['prenom'] ?? null;
        $u->email             = $rs['email'] ?? null;
        $u->telephone         = $rs['telephone'] ?? null;
        $u->motDePasseHash    = $rs['mot_de_passe_hash'] ?? null;
        $u->photo             = $rs['photo'] ?? null;
        $u->role              = $rs['role'] ?? null;
        $u->noteMoyenne       = isset($rs['note_moyenne']) ? (float) $rs['note_moyenne'] : 0.0;
        $u->statut            = $rs['statut'] ?? null;
        $u->contactConfiance  = $rs['contact_confiance'] ?? null;
        $u->createdAt         = $rs['created_at'] ?? null;
        return $u;
    }
}
