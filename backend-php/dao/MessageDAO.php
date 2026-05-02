<?php
/**
 * DAO : accès à la table messages.
 * Équivalent PHP de com.ihecride.dao.MessageDAO.
 */

require_once __DIR__ . '/../utils/DBConnection.php';
require_once __DIR__ . '/../models/Message.php';

class MessageDAO {

    public function insert(Message $m): int {
        $pdo = DBConnection::getConnection();
        try {
            $stmt = $pdo->prepare(
                "INSERT INTO messages(expediteur_id, destinataire_id, contenu) VALUES(?,?,?)"
            );
            $ok = $stmt->execute([$m->expediteurId, $m->destinataireId, $m->contenu]);
            if ($ok && $stmt->rowCount() > 0) return (int) $pdo->lastInsertId();
        } catch (PDOException $e) {
            error_log('MessageDAO::insert ' . $e->getMessage());
        }
        return -1;
    }

    /** @return Message[] */
    public function findConversation(int $userA, int $userB): array {
        $sql = "SELECT m.*, CONCAT(u.prenom,' ',u.nom) AS expediteur_nom
                FROM messages m JOIN users u ON m.expediteur_id = u.id
                WHERE (expediteur_id = ? AND destinataire_id = ?)
                   OR (expediteur_id = ? AND destinataire_id = ?)
                ORDER BY date_envoi ASC";
        $stmt = DBConnection::getConnection()->prepare($sql);
        $stmt->execute([$userA, $userB, $userB, $userA]);
        $list = [];
        foreach ($stmt->fetchAll() as $row) $list[] = $this->map($row);
        return $list;
    }

    public function marquerLu(int $destinataireId, int $expediteurId): bool {
        $stmt = DBConnection::getConnection()->prepare(
            "UPDATE messages SET lu = TRUE WHERE destinataire_id = ? AND expediteur_id = ?"
        );
        return $stmt->execute([$destinataireId, $expediteurId]);
    }

    private function map(array $rs): Message {
        $m = new Message();
        $m->id              = isset($rs['id']) ? (int) $rs['id'] : null;
        $m->expediteurId    = isset($rs['expediteur_id']) ? (int) $rs['expediteur_id'] : null;
        $m->destinataireId  = isset($rs['destinataire_id']) ? (int) $rs['destinataire_id'] : null;
        $m->expediteurNom   = $rs['expediteur_nom'] ?? null;
        $m->contenu         = $rs['contenu'] ?? null;
        $m->dateEnvoi       = $rs['date_envoi'] ?? null;
        $m->lu              = !empty($rs['lu']);
        return $m;
    }
}
