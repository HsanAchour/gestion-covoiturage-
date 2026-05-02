<?php
/**
 * Contrôleur messages : /api/messages, /api/messages/conversation.
 * Équivalent PHP de com.ihecride.controllers.MessageController.
 */

require_once __DIR__ . '/../dao/MessageDAO.php';
require_once __DIR__ . '/../models/Message.php';

class MessageController {

    private MessageDAO $dao;

    public function __construct() {
        $this->dao = new MessageDAO();
    }

    public function conversation(array $ctx): void {
        $other = (int) ($_GET['userId'] ?? 0);
        $list  = $this->dao->findConversation($ctx['userId'], $other);
        $this->dao->marquerLu($ctx['userId'], $other);
        json_send(['success' => true, 'data' => $list]);
    }

    public function send(array $ctx): void {
        $m = new Message();
        $m->expediteurId   = $ctx['userId'];
        $m->destinataireId = (int) ($_POST['destinataireId'] ?? 0);
        $m->contenu        = $_POST['contenu'] ?? '';
        $id = $this->dao->insert($m);
        json_send(['success' => $id > 0, 'id' => $id]);
    }
}
